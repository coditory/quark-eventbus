package com.coditory.quark.eventbus


import com.coditory.quark.eventbus.base.InMemEventListener
import com.coditory.quark.eventbus.base.InMemEventExceptionHandler
import com.coditory.quark.eventbus.base.InMemUnhandledEventListener
import spock.lang.Specification

class HandleDispatchExceptionSpec extends Specification {
    InMemEventListener<String> stringListener = new InMemEventListener<>()
    InMemUnhandledEventListener unhandledEventListener = new InMemUnhandledEventListener()
    DispatchExceptionHandler exceptionHandler = new InMemEventExceptionHandler()
    Exception exception = new RuntimeException("Simulation")

    def "should pass exception to exceptionListener and continue dispatching"() {
        given:
            EventListener<String> listener = { throw exception }
            EventBus eventBus = new EventBusBuilder()
                    .addEventListener(String, stringListener)
                    .addEventListener(String, listener)
                    .addUnhandledEventListener(unhandledEventListener)
                    .setExceptionHandler(exceptionHandler)
                    .build()
        when:
            eventBus.emit("Hello")
        then:
            exceptionHandler.getContexts() == [new DispatchExceptionContext(
                    exception,
                    "Hello",
                    Subscription.fromEventListener(String, listener)
            )]
        and:
            stringListener.getEvents() == ["Hello"]
            unhandledEventListener.wasNotExecuted()
    }

    def "should pass exception thrown by deadEventListener to exceptionListener"() {
        given:
            EventListener<UnhandledEvent> unhandledEventListener = { throw exception }
            DispatchExceptionHandler exceptionHandler = new InMemEventExceptionHandler()
            EventBus eventBus = new EventBusBuilder()
                    .addUnhandledEventListener(unhandledEventListener)
                    .setExceptionHandler(exceptionHandler)
                    .build()
        when:
            eventBus.emit("Hello")
        then:
            exceptionHandler.getContexts() == [new DispatchExceptionContext(
                    exception,
                    new UnhandledEvent("Hello"),
                    Subscription.fromEventListener(UnhandledEvent, unhandledEventListener)
            )]
    }

    def "should propagate exception thrown by exceptionListener"() {
        given:
            DispatchExceptionHandler exceptionHandler = { throw exception }
            EventBus eventBus = new EventBusBuilder()
                    .addEventListener(String, { throw new RuntimeException("Thrown by event listener") })
                    .addUnhandledEventListener(unhandledEventListener)
                    .setExceptionHandler(exceptionHandler)
                    .build()
        when:
            eventBus.emit("Hello")
        then:
            RuntimeException e = thrown(RuntimeException)
            e == exception
    }
}
