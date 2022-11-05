package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface EventListener<T> {
    void handle(@NotNull T event) throws Throwable;
}
