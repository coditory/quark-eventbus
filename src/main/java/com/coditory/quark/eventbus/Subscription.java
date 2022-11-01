package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.coditory.quark.eventbus.Reflections.getAnnotatedMethods;
import static java.util.Collections.unmodifiableList;

public sealed interface Subscription<T> extends EventListener<T>
        permits EventHandlerSubscription, EventListenerSubscription {
    static <T> Subscription<T> fromEventListener(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        return new EventListenerSubscription<>(eventType, listener);
    }

    static List<Subscription<?>> fromEventHandlerMethods(@NotNull Object listener) {
        List<Subscription<?>> listeners = new ArrayList<>();
        for (Method method : getAnnotatedMethods(listener.getClass(), EventHandler.class)) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1 || parameterTypes[0].isPrimitive()) {
                throw new IllegalArgumentException("Expected single non-primitive parameter on @EventHandler method: " + method.getName());
            }
            Class<?> eventType = parameterTypes[0];
            Subscription<?> subscription = new EventHandlerSubscription<>(listener, method, eventType);
            listeners.add(subscription);
        }
        if (listeners.isEmpty()) {
            throw new IllegalArgumentException("Expected at least one method annotated with @EventHandler");
        }
        return unmodifiableList(listeners);
    }

    Class<? extends T> getEventType();
}
