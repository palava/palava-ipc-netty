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

import java.util.concurrent.ExecutorService;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.socket.ServerSocketChannel;
import org.jboss.netty.channel.socket.ServerSocketChannelFactory;
import org.jboss.netty.channel.socket.oio.OioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * Configurable oio {@link ServerSocketChannelFactory} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ConfigurableOioServerSocketChannelFactory implements ServerChannelFactory, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableOioServerSocketChannelFactory.class);

    private final ServerSocketChannelFactory factory;

    @Inject
    ConfigurableOioServerSocketChannelFactory(
        @Boss ExecutorService boss,
        @Worker ExecutorService worker) {
        
        Preconditions.checkNotNull(boss, "Boss");
        Preconditions.checkNotNull(worker, "Worker");
        LOG.trace("Configuring {} with boss {} and worker {}", new Object[] {
            this, boss, worker
        });
        this.factory = new OioServerSocketChannelFactory(boss, worker);
    }
    
    @Override
    public ServerSocketChannel newChannel(ChannelPipeline pipeline) {
        return factory.newChannel(pipeline);
    }

    @Override
    public void releaseExternalResources() {
        factory.releaseExternalResources();
    }
    
    @Override
    public void dispose() throws LifecycleException {
        releaseExternalResources();
    }

}
