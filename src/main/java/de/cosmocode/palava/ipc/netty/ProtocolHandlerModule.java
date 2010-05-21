/**
 * Copyright 2010 CosmoCode GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.cosmocode.palava.ipc.netty;

import java.lang.annotation.Annotation;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.protocol.Protocol;

/**
 * Module for binding {@link ProtocolHandler}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class ProtocolHandlerModule extends PrivateModule {

    private final TypeLiteral<Iterable<Protocol>> literal = new TypeLiteral<Iterable<Protocol>>() { };
    
    private final Class<? extends Annotation> annotation;

    @Inject
    private ProtocolHandlerModule(Class<? extends Annotation> annotation) {
        this.annotation = Preconditions.checkNotNull(annotation, "Annotation");
    }

    @Override
    protected void configure() {
        bind(literal).to(Key.get(literal, annotation)).in(Singleton.class);
        bind(ProtocolHandler.class).annotatedWith(annotation).to(ProtocolHandler.class).in(Singleton.class);
        expose(ProtocolHandler.class).annotatedWith(annotation);
    }
    
    /**
     * Creates a Module which binds a {@link ProtocolHandler} using the specifed
     * {@link Annotation} type. The bound protocol handler depends on an {@link Iterable}
     * of {@link Protocol}s bound with the same annotation.
     * 
     * @since 1.0
     * @param annotation the binding annotation
     * @return a {@link Module} used for binding a {@link ProtocolHandler}
     * @throws NullPointerException if annotation is null
     */
    public static Module annotatedWith(Class<? extends Annotation> annotation) {
        return new ProtocolHandlerModule(annotation);
    }

}
