package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import static com.coditory.quark.eventbus.Preconditions.expectNonNull;

public record DispatchExceptionContext(
        @NotNull Throwable exception,
        @NotNull Object event,
        @NotNull Subscription<?> subscription
) {
    public DispatchExceptionContext(
            Throwable exception,
            Object event,
            Subscription<?> subscription
    ) {
        this.exception = expectNonNull(exception, "exception");
        this.event = expectNonNull(event, "event");
        this.subscription = expectNonNull(subscription, "subscription");
    }
}
