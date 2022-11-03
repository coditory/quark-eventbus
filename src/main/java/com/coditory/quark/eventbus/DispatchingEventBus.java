package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static java.util.Objects.requireNonNull;

final class DispatchingEventBus implements EventBus {
    private final String name;
    private final DispatchExceptionHandler exceptionHandler;
    private final ConcurrentMap<Class<?>, List<Class<?>>> eventsHierarchy = new ConcurrentHashMap<>();
    private final ConcurrentMap<Class<?>, Set<Subscription<Object>>> routes = new ConcurrentHashMap<>();

    DispatchingEventBus(@NotNull String name, @NotNull DispatchExceptionHandler exceptionHandler) {
        this.name = requireNonNull(name);
        this.exceptionHandler = requireNonNull(exceptionHandler);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void emit(@NotNull Object event) {
        requireNonNull(event);
        Set<Subscription<Object>> subscriptions = getSubscriptionsForEvent(event.getClass());
        if (subscriptions.isEmpty()) {
            emitUnhandledEvent(event);
        } else {
            subscriptions.forEach(subscription -> dispatch(event, subscription));
        }
    }

    private void emitUnhandledEvent(Object event) {
        Set<Subscription<Object>> subscriptions = getSubscriptionsForEvent(UnhandledEvent.class);
        if (!subscriptions.isEmpty()) {
            UnhandledEvent wrapped = new UnhandledEvent(event);
            subscriptions.forEach(subscription -> dispatch(wrapped, subscription));
        }
    }

    private Set<Subscription<Object>> getSubscriptionsForEvent(Class<?> eventType) {
        Set<Subscription<Object>> result = new LinkedHashSet<>();
        for (Class<?> eventSuperType : getEventHierarchy(eventType)) {
            result.addAll(routes.getOrDefault(eventSuperType, Set.of()));
        }
        return result;
    }

    private void dispatch(Object event, Subscription<Object> subscription) {
        try {
            subscription.handle(event);
        } catch (RuntimeException e) {
            DispatchExceptionContext context = new DispatchExceptionContext(e, event, subscription);
            exceptionHandler.handle(context);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void subscribe(@NotNull Subscription<?> subscription) {
        requireNonNull(subscription);
        routes.computeIfAbsent(subscription.getEventType(), (x) -> new CopyOnWriteArraySet<>())
                .add((Subscription<Object>) subscription);
    }

    @Override
    public void unsubscribe(@NotNull Subscription<?> listener) {
        requireNonNull(listener);
        getEventHierarchy(listener.getEventType())
                .forEach(clazz -> this.unregisterRoute(clazz, listener));
    }

    public void unregisterRoute(Class<?> eventType, Subscription<?> subscription) {
        routes.computeIfAbsent(eventType, (x) -> new CopyOnWriteArraySet<>())
                .remove(subscription);
    }

    private List<Class<?>> getEventHierarchy(Class<?> eventType) {
        return eventsHierarchy.computeIfAbsent(eventType, Reflections::getAllInterfacesAndClasses);
    }

    @Override
    public String toString() {
        int hashCode = this.hashCode();
        return "EventBus{name='" + name + "', hashCode='" + hashCode + "'}";
    }
}
