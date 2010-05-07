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
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;

import de.cosmocode.palava.core.inject.AbstractRebindingModule;
import de.cosmocode.palava.core.inject.RebindModule;

/**
 * Binds {@link NettyService}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class NettyServiceModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(NettyService.class).asEagerSingleton();
    }
    
    public static RebindModule named(String name) {
        Preconditions.checkNotNull(name, "Name");
        return annotatedWith(Names.named(name), name);
    }
    
    public static RebindModule annotatedWith(final Annotation annotation, final String name) {
        return null;
    }
    
    public static RebindModule annotatedWith(final Class<? extends Annotation> annotation, final String name) {
        return new AbstractRebindingModule() {
            
            @Override
            protected void configuration() {
                binder().bind(String.class).annotatedWith(Names.named(NettyServiceConfig.NAME)).toInstance(name);

                
            }
            
            @Override
            protected void optionals() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            protected void bindings() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            protected void expose() {
                // TODO Auto-generated method stub
                
            }
            
        };
    }

}
