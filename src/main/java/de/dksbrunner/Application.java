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
package de.dksbrunner;

import de.dksbrunner.business.Contract;
import de.dksbrunner.persistence.model.ContractEntity;
import de.dksbrunner.persistence.model.CustomerEntity;
import de.dksbrunner.persistence.model.ProductEntity;
import de.dksbrunner.persistence.service.ContractPersistence;
import de.dksbrunner.persistence.service.ContractRelations;
import de.dksbrunner.persistence.service.LoadingType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;

import java.util.List;

/**
 * @author Daniel Brunner
 */
@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private R2dbcEntityOperations entityOperations;

    @Autowired
    private ContractPersistence contractPersistence;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        ContractEntity contractEntity = createContract(
                "40030-1000", createCustomer("John Doe"), createProduct("Boomercringe"));

        ContractRelations relations_0 = ContractRelations.builder()
                .customer(LoadingType.NONE)
                .product(LoadingType.NONE)
                .build();
        loadContractsByCustomerId(relations_0, contractEntity.getCustomerId());

        ContractRelations relations_1 = ContractRelations.builder()
                .customer(LoadingType.EAGER)
                .product(LoadingType.NONE)
                .build();
        loadContractsByCustomerId(relations_1, contractEntity.getCustomerId());

        ContractRelations relations_2 = ContractRelations.builder()
                .customer(LoadingType.EAGER)
                .product(LoadingType.EAGER)
                .build();
        loadContractsByCustomerId(relations_2, contractEntity.getCustomerId());
    }

    private ProductEntity createProduct(String name) {
        ProductEntity productEntity = ProductEntity.builder()
                .name(name)
                .build();
        return entityOperations.insert(productEntity).block();
    }

    private CustomerEntity createCustomer(String name) {
        CustomerEntity customerEntity = CustomerEntity.builder()
                .name(name)
                .build();
        return entityOperations.insert(customerEntity).block();
    }

    private ContractEntity createContract(String reference, CustomerEntity customerEntity, ProductEntity productEntity) {
        ContractEntity contractEntity = ContractEntity.builder()
                .reference(reference)
                .customerId(customerEntity.getId())
                .productId(productEntity.getId())
                .build();
        return entityOperations.insert(contractEntity).block();
    }

    private void loadContractsByCustomerId(ContractRelations relations, long customerId) {
        List<Contract> contracts = contractPersistence.findAllByCustomerId(relations, customerId)
                .collectList()
                .block();
        log.info("Contracts: {}", contracts);
    }
}
