package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import static com.coditory.quark.eventbus.Preconditions.expectNonNull;

public record UnhandledEvent(Object event) {
    public UnhandledEvent(@NotNull Object event) {
        this.event = expectNonNull(event, "event");
    }
}
