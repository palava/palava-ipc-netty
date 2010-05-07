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

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import de.cosmocode.palava.core.Framework;
import de.cosmocode.palava.core.Palava;

/**
 * Tests {@link NettyService}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class NettyServiceTest {

    /**
     * Tests booting {@link NettyService}.
     * 
     * @throws InterruptedException should not happen
     */
    @Test
    public void boot() throws InterruptedException {
        final Framework framework = Palava.newFramework();
        framework.start();
        
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        } finally {
            framework.stop();
        }
    }

}
