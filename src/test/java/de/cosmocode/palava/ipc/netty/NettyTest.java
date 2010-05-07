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

import org.junit.Assert;
import org.junit.Test;

import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;

/**
 * Tests {@link Netty}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class NettyTest {

    /**
     * Tests booting {@link Netty}.
     */
    @Test
    public void boot() {
        final Framework framework = Palava.newFramework();
        framework.start();

        final Client client = new NettyClient();
        final Connection connection = client.connect("localhost", 8081);
        
        
        try {
            Assert.assertEquals("test", connection.send("test"));
            Assert.assertEquals(getClass().toString(), connection.send(getClass().toString()));
        } finally {
            connection.disconnect();
            client.shutdown();
            framework.stop();
        }
    }

}
