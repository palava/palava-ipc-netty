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
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;

/**
 * Configurable nio {@link ServerSocketChannelFactory} implementation.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ConfigurableNioServerSocketChannelFactory implements ServerChannelFactory, Initializable, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurableNioServerSocketChannelFactory.class);
    
    private final ExecutorService boss;
    
    private final ExecutorService worker;
    
    private int workerCount = Runtime.getRuntime().availableProcessors() * 2;

    private ServerSocketChannelFactory factory;

    @Inject
    public ConfigurableNioServerSocketChannelFactory(
        @Boss ExecutorService boss,
        @Worker ExecutorService worker) {
        this.boss = Preconditions.checkNotNull(boss, "Boss");
        this.worker = Preconditions.checkNotNull(worker, "Worker");
    }
    
    @Inject(optional = true)
    void setWorkerCount(@Named(NettyServiceConfig.WORKER_COUNT) int workerCount) {
        this.workerCount = workerCount;
    }
    
    @Override
    public void initialize() throws LifecycleException {
        LOG.trace("Configuring {} with boss {}, worker {} and worker count {}", new Object[] {
            this, boss, worker, workerCount
        });
        this.factory = new NioServerSocketChannelFactory(boss, worker, workerCount);
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
