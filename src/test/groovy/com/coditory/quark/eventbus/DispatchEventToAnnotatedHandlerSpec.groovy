package com.coditory.quark.eventbus

import com.coditory.quark.eventbus.base.InMemEventExceptionHandler
import com.coditory.quark.eventbus.base.InMemUnhandledEventListener
import spock.lang.Specification

class DispatchEventToAnnotatedHandlerSpec extends Specification {
    InMemUnhandledEventListener unhandledEventListener = new InMemUnhandledEventListener()
    DispatchExceptionHandler exceptionHandler = new InMemEventExceptionHandler()

    EventBus eventBus = new EventBusBuilder()
            .subscribe(unhandledEventListener)
            .setExceptionHandler(exceptionHandler)
            .build()

    def "should dispatch event to annotated handler"() {
        given:
            A handler = new A()
            eventBus.subscribe(handler)
        when:
            eventBus.emit("hello")
        then:
            handler.executed == ["A.handle(String hello)"]
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should dispatch event to two annotated handlers"() {
        given:
            B handler = new B()
            eventBus.subscribe(handler)
        when:
            eventBus.emit("hello")
        then:
            handler.executed == ["B.handle(String hello)", "B.handle(Object hello)"]
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should dispatch event to overridden and inherited handle methods"() {
        given:
            C1 handler = new C1()
            eventBus.subscribe(handler)
        when:
            eventBus.emit("hello")
        then:
            handler.executed == ["C1.handle(String hello)", "C2.handle(Object hello)"]
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should dispatch event to method annotated on an interface"() {
        given:
            D1 handler = new D1()
            eventBus.subscribe(handler)
        when:
            eventBus.emit("hello")
        then:
            handler.executed == ["D1.handle(String hello)"]
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    class A extends InMemHandler {
        @EventHandler
        void handle(String event) {
            receive("A.handle(String $event)")
        }
    }

    class B extends InMemHandler {
        @EventHandler
        void handle(String event) {
            receive("B.handle(String $event)")
        }

        @EventHandler
        void handle(Object event) {
            receive("B.handle(Object $event)")
        }
    }

    class C1 extends C2 {
        void handle(String event) {
            receive("C1.handle(String $event)")
        }
    }

    class C2 extends InMemHandler {
        @EventHandler
        void handle(String event) {
            receive("C2.handle(String $event)")
        }

        @EventHandler
        void handle(Object event) {
            receive("C2.handle(Object $event)")
        }
    }

    class D1 extends InMemHandler implements D2 {
        void handle(String event) {
            receive("D1.handle(String $event)")
        }

        void handle(Object event) {
            receive("C2.handle(Object $event)")
        }
    }

    interface D2 {
        @EventHandler
        void handle(String event)
    }

    abstract class InMemHandler {
        private final List<String> executed = new ArrayList<>();

        void receive(String eventDescriptor) {
            executed.add(eventDescriptor)
        }

        List<String> getExecuted() {
            return List.copyOf(executed)
        }
    }
}
