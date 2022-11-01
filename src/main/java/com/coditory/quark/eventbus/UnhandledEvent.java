package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public record UnhandledEvent(Object event) {
    public UnhandledEvent(@NotNull Object event) {
        this.event = requireNonNull(event);
    }
}
