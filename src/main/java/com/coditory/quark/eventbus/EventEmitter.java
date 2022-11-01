package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EventEmitter {
    void emit(@NotNull Object event);
}
