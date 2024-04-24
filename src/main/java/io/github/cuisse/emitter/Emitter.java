package io.github.cuisse.emitter;

import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Event emitter implementation.
 *
 * @author Brayan Roman
 * @since  1.0.0
 */
public class Emitter<T> {

    private final Map<String, Map<Class<?>, EventChannel<T>>> registries = new ConcurrentHashMap<>();

    public int size() {
        return registries.size();
    }

    public<E extends T> Subscriber<E> subscribe(Subscriber<E> subscriber) {
        registries.computeIfAbsent(subscriber.channel(), (key) -> {
            return new ConcurrentHashMap<>();
        }).computeIfAbsent(subscriber.event(), (clazz) -> new EventChannel<>(Comparator.comparingInt(Subscriber::priority))).subscribe(cast(subscriber));
        return subscriber;
    }

    public<E extends T> void unsubscribe(Subscriber<E> subscriber) {
        var channel = registries.getOrDefault(subscriber.channel(), Map.of()).get(subscriber.event());
        if (channel != null) {
            channel.unsubscribe(cast(subscriber));
            if (channel.active() == false) {
                clean(subscriber.channel(), subscriber.event());
            }
        }
    }

    public<E extends T> Object emit(String index, E event) {
        return emit(index, event.getClass(), event, null);
    }

    public<E extends T> Object emit(String index, E event, SubscriberErrorResolver resolver) {
        return emit(index, event.getClass(), event, resolver);
    }

    public<E extends T> NotificationReceipt emit(String index, Class<?> clazz, E event, SubscriberErrorResolver resolver) {
        if (clazz.isInstance(event)) {
            var channel = registries.getOrDefault(index, Map.of()).get(clazz);
            if (channel != null) {
                try {
                    return channel.notify(event, resolver);
                } finally {
                    if (channel.active() == false) {
                        clean(index, clazz);
                    }
                }
            }
            return new NotificationReceipt(0, null, NotificationReceipt.Status.FAILED_CHANNEL_NOT_FOUND);
        } else {
            throw new IllegalArgumentException("event is not an instance of the event class");
        }
    }

    private void clean(String id, Class<?> event) {
        var registry = registries.get(id);
        if (registry != null) {
            var channel = registry.get(event);
            if (channel != null) {
                if (channel.active() == false) {
                    registry.remove(event);
                }
            }
            if (registry.isEmpty()) {
                registries.remove(id);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected<E extends T> Subscriber<T> cast(Subscriber<E> event) {
        return (Subscriber<T>) event;
    }

}
