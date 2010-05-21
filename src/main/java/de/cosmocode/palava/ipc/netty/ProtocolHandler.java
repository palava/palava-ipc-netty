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

import java.util.NoSuchElementException;

import javax.annotation.concurrent.ThreadSafe;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;

import de.cosmocode.palava.ipc.protocol.DetachedConnection;
import de.cosmocode.palava.ipc.protocol.Protocol;
import de.cosmocode.palava.ipc.protocol.ProtocolException;

/**
 * A {@link ChannelHandler} implementation which delegates to
 * configured {@link Protocol}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
@Sharable
@ThreadSafe
public final class ProtocolHandler extends SimpleChannelHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ProtocolHandler.class);
    
    private final ConnectionManager manager;
    
    private final Iterable<Protocol> protocols;

    @Inject
    public ProtocolHandler(ConnectionManager manager, Iterable<Protocol> protocols) {
        this.manager = Preconditions.checkNotNull(manager, "Manager");
        this.protocols = Preconditions.checkNotNull(protocols, "Protocols");
    }
    
    @Override
    public void messageReceived(ChannelHandlerContext context, MessageEvent event) throws Exception {
        final Object request = event.getMessage();
        final Channel channel = event.getChannel();
        
        final Protocol protocol = findProtocol(request);
        final DetachedConnection connection = manager.get(channel);
        final Object response = process(protocol, request, connection);
        
        if (response == Protocol.NO_RESPONSE) {
            LOG.trace("Omitting response as requested by {}", protocol);
        } else {
            LOG.trace("Writing response {} to channel", response);
            channel.write(response);
        }
    }
    
    private Protocol findProtocol(Object request) {
        for (Protocol protocol : protocols) {
            if (protocol.supports(request)) return protocol;
        }
        throw new NoSuchElementException("No protocol found which can handle " + request);
    }

    private Object process(Protocol protocol, Object request, DetachedConnection connection) {
        try {
            LOG.trace("Processing request of type {} using {}", request.getClass(), protocol);
            return protocol.process(request, connection);
        } catch (ProtocolException e) {
            LOG.warn("Error in protocol", e);
            return protocol.onError(e, request);
        /* CHECKSTYLE:OFF */
        } catch (RuntimeException e) {
        /* CHECKSTYLE:ON */
            LOG.error("Unexpected exception in protocol", e);
            return protocol.onError(e, request);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext context, ExceptionEvent event) throws Exception {
        final Channel channel = event.getChannel();
        LOG.error("Exception in channel " + channel, event.getCause());
        channel.close();
    }

}
