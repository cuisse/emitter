package io.github.cuisse.emitter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Subscriber context.
 * 
 * @param channel The channel where the subscriber is subscribed.
 * @param message A shared message between subscribers.
 * @param output  A shared output between subscribers.
 * @param unsubscribe  The remove flag of the subscriber.
 * 
 * @author Brayan Roman
 * @since  1.0.0
 */
public record SubscriberContext(String channel, AtomicReference<Object> message, AtomicReference<Object> output, AtomicBoolean unsubscribe) {
    public SubscriberContext(String channel) {
        this(channel, new AtomicReference<>(), new AtomicReference<>(), new AtomicBoolean(false));
    }
}
