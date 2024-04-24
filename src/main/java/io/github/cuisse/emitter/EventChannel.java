package io.github.cuisse.emitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.StampedLock;

/**
 * @author Brayan Roman
 * @since  1.0.0
 */
public class EventChannel<T> {

    private final Comparator<Subscriber<T>> comparator;
    private final List<Subscriber<T>> subscribers = new ArrayList<>();
    private final StampedLock lock = new StampedLock();

    public EventChannel(Comparator<Subscriber<T>> comparator) {
        this.comparator = comparator;
    }

    public boolean active() {
        long stamp = lock.readLock();
        try {
            return subscribers.size() > 0;
        } finally {
            lock.unlockRead(stamp);
        }
    }

    public void subscribe(Subscriber<T> subscriber) {
        long stamp = lock.writeLock();
        try {
            subscribers.add(subscriber);
            if (comparator != null) {
                subscribers.sort(comparator);
            }
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void unsubscribe(List<Subscriber<T>> subscribers) {
        long stamp = lock.writeLock();
        try {
            this.subscribers.removeAll(subscribers);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void unsubscribe(Subscriber<T> subscriber) {
        long stamp = lock.writeLock();
        try {
            subscribers.removeIf(value -> value.equals(subscriber));
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public void unsubscribeAll() {
        long stamp = lock.writeLock();
        try {
            subscribers.clear();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    public NotificationReceipt notify(T event, SubscriberErrorResolver resolver) {
        long stamp = lock.readLock();
        List<Subscriber<T>> removed = null;
        try {
            if (subscribers.isEmpty()) {
                return new NotificationReceipt(0, null, NotificationReceipt.Status.SUCCESS_NO_SUBSCRIBERS);
            } else {
                var context = new SubscriberContext(subscribers.get(0).channel());
                var status  = NotificationReceipt.Status.SUCCESS;
                for (Subscriber<T> subscriber : subscribers) {
                    try {
                        subscriber.handler().accept(event, context);
                        if (context.unsubscribe().compareAndSet(true, false)) {
                            if (removed == null) {
                                removed = new ArrayList<>();
                            }
                            removed.add(subscriber);
                        }
                    } catch (Throwable error) {
                        if (resolver != null) {
                            if (resolver.resolve(error, context) == false) { // false = stop propagation
                                status = NotificationReceipt.Status.FAILED;
                                break;
                            }
                        } else {
                            throw error;
                        }
                    }
                }
                return new NotificationReceipt(removed != null ? removed.size() : 0, context.output().get(), status);
            }
        } finally {
            lock.unlockRead(stamp);
            if (removed != null) {
                unsubscribe(removed);
            }
        }
    }

}
