package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.requireNonNull;

public final class EventBusBuilder {
    private final AtomicInteger DEFAULT_NAME_COUNTER = new AtomicInteger(1);
    private final DispatchExceptionHandler DEFAULT_EXCEPTION_HANDLER = new LoggingExceptionHandler();
    private final List<Subscription<?>> subscriptions = new ArrayList<>();
    private String name;
    private DispatchExceptionHandler exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

    EventBusBuilder() {
        // package scope constructor
    }

    public EventBusBuilder subscribe(@NotNull Object listener) {
        requireNonNull(listener);
        subscriptions.addAll(Subscription.of(listener));
        return this;
    }

    public EventBusBuilder subscribe(@NotNull Subscription<?> listener) {
        requireNonNull(listener);
        subscriptions.add(listener);
        return this;
    }

    public <T> EventBusBuilder subscribe(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        requireNonNull(eventType);
        requireNonNull(listener);
        subscriptions.add(Subscription.of(eventType, listener));
        return this;
    }

    public EventBusBuilder subscribe(@NotNull EventListener<UnhandledEvent> listener) {
        requireNonNull(listener);
        subscribe(UnhandledEvent.class, listener);
        return this;
    }

    public EventBusBuilder setName(@NotNull String name) {
        this.name = requireNonNull(name);
        return this;
    }

    public EventBusBuilder setExceptionHandler(@NotNull DispatchExceptionHandler exceptionHandler) {
        this.exceptionHandler = requireNonNull(exceptionHandler);
        return this;
    }

    public EventBus build() {
        DispatchingEventBus eventBus = new DispatchingEventBus(resolveName(), exceptionHandler);
        for (Subscription<?> subscription : subscriptions) {
            eventBus.subscribe(subscription);
        }
        return eventBus;
    }

    private String resolveName() {
        if (name != null) {
            return name;
        }
        int counter = DEFAULT_NAME_COUNTER.incrementAndGet();
        String defaultName = "EventBus";
        return counter > 1 ? defaultName + "-" + counter : defaultName;
    }

    private static final class LoggingExceptionHandler implements DispatchExceptionHandler {
        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        @Override
        public void handle(@NotNull DispatchExceptionContext context) {
            if (logger.isErrorEnabled()) {
                logger.error("Exception on event dispatch.\nSubscription: {}\nEvent: {}",
                        context.subscription(), context.event(), context.exception());
            }
        }
    }
}
