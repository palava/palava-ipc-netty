package de.cosmocode.palava.ipc.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

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
final class NettyClient extends AbstractClient {

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
    public Connection connect(InetSocketAddress address) {
        Preconditions.checkNotNull(address, "Address");
        final ChannelFuture future = bootstrap.connect(address);
        final Channel channel = future.awaitUninterruptibly().getChannel();
        return new InternalConnection(channel);
    }
    
    /**
     * Internal implementation of the {@link Connection} interface.
     *
     * @since 
     * @author Willi Schoenborn
     */
    private final class InternalConnection implements Connection {
     
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
                latch.await();
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
