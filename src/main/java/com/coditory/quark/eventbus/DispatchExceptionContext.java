package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public record DispatchExceptionContext(
        @NotNull RuntimeException exception,
        @NotNull Object event,
        @NotNull Subscription<?> subscription
) {
    public DispatchExceptionContext(
            RuntimeException exception,
            Object event,
            Subscription<?> subscription
    ) {
        this.exception = requireNonNull(exception);
        this.event = requireNonNull(event);
        this.subscription = requireNonNull(subscription);
    }
}
