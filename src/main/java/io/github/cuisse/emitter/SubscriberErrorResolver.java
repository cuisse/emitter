package io.github.cuisse.emitter;

@FunctionalInterface
public interface SubscriberErrorResolver {

    boolean resolve(Throwable throwable, SubscriberContext pipeline);
    
}
