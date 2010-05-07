package de.cosmocode.palava.ipc.netty;

import java.net.InetSocketAddress;

import com.google.common.base.Preconditions;

/**
 * Abstract implementation of the {@link Client} interface.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public abstract class AbstractClient implements Client {

    @Override
    public Connection connect(String host, int port) {
        Preconditions.checkNotNull(host, "Host");
        return connect(new InetSocketAddress(host, port));
    }

}
