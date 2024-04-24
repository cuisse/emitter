package io.github.cuisse.emitter;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Subscriber.
 * 
 * @author Brayan Roman
 * @since  1.0.0
 */
public record Subscriber<T>(String channel, Class<T> event, int priority, BiConsumer<T, SubscriberContext> handler) implements Comparable<Subscriber<T>> {
  
    public Subscriber(String channel, Class<T> event, BiConsumer<T, SubscriberContext> handler) {
        this(channel, event, 0, handler);
    }

    public Subscriber(String channel, Class<T> event, Consumer<T> handler) {
        this(channel, event, 0, handler);
    }

    public Subscriber(String channel, Class<T> event, int priority, Consumer<T> handler) {
        this(channel, event, priority, (value, pipeline) -> {
            handler.accept(value);
        });
    }

    @Override
    public int compareTo(Subscriber<T> properties) {
        return Integer.compare(priority, properties.priority());
    }

}
