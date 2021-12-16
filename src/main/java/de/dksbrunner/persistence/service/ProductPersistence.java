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

import de.dksbrunner.business.ImmutableProduct;
import de.dksbrunner.business.Product;
import de.dksbrunner.persistence.model.ProductEntity;
import de.dksbrunner.persistence.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Daniel Brunner
 */
@Component
@RequiredArgsConstructor
public class ProductPersistence {

    private final ProductRepository repository;

    public Mono<Product> findById(Long id) {
        return repository.findById(id)
                .map(this::mapToBusiness);
    }

    private Product mapToBusiness(ProductEntity productEntity) {
        return ImmutableProduct.builder()
                .name(productEntity.getName())
                .build();
    }
}
