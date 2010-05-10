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
            public synchronized int read() throws IOException {
                if (buffer.readable()) {
                    return buffer.readByte();
                } else {
                    return -1;
                }
            }
            
            @Override
            public synchronized int read(byte[] b, int off, int len) throws IOException {
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
                return String.format("ChannelBuffers.asInputStream(%s)", buffer);
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
            public synchronized void write(int b) throws IOException {
                buffer.writeByte(b);
            }
            
            @Override
            public synchronized void write(byte[] b, int off, int len) throws IOException {
                buffer.writeBytes(b, off, len);
            }
            
            @Override
            public String toString() {
                return String.format("ChannelBuffers.asOutputStream(%s)", buffer);
            }
            
        };
    }
    
}
