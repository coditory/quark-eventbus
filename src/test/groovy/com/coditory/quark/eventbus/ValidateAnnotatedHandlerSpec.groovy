package com.coditory.quark.eventbus

import com.coditory.quark.eventbus.base.InMemEventExceptionHandler
import com.coditory.quark.eventbus.UnhandledEvent
import com.coditory.quark.eventbus.base.InMemUnhandledEventListener
import spock.lang.Specification
import spock.lang.Unroll

class ValidateAnnotatedHandlerSpec extends Specification {
    InMemUnhandledEventListener unhandledEventListener = new InMemUnhandledEventListener()
    DispatchExceptionHandler exceptionHandler = new InMemEventExceptionHandler()

    EventBus eventBus = new EventBusBuilder()
            .subscribe(UnhandledEvent, unhandledEventListener)
            .setExceptionHandler(exceptionHandler)
            .build()

    @Unroll
    def "should fail registering: #handler.class.simpleName"() {
        when:
            eventBus.subscribe(handler)
        then:
            IllegalArgumentException e = thrown(IllegalArgumentException)
            e.message == "Expected $expected parameter on @EventHandler method: $name"
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
        where:
            handler                      | expected               | name
            new MultiParamHandler()      | "single"               | "MultiParamHandler.handle(String, Integer)"
            new PrimitiveHandler()       | "single non-primitive" | "PrimitiveHandler.handle(int)"
            new GenericHandler<String>() | "single non-generic"   | "GenericHandler.handle(T)"
    }

    class MultiParamHandler {
        @EventHandler
        void handle(String event, Integer event2) {}
    }

    class PrimitiveHandler {
        @EventHandler
        void handle(int event) {}
    }

    class GenericHandler<T> {
        @EventHandler
        void handle(T event) {}
    }
}
