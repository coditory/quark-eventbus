package com.coditory.quark.eventbus;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface DispatchExceptionHandler {
    void handle(@NotNull DispatchExceptionContext context);
}
