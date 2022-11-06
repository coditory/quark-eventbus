package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import static com.coditory.quark.eventbus.Preconditions.expectNonNull;
import static com.coditory.quark.eventbus.Reflections.toShortString;

public final class AnnotatedSubscription<T> implements Subscription<T> {
    private final Object instance;
    private final Method method;
    private final Class<T> eventType;
    private boolean accessible = false;

    AnnotatedSubscription(@NotNull Object instance, @NotNull Method method, @NotNull Class<T> eventType) {
        this.instance = expectNonNull(instance, "instance");
        this.method = expectNonNull(method, "method");
        this.eventType = expectNonNull(eventType, "eventType");
    }

    @NotNull
    @Override
    public Class<T> getEventType() {
        return eventType;
    }

    @Override
    public void handle(@NotNull T event) throws Throwable {
        expectNonNull(event, "event");
        Object[] args = new Object[]{event};
        try {
            if (!accessible) {
                method.setAccessible(true);
                accessible = true;
            }
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Method became inaccessible: " + toShortString(method) + " Event: " + event, e);
        } catch (InvocationTargetException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }
            throw e;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotatedSubscription<?> that = (AnnotatedSubscription<?>) o;
        return Objects.equals(instance, that.instance)
                && Objects.equals(method, that.method)
                && Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, method, eventType);
    }

    @Override
    public String toString() {
        return "AnnotatedSubscription{" +
                "instance=" + instance +
                ", method=" + method +
                ", eventType=" + eventType +
                '}';
    }
}
