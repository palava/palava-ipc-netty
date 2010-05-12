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
import de.cosmocode.palava.core.inject.Config;
import de.cosmocode.palava.core.inject.RebindModule;

/**
 * Binds {@link Netty}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class NettyModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(Netty.class).asEagerSingleton();
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
        
        private final Config config;
        
        public NamedModule(String name) {
            this.name = name;
            this.config = new Config(name);
        }

        @Override
        protected void configuration() {
            bind(String.class).annotatedWith(Names.named(NettyConfig.NAME)).toInstance(name);

            bind(ChannelPipelineFactory.class).to(
                Key.get(ChannelPipelineFactory.class, Names.named(config.prefixed(NettyConfig.PIPELINE_FACTORY))));
            
            bind(InetSocketAddress.class).annotatedWith(Names.named(NettyConfig.ADDRESS)).to(
                Key.get(InetSocketAddress.class, Names.named(config.prefixed(NettyConfig.ADDRESS))));
        }
        
        @Override
        protected void optionals() {
            bind(int.class).annotatedWith(Names.named(NettyConfig.WORKER_COUNT)).to(
                Key.get(int.class, Names.named(config.prefixed(NettyConfig.WORKER_COUNT))));
            
            bind(String.class).annotatedWith(Names.named(NettyConfig.GROUP_NAME)).to(
                Key.get(String.class, Names.named(config.prefixed(NettyConfig.GROUP_NAME))));
            
            bind(Properties.class).annotatedWith(Names.named(NettyConfig.OPTIONS)).to(
                Key.get(Properties.class, Names.named(config.prefixed(NettyConfig.OPTIONS))));
            
            bind(long.class).annotatedWith(Names.named(NettyConfig.SHUTDOWN_TIMEOUT)).to(
                Key.get(long.class, Names.named(config.prefixed(NettyConfig.SHUTDOWN_TIMEOUT))));
            
            bind(TimeUnit.class).annotatedWith(Names.named(NettyConfig.SHUTDOWN_TIMEOUT_UNIT)).to(
                Key.get(TimeUnit.class, Names.named(config.prefixed(NettyConfig.SHUTDOWN_TIMEOUT_UNIT))));
        }
        
        @Override
        protected void bindings() {
            install(new NettyModule());
        }
        
        @Override
        protected void expose() {
            // nothing to expose
        }
        
    }
    
}
