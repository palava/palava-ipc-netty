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

import java.util.concurrent.ConcurrentMap;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

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
final class DefaultConnectionManager extends SimpleChannelHandler implements ConnectionManager {
    
    private final ConcurrentMap<Channel, DetachedConnection> connections = new MapMaker().makeMap();
    
    private final IpcConnectionCreateEvent createEvent;
    
    private final IpcConnectionDestroyEvent destroyEvent;
    
    @Inject
    DefaultConnectionManager(
        @Proxy IpcConnectionCreateEvent createEvent,
        @SilentProxy IpcConnectionDestroyEvent destroyEvent) {
        this.createEvent = Preconditions.checkNotNull(createEvent, "CreateEvent");
        this.destroyEvent = Preconditions.checkNotNull(destroyEvent, "DestroyEvent");
    }
    
    @Override
    public void channelConnected(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final Channel channel = event.getChannel();
        final DetachedConnection connection = new ChannelConnection(channel);
        connections.put(channel, connection);
        createEvent.eventIpcConnectionCreate(connection);
    }
    
    @Override
    public DetachedConnection get(Channel channel) {
        final DetachedConnection connection = connections.get(channel);
        Preconditions.checkState(connection != null, "No connection set for channel %s", channel);
        return connection;
    }
    
    @Override
    public void channelClosed(ChannelHandlerContext context, ChannelStateEvent event) throws Exception {
        final Channel channel = event.getChannel();
        final DetachedConnection connection = connections.remove(channel);
        destroyEvent.eventIpcConnectionDestroy(connection);
        connection.clear();
    }
    
}
