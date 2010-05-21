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

import java.net.SocketAddress;
import java.util.UUID;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.local.LocalAddress;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.PrivateModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import de.cosmocode.palava.concurrent.DefaultThreadProviderModule;
import de.cosmocode.palava.concurrent.ExecutorModule;
import de.cosmocode.palava.core.DefaultRegistryModule;
import de.cosmocode.palava.core.inject.TypeConverterModule;
import de.cosmocode.palava.core.lifecycle.LifecycleModule;
import de.cosmocode.palava.jmx.FakeMBeanServerModule;

/**
 * Tests module.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class NettyTestModule2 implements Module {

    @Override
    public void configure(Binder binder) {
        binder.install(new LifecycleModule());
        binder.install(new TypeConverterModule());
        binder.install(new DefaultRegistryModule());
        binder.install(new DefaultThreadProviderModule());
        binder.install(new FakeMBeanServerModule());
        
        binder.install(new PrivateModule() {
            
            @Override
            protected void configure() {
                install(new ExecutorModule(Boss.class, "json-boss"));
                install(new ExecutorModule(Worker.class, "json-worker"));
                bind(SocketAddress.class).annotatedWith(Names.named("json." + NettyServiceConfig.ADDRESS)).toInstance(
                    new LocalAddress(UUID.randomUUID().toString()));
                install(new LocalServerChannelFactoryModule());
                install(new ChannelPipelineFactoryModule());
                install(NettyServiceModule.named("json").overrideOptionals());
            }
            
        });
        
        binder.install(new PrivateModule() {
            
            @Override
            protected void configure() {
                install(new ExecutorModule(Boss.class, "xml-boss"));
                install(new ExecutorModule(Worker.class, "xml-worker"));
                install(new NioServerSocketChannelFactoryModule());
                install(new ChannelPipelineFactoryModule());
                install(NettyServiceModule.named("xml"));
            }

        });
    }

    /**
     * Provides a channel pipeline for testing.
     * 
     * @since 1.0
     * @param handler the echo handler
     * @return a new {@link ChannelPipeline}
     */
    @Provides
    ChannelPipeline provideChannelPipeline(EchoHandler handler) {
        return Channels.pipeline(handler);
    }

}
