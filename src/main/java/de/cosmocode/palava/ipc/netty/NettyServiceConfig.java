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

/**
 * Static constant holder class for netty config key names.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
final class NettyServiceConfig {

    public static final String PREFIX = "netty.";
    
    public static final String NAME = PREFIX + "name";
    
    public static final String PIPELINE_FACTORY = PREFIX + "pipelineFactory";
    
    public static final String ADDRESS = PREFIX + "address";
    
    public static final String OPTIONS = PREFIX + "options";
    
    public static final String GROUP_NAME = PREFIX + "groupName";
    
    public static final String SHUTDOWN_TIMEOUT = PREFIX + "shutdownTimeout";
    
    public static final String SHUTDOWN_TIMEOUT_UNIT = PREFIX + "shutdownTimeoutUnit";
    
    private NettyServiceConfig() {
        
    }

}
