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
package de.dksbrunner.web.controller;

import de.dksbrunner.business.Contract;
import de.dksbrunner.business.Customer;
import de.dksbrunner.business.Product;
import de.dksbrunner.persistence.service.CustomerPersistence;
import de.dksbrunner.persistence.service.CustomerRelations;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * @author Daniel Brunner
 */
@RestController("contract")
@RequiredArgsConstructor
public class GetCustomerController {

    private final CustomerPersistence persistence;

    @GetMapping(value = "api/customers/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<GetCustomerResponse> getCustomerByCustomerId(@PathVariable Long customerId) {
        return persistence.findByName(CustomerRelations.builder().build(), customerId)
                .map(this::mapToResponse);
    }

    private GetCustomerResponse mapToResponse(Customer customer) {
        return ImmutableGetCustomerResponse.builder()
                .payload(mapToCustomer(customer))
                .build();
    }

    private CustomerDto mapToCustomer(Customer customer) {
        return ImmutableCustomerDto.builder()
                .name(customer.getName())
                .contracts(customer.getContracts().stream()
                        .map(this::mapToContract)
                        .toList())
                .build();
    }

    private ContractDto mapToContract(Contract contract) {
        return ImmutableContractDto.builder()
                .reference(contract.getReference())
                .product(contract.getProduct()
                        .map(Product::getName)
                        .orElse(null))
                .build();
    }
}
