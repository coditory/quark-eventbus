package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

public interface EventBus extends EventEmitter {
    @NotNull
    static EventBus create() {
        return new EventBusBuilder().build();
    }

    @NotNull
    static EventBusBuilder builder() {
        return new EventBusBuilder();
    }

    @NotNull
    String getName();

    void subscribe(@NotNull Subscription<?> listener);

    void unsubscribe(@NotNull Subscription<?> listener);

    default <T> void subscribe(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        subscribe(Subscription.of(eventType, listener));
    }

    default void subscribe(@NotNull Object listener) {
        Subscription.of(listener)
                .forEach(this::subscribe);
    }

    default <T> void unsubscribe(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        unsubscribe(Subscription.of(eventType, listener));
    }

    default void unsubscribe(@NotNull Object listener) {
        Subscription.of(listener)
                .forEach(this::unsubscribe);
    }
}
