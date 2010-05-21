package de.cosmocode.palava.ipc.netty;

import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.google.inject.Inject;

import de.cosmocode.palava.core.Registry.Proxy;
import de.cosmocode.palava.core.Registry.SilentProxy;
import de.cosmocode.palava.ipc.IpcConnectionCreateEvent;
import de.cosmocode.palava.ipc.IpcConnectionDestroyEvent;
import de.cosmocode.palava.ipc.protocol.DetachedConnection;

/**
 * A {@link ConnectionManager} implementation using netty's {@link ChannelHandler}
 * event system.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class ConnectionChannelHandler extends SimpleChannelHandler implements ConnectionManager {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConnectionChannelHandler.class);
    
    private final ConcurrentMap<Channel, DetachedConnection> connections = new MapMaker().makeMap();
    
    private final IpcConnectionCreateEvent createEvent;
    
    private final IpcConnectionDestroyEvent destroyEvent;
    
    @Inject
    public ConnectionChannelHandler(
        @Proxy IpcConnectionCreateEvent createEvent,
        @SilentProxy IpcConnectionDestroyEvent destroyEvent) {
        this.createEvent = Preconditions.checkNotNull(createEvent, "CreateEvent");
        this.destroyEvent = Preconditions.checkNotNull(destroyEvent, "DestroyEvent");
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final Channel channel = event.getChannel();
        LOG.trace("Channel {} connected", channel);
        final DetachedConnection connection = new ChannelConnection(channel);
        connections.put(channel, connection);
        createEvent.eventIpcConnectionCreate(connection);
        context.sendUpstream(event);
    }
    
    @Override
    public DetachedConnection get(Channel channel) {
        Preconditions.checkNotNull(channel, "Channel");
        return connections.get(channel);
    }
    
    @Override
    public void channelClosed(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final Channel channel = event.getChannel();
        LOG.trace("Channel {} closed", channel);
        final DetachedConnection connection = connections.remove(channel);
        Preconditions.checkState(connection != null, "No connection was set for %s", channel);
        destroyEvent.eventIpcConnectionDestroy(connection);
        connection.clear();
        context.sendUpstream(event);
    }
    
}
