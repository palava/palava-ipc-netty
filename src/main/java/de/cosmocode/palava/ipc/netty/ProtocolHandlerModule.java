package de.cosmocode.palava.ipc.netty;

import java.lang.annotation.Annotation;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import de.cosmocode.palava.ipc.protocol.Protocol;

/**
 * Module for binding {@link ProtocolHandler}.
 *
 * @since 1.0
 * @author Willi Schoenborn
 */
public final class ProtocolHandlerModule extends PrivateModule {

    private final TypeLiteral<Iterable<Protocol>> literal = new TypeLiteral<Iterable<Protocol>>() { };
    
    private final Class<? extends Annotation> annotation;

    @Inject
    public ProtocolHandlerModule(Class<? extends Annotation> annotation) {
        this.annotation = Preconditions.checkNotNull(annotation, "Annotation");
    }

    @Override
    protected void configure() {
        bind(literal).to(Key.get(literal, annotation)).in(Singleton.class);
        bind(ProtocolHandler.class).annotatedWith(annotation).to(ProtocolHandler.class).in(Singleton.class);
        expose(ProtocolHandler.class).annotatedWith(annotation);
    }

}
