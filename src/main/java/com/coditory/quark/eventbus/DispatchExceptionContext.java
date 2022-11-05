package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

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
        this.exception = requireNonNull(exception);
        this.event = requireNonNull(event);
        this.subscription = requireNonNull(subscription);
    }
}
