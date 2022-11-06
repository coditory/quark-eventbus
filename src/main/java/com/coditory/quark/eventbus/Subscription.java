package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.coditory.quark.eventbus.Reflections.getAnnotatedMethods;
import static com.coditory.quark.eventbus.Reflections.toShortString;
import static java.util.Collections.unmodifiableList;

public sealed interface Subscription<T> extends EventListener<T>
        permits AnnotatedSubscription, EventListenerSubscription {
    static <T> Subscription<T> of(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        return new EventListenerSubscription<>(eventType, listener);
    }

    static List<Subscription<?>> of(@NotNull Object listener) {
        List<Subscription<?>> listeners = new ArrayList<>();
        for (Method method : getAnnotatedMethods(listener.getClass(), EventHandler.class)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IllegalArgumentException("Expected single parameter on @EventHandler method: " + toShortString(method));
            }
            if (parameterTypes[0].isPrimitive()) {
                throw new IllegalArgumentException("Expected single non-primitive parameter on @EventHandler method: " + toShortString(method));
            }
            if (!(method.getGenericParameterTypes()[0] instanceof Class<?>)) {
                throw new IllegalArgumentException("Expected single non-generic parameter on @EventHandler method: " + toShortString(method));
            }
            Class<?> eventType = parameterTypes[0];
            Subscription<?> subscription = new AnnotatedSubscription<>(listener, method, eventType);
            listeners.add(subscription);
        }
        if (listeners.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one method annotated with @EventHandler");
        }
        return unmodifiableList(listeners);
    }

    @NotNull
    Class<? extends T> getEventType();
}
