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
import de.dksbrunner.business.ImmutableContract;
import de.dksbrunner.persistence.model.ContractEntity;
import de.dksbrunner.persistence.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Daniel Brunner
 */
@Component
@RequiredArgsConstructor
public class ContractPersistence {

    private final ContractRepository contractRepository;

    private final ProductPersistence productPersistence;

    public Flux<Contract> findAllByCustomerId(ContractRelations relations, long customerId) {
        return contractRepository.findAllByCustomerId(customerId)
                .flatMap(contractEntity -> loadContractsWithRelations(relations, contractEntity));
    }

    private Mono<Contract> loadContractsWithRelations(ContractRelations relations, ContractEntity contractEntity) {
        return Mono.just(contractEntity)
                .map(this::mapToBusiness)
                .flatMap(contract -> loadProductIfNecessary(relations, contract, contractEntity.getProductId()));
    }

    private Mono<Contract> loadProductIfNecessary(ContractRelations relations, Contract contract, long productId) {
        return productPersistence.loadProductByStrategy(relations, productId)
                .map(product -> (Contract) ImmutableContract.copyOf(contract).withProduct(product))
                .defaultIfEmpty(contract);
    }

    private Contract mapToBusiness(ContractEntity entity) {
        return ImmutableContract.builder()
                .reference(entity.getReference())
                .build();
    }
}
