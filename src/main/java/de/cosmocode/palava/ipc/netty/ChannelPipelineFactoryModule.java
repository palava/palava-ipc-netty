package de.cosmocode.palava.ipc.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;

/**
 * Binds {@link ChannelPipelineFactory} in {@link Singleton} backed by a {@link Provider}
 * of {@link ChannelPipeline}s.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class ChannelPipelineFactoryModule implements Module {

    @Override
    public void configure(Binder binder) {

    }

    /**
     * Provides a channel pipeline factory.
     * 
     * @since 1.0
     * @param provider provider for the underlying pipeline
     * @return a {@link ChannelPipelineFactory}
     */
    @Provides
    @Singleton
    ChannelPipelineFactory providerChannelPipelineFactory(final Provider<ChannelPipeline> provider) {
        return new ChannelPipelineFactory() {
            
            @Override
            public ChannelPipeline getPipeline() {
                return provider.get();
            }
            
        };
    }

}
