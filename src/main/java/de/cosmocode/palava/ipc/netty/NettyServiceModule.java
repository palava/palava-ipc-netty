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

import java.net.InetSocketAddress;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelPipelineFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Binder;
import com.google.inject.Key;
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
    
    /**
     * Creates a rebinding module rebind netty configuration using the specified
     * name as a prefix.
     * 
     * @since 1.0
     * @param name the prefix/name
     * @return a {@link RebindModule}
     * @throws NullPointerException if name is null
     */
    public static RebindModule named(String name) {
        Preconditions.checkNotNull(name, "Name");
        return new NamedModule(name);
    }
    
    /**
     * Named rebing module for netty.
     *
     * @since 1.0
     * @author Willi Schoenborn
     */
    private static final class NamedModule extends AbstractRebindingModule {

        private final String name;
        
        public NamedModule(String name) {
            this.name = name;
        }

        @Override
        protected void configuration() {
            bind(String.class).annotatedWith(Names.named(NettyServiceConfig.NAME)).toInstance(name);

            bind(ChannelPipelineFactory.class).to(
                Key.get(ChannelPipelineFactory.class, Names.named(name + NettyServiceConfig.PIPELINE_FACTORY)));
            
            bind(InetSocketAddress.class).annotatedWith(Names.named(NettyServiceConfig.ADDRESS)).to(
                Key.get(InetSocketAddress.class, Names.named(name + NettyServiceConfig.ADDRESS)));
        }
        
        @Override
        protected void optionals() {
            bind(String.class).annotatedWith(Names.named(NettyServiceConfig.GROUP_NAME)).to(
                Key.get(String.class, Names.named(name + NettyServiceConfig.GROUP_NAME)));
            
            bind(Properties.class).annotatedWith(Names.named(NettyServiceConfig.OPTIONS)).to(
                Key.get(Properties.class, Names.named(name + NettyServiceConfig.OPTIONS)));
            
            bind(long.class).annotatedWith(Names.named(NettyServiceConfig.SHUTDOWN_TIMEOUT)).to(
                Key.get(long.class, Names.named(name + NettyServiceConfig.SHUTDOWN_TIMEOUT)));
            
            bind(TimeUnit.class).annotatedWith(Names.named(NettyServiceConfig.SHUTDOWN_TIMEOUT_UNIT)).to(
                Key.get(TimeUnit.class, Names.named(name + NettyServiceConfig.SHUTDOWN_TIMEOUT_UNIT)));
        }
        
        @Override
        protected void bindings() {
            install(new NettyServiceModule());
        }
        
        @Override
        protected void expose() {
            // nothing to expose
        }
        
    }
    
}
