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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.netty.buffer.ChannelBuffer;

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
     * @param buffer the underlying channel buffer
     * @return an inputstream which reads from the given channel buffer
     * @throws NullPointerException if buffer is null
     */
    public static InputStream asInputStream(final ChannelBuffer buffer) {
        Preconditions.checkNotNull(buffer, "Buffer");
        return new InputStream() {
            
            @Override
            public int read() throws IOException {
                if (buffer.readable()) {
                    return buffer.readByte();
                } else {
                    return -1;
                }
            }
            
            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (buffer.readable()) {
                    final int length = Math.min(len, buffer.readableBytes());
                    buffer.readBytes(b, off, length);
                    return length;
                } else {
                    return -1;
                }
            }
            
            @Override
            public String toString() {
                return String.format("ChannelBuffering.asInputStream(%s)", buffer);
            }
            
        };
    }

    /**
     * Adapts a {@link ChannelBuffer} to an {@link OutputStream}.
     * 
     * @param buffer the underlying channel buffer
     * @return an outputstream which writes to the given channel buffer
     * @throws NullPointerException if buffer is null
     */
    public static OutputStream asOutputStream(final ChannelBuffer buffer) {
        Preconditions.checkNotNull(buffer, "Buffer");
        return new OutputStream() {
            
            @Override
            public void write(int b) throws IOException {
                buffer.writeByte(b);
            }
            
            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                buffer.writeBytes(b, off, len);
            }
            
            @Override
            public String toString() {
                return String.format("ChannelBuffering.asOutputStream(%s)", buffer);
            }
            
        };
    }
    
}
