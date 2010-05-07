package de.cosmocode.palava.ipc.netty;

/**
 * A connected socket.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public interface Connection {
    
    /**
     * Sends the specified string and notifies the given callback asynchronously.
     * 
     * @since 1.0
     * @param request the request to be sent
     * @return the response
     * @throws NullPointerException if request is null
     * @throws IllegalStateException if not connected
     */
    String send(String request);
    
    /**
     * Disconnects this connection.
     * 
     * @since 1.0
     * @throws IllegalStateException if disconnect failed
     */
    void disconnect();

}
