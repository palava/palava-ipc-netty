package de.cosmocode.palava.ipc.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;

import de.cosmocode.palava.ipc.protocol.DetachedConnection;

/**
 * {@link ChannelHandler} extension which maps {@link Channel}s
 * to {@link DetachedConnection}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public interface ConnectionManager extends ChannelHandler {

    /**
     * Provides the connection for the specified channel.
     * 
     * @since 1.0
     * @param channel the current channel
     * @return the connection associated with the specified channel
     * @throws NullPointerException if channel is null
     */
    DetachedConnection get(Channel channel);
    
}
