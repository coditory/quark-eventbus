package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

public interface EventBus extends EventEmitter {
    String getName();

    void addSubscription(@NotNull Subscription<?> listener);

    void removeSubscription(@NotNull Subscription<?> listener);

    default <T> void addEventListener(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        addSubscription(Subscription.fromEventListener(eventType, listener));
    }

    default void addEventHandler(@NotNull Object listener) {
        Subscription.fromEventHandlerMethods(listener)
                .forEach(this::addSubscription);
    }

    default <T> void removeEventListener(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        removeSubscription(Subscription.fromEventListener(eventType, listener));
    }

    default void removeEventHandler(@NotNull Object listener) {
        Subscription.fromEventHandlerMethods(listener)
                .forEach(this::removeSubscription);
    }

    default void addUnhandledEventListener(@NotNull EventListener<UnhandledEvent> listener) {
        addEventListener(UnhandledEvent.class, listener);
    }

    default void removeUnhandledEventListener(@NotNull EventListener<UnhandledEvent> listener) {
        removeEventListener(UnhandledEvent.class, listener);
    }
}
