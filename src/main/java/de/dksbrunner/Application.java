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

import de.dksbrunner.persistence.model.ContractEntity;
import de.dksbrunner.persistence.model.CustomerEntity;
import de.dksbrunner.persistence.model.ProductEntity;
import de.dksbrunner.persistence.repository.CustomerRepository;
import de.dksbrunner.persistence.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import reactor.core.publisher.Mono;

/**
 * @author Daniel Brunner
 */
@Slf4j
@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private R2dbcEntityOperations entityOperations;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        createContract("40030-1000", getOrCreateCustomer("John Doe"), getOrCreateProduct("Boomercringe"));
        createContract("40030-2000", getOrCreateCustomer("John Doe"), getOrCreateProduct("Boomercringe"));
        createContract("40080-1000", getOrCreateCustomer("Jane Doe"), getOrCreateProduct("Boomercringe"));
    }

    private ProductEntity getOrCreateProduct(String name) {
        return productRepository.findByName(name)
                .switchIfEmpty(createProduct(name))
                .block();
    }

    private Mono<ProductEntity> createProduct(String name) {
        ProductEntity productEntity = ProductEntity.builder()
                .name(name)
                .build();
        return productRepository.save(productEntity);
    }

    private CustomerEntity getOrCreateCustomer(String name) {
        return customerRepository.findByName(name)
                .switchIfEmpty(createCustomer(name))
                .block();
    }

    private Mono<CustomerEntity> createCustomer(String name) {
        CustomerEntity customerEntity = CustomerEntity.builder()
                .name(name)
                .build();
        return customerRepository.save(customerEntity);
    }

    private ContractEntity createContract(String reference, CustomerEntity customerEntity, ProductEntity productEntity) {
        ContractEntity contractEntity = ContractEntity.builder()
                .reference(reference)
                .customerId(customerEntity.getId())
                .productId(productEntity.getId())
                .build();
        return entityOperations.insert(contractEntity).block();
    }
}
