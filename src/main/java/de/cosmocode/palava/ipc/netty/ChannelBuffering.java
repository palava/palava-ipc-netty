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

import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBufferOutputStream;

import com.google.common.base.Preconditions;

/**
 * Static utility class for {@link ChannelBuffer}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class ChannelBuffering {

    private ChannelBuffering() {
        
    }
    
    /**
     * Adapts a {@link ChannelBuffer} to an {@link InputStream}.
     * 
     * @deprecated use {@link ChannelBufferInputStream}
     * @param buffer the underlying channel buffer
     * @return an inputstream which reads from the given channel buffer
     * @throws NullPointerException if buffer is null
     */
    @Deprecated
    public static InputStream asInputStream(final ChannelBuffer buffer) {
        Preconditions.checkNotNull(buffer, "Buffer");
        return new ChannelBufferInputStream(buffer);
    }

    /**
     * Adapts a {@link ChannelBuffer} to an {@link OutputStream}.
     * 
     * @deprecated use {@link ChannelBufferOutputStream}
     * @param buffer the underlying channel buffer
     * @return an outputstream which writes to the given channel buffer
     * @throws NullPointerException if buffer is null
     */
    @Deprecated
    public static OutputStream asOutputStream(final ChannelBuffer buffer) {
        Preconditions.checkNotNull(buffer, "Buffer");
        return new ChannelBufferOutputStream(buffer);
    }
    
}
