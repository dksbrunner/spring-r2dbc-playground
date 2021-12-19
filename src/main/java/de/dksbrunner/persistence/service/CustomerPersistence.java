/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.dksbrunner.persistence.service;

import de.dksbrunner.business.Contract;
import de.dksbrunner.business.Customer;
import de.dksbrunner.business.ImmutableCustomer;
import de.dksbrunner.persistence.model.CustomerEntity;
import de.dksbrunner.persistence.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Daniel Brunner
 */
@Component
@RequiredArgsConstructor
public class CustomerPersistence {

    private final CustomerRepository repository;

    private final ContractPersistence contractPersistence;

    public Mono<Customer> findByName(CustomerRelations relations, long customerId) {
        return repository.findById(customerId)
                .flatMap(customerEntity -> loadCustomerWithRelations(relations, customerEntity));
    }

    private Mono<Customer> loadCustomerWithRelations(CustomerRelations relations, CustomerEntity customerEntity) {
        return Mono.just(customerEntity)
                .map(this::mapToBusiness)
                .flatMap(customer -> loadContractsIfNecessary(relations, customer, customerEntity.getId()));
    }

    private Mono<Customer> loadContractsIfNecessary(CustomerRelations relations, Customer customer, long customerId) {
        return loadContractsByStrategy(relations, customerId).collectList()
                .map(contracts -> (Customer) ImmutableCustomer.copyOf(customer).withContracts(contracts))
                .defaultIfEmpty(customer);
    }

    private Flux<Contract> loadContractsByStrategy(WithContractRelation relation, long customerId) {
        return switch (relation.getContract()) {
            case EAGER -> contractPersistence.findAllByCustomerId(ContractRelations.builder().build(), customerId);
            case NONE -> Flux.empty();
        };
    }

    private Customer mapToBusiness(CustomerEntity customerEntity) {
        return ImmutableCustomer.builder()
                .name(customerEntity.getName())
                .build();
    }
}
