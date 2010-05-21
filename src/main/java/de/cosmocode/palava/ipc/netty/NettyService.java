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
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import de.cosmocode.palava.core.Registry;
import de.cosmocode.palava.core.event.PostFrameworkStart;
import de.cosmocode.palava.core.event.PreFrameworkStop;
import de.cosmocode.palava.core.lifecycle.Disposable;
import de.cosmocode.palava.core.lifecycle.Initializable;
import de.cosmocode.palava.core.lifecycle.LifecycleException;
import de.cosmocode.palava.jmx.MBeanService;

/**
 * A service which handles a client/server protocol on a specific
 * port using netty. 
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class NettyService implements NettyServiceMBean, 
    Initializable, PostFrameworkStart, PreFrameworkStop, Disposable {

    private static final Logger LOG = LoggerFactory.getLogger(NettyService.class);
    
    private String name = "netty";
    
    private final ServerChannelFactory channelFactory;
    
    private final ChannelPipelineFactory pipelineFactory;
    
    private final Registry registry;
    
    private final MBeanService mBeanService;
    
    private final ChannelGroup group = new DefaultChannelGroup();
    
    private final InetSocketAddress address;
    
    private Map<Object, Object> options = Maps.newHashMap();
    
    private long shutdownTimeout = 30;
    
    private TimeUnit shutdownTimeoutUnit = TimeUnit.SECONDS;
    
    @Inject
    public NettyService(
        ServerChannelFactory factory,
        Registry registry,
        MBeanService mBeanService,
        ChannelPipelineFactory pipelineFactory,
        @Named(NettyServiceConfig.ADDRESS) InetSocketAddress address) {
        
        this.channelFactory = Preconditions.checkNotNull(factory, "ChannelFactory");
        this.pipelineFactory = Preconditions.checkNotNull(pipelineFactory, "PipelineFactory");
        this.registry = Preconditions.checkNotNull(registry, "Registry");
        this.mBeanService = Preconditions.checkNotNull(mBeanService, "MBeanService");
        this.address = Preconditions.checkNotNull(address, "Address");
    }
    
    @Inject(optional = true)
    void setName(@Named(NettyServiceConfig.NAME) String name) {
        this.name = Preconditions.checkNotNull(name, "Name");
    }
    
    @Inject(optional = true)
    void setOptions(@Named(NettyServiceConfig.OPTIONS) Properties options) {
        this.options = Preconditions.checkNotNull(options, "Options");
    }
    
    @Inject(optional = true)
    void setShutdownTimeout(@Named(NettyServiceConfig.SHUTDOWN_TIMEOUT) long shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }
    
    @Inject(optional = true)
    void setShutdownTimeoutUnit(@Named(NettyServiceConfig.SHUTDOWN_TIMEOUT_UNIT) TimeUnit shutdownTimeoutUnit) {
        this.shutdownTimeoutUnit = Preconditions.checkNotNull(shutdownTimeoutUnit, "ShutdownTimeoutUnit");
    }
    
    @Override
    public void initialize() throws LifecycleException {
        registry.register(PostFrameworkStart.class, this);
        registry.register(PreFrameworkStop.class, this);
        mBeanService.register(this, "name", name);
    }
    
    @Override
    public void eventPostFrameworkStart() {
        final ServerBootstrap bootstrap = new ServerBootstrap(channelFactory);
        
        for (Entry<Object, Object> entry : options.entrySet()) {
            LOG.trace("Setting option {}", entry);
            bootstrap.setOption(entry.getKey().toString(), entry.getValue());
        }
        
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                final ChannelPipeline pipeline = pipelineFactory.getPipeline();
                
                pipeline.addFirst("channel-add-handler", new SimpleChannelHandler() {

                    @Override
                    public void channelOpen(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
                        final Channel channel = context.getChannel();
                        LOG.info("Adding {} to group", channel);
                        group.add(channel);
                    }
                    
                });
                
                return pipeline;
            }
            
        });
        
        LOG.trace("Binding {} to {}", bootstrap, address);
        final Channel channel = bootstrap.bind(address);

        LOG.info("Adding server socket {} to group", channel);
        group.add(channel);
    }
    
    @Override
    public int getOpenConnections() {
        // server socket is no connection
        return group.size() - 1;
    }
    
    @Override
    public void eventPreFrameworkStop() {
        LOG.info("Waiting {} {} for connections to close", shutdownTimeout, shutdownTimeoutUnit.name().toLowerCase());
        group.close().awaitUninterruptibly(shutdownTimeout, shutdownTimeoutUnit);
    }
    
    @Override
    public void dispose() throws LifecycleException {
        try {
            mBeanService.unregister(this, "name", name);
        } finally {
            channelFactory.releaseExternalResources();
            registry.remove(this);
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s [%s]", NettyService.class.getSimpleName(), name);
    }

}
