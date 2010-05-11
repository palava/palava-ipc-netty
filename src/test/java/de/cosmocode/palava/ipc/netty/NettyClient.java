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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;

/**
 * Netty based implementation of the {@link Client} interface.
 *
 * @since 
 * @author Willi Schoenborn
 */
public final class NettyClient extends AbstractClient {

    private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);
    
    private final ChannelFactory factory = new NioClientSocketChannelFactory(
        Executors.newCachedThreadPool(),
        Executors.newCachedThreadPool()
    );
    
    private final ClientBootstrap bootstrap;
    
    private final ConcurrentMap<Channel, CountDownLatch> latches = new MapMaker().weakKeys().makeMap();
    private final ConcurrentMap<Channel, String> responses = new MapMaker().weakKeys().makeMap();
    
    public NettyClient() {
        this.bootstrap = new ClientBootstrap(factory);
        
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(
                    new StringDecoder(Charsets.UTF_8),
                    new SimpleChannelHandler() {

                        @Override
                        public void messageReceived(ChannelHandlerContext context, MessageEvent event) {
                            Assert.assertTrue(event.getMessage() instanceof String);
                            responses.put(event.getChannel(), event.getMessage().toString());
                            latches.get(event.getChannel()).countDown();
                        }

                    },
                    new StringEncoder(Charsets.UTF_8)
                );
            }
            
        });
    }
    
    @Override
    public ClientConnection connect(InetSocketAddress address) {
        Preconditions.checkNotNull(address, "Address");
        final ChannelFuture future = bootstrap.connect(address);
        final Channel channel = future.awaitUninterruptibly().getChannel();
        return new InternalConnection(channel);
    }
    
    /**
     * Internal implementation of the {@link ClientConnection} interface.
     *
     * @since 
     * @author Willi Schoenborn
     */
    private final class InternalConnection implements ClientConnection {
     
        private final Channel channel;
        
        public InternalConnection(Channel channel) {
            this.channel = channel;
        }
        
        @Override
        public String send(String request) {
            Preconditions.checkNotNull(request, "Request");
            final CountDownLatch latch = new CountDownLatch(1);
            latches.put(channel, latch);
            final ChannelFuture future = channel.write(request);
            future.awaitUninterruptibly();
            
            try {
                latch.await(30, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
            
            return responses.get(channel);
        }
        
        @Override
        public void disconnect() {
            final ChannelFuture future = channel.close().awaitUninterruptibly();
            LOG.trace("Channel {} closed", channel);
            Preconditions.checkState(future.isSuccess(), "%s", future.getCause());
        }
        
    }
    
    @Override
    public void shutdown() {
        factory.releaseExternalResources();
    }

}
