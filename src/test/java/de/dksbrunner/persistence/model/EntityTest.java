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
package de.dksbrunner.persistence.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Brunner
 */
@ExtendWith(MockitoExtension.class)
class EntityTest {

    @Spy
    private Entity cut;

    @Test
    void testIsNewReturnTrue() {
        when(cut.getId()).thenReturn(null);
        assertThat(cut.isNew()).isTrue();
    }

    @Test
    void testIsNewReturnFalse() {
        when(cut.getId()).thenReturn(1000L);
        assertThat(cut.isNew()).isFalse();
    }
}