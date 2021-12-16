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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * @author Daniel Brunner
 */
public enum LoadingType {
    EAGER, LAZY, NONE;

    public boolean isLazy() {
        return this == LAZY;
    }

    public boolean isEager() {
        return this == EAGER;
    }

    public <T> Mono<T> ifLazy(MonoSupplier<T> supplier) {
        throw new UnsupportedOperationException();
    }

    public <T> Mono<T> ifLazy(FluxSupplier<T> supplier) {
        throw new UnsupportedOperationException();
    }

    public <T> Mono<T> ifEager(MonoSupplier<T> supplier) {
        return isEager() ? supplier.get() : Mono.empty();
    }

    public <T> Flux<T> ifEager(FluxSupplier<T> supplier) {
        return isEager() ? supplier.get() : Flux.empty();
    }

    interface MonoSupplier<T> extends Supplier<Mono<T>> {
    }

    interface FluxSupplier<T> extends Supplier<Flux<T>> {
    }
}
