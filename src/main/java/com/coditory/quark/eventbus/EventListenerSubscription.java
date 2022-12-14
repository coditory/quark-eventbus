package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static com.coditory.quark.eventbus.Preconditions.expectNonNull;

public final class EventListenerSubscription<T> implements Subscription<T> {
    private final Class<? extends T> eventType;
    private final EventListener<T> listener;

    EventListenerSubscription(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        this.eventType = expectNonNull(eventType, "eventType");
        this.listener = expectNonNull(listener, "listener");
    }

    @NotNull
    @Override
    public Class<? extends T> getEventType() {
        return eventType;
    }

    @Override
    public void handle(@NotNull T event) throws Throwable {
        expectNonNull(event, "event");
        listener.handle(event);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventListenerSubscription<?> that = (EventListenerSubscription<?>) o;
        return Objects.equals(eventType, that.eventType)
                && Objects.equals(listener, that.listener);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventType, listener);
    }

    @Override
    public String toString() {
        return "EventListenerSubscription{" +
                "eventType=" + eventType +
                ", listener=" + listener +
                '}';
    }
}
