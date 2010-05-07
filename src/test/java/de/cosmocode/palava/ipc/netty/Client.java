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
