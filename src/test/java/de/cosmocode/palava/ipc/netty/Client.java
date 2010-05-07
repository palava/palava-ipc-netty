package de.cosmocode.palava.ipc.netty;

import java.net.InetSocketAddress;

/**
 * Simple client interface which allows small
 * socket communication for unit tests.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public interface Client {

    /**
     * Connects to the given host and port.
     * 
     * @since 1.0
     * @param host host name
     * @param port port
     * @return the new connection
     * @throws NullPointerException if host is null
     */
    Connection connect(String host, int port);
    
    /**
     * Connects to the given address.
     * 
     * @since 1.0
     * @param address the address
     * @return the new connection
     * @throws NullPointerException if address is null
     */
    Connection connect(InetSocketAddress address);
    
    /**
     * Shutsdown this client and releases resources.
     * 
     * @since 1.0
     */
    void shutdown();

}
