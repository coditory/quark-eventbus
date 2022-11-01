package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public final class EventBusBuilder {
    private final DispatchExceptionHandler DEFAULT_EXCEPTION_HANDLER = new LoggingExceptionHandler();
    private final List<Subscription<?>> subscriptions = new ArrayList<>();
    private String name = "EventBus";
    private DispatchExceptionHandler exceptionHandler = DEFAULT_EXCEPTION_HANDLER;

    public EventBusBuilder addEventHandler(@NotNull Object listener) {
        requireNonNull(listener);
        subscriptions.addAll(Subscription.fromEventHandlerMethods(listener));
        return this;
    }

    public EventBusBuilder addSubscription(@NotNull Subscription<?> listener) {
        requireNonNull(listener);
        subscriptions.add(listener);
        return this;
    }

    public <T> EventBusBuilder addEventListener(@NotNull Class<? extends T> eventType, @NotNull EventListener<T> listener) {
        requireNonNull(eventType);
        requireNonNull(listener);
        subscriptions.add(Subscription.fromEventListener(eventType, listener));
        return this;
    }

    public EventBusBuilder addUnhandledEventListener(@NotNull EventListener<UnhandledEvent> listener) {
        requireNonNull(listener);
        addEventListener(UnhandledEvent.class, listener);
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
        DispatchingEventBus eventBus = new DispatchingEventBus(name, exceptionHandler);
        for (Subscription<?> subscription : subscriptions) {
            eventBus.addSubscription(subscription);
        }
        return eventBus;
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
