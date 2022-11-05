package com.coditory.quark.eventbus

import com.coditory.quark.eventbus.base.InMemEventExceptionHandler
import com.coditory.quark.eventbus.base.InMemEventListener
import com.coditory.quark.eventbus.base.InMemUnhandledEventListener
import spock.lang.Specification
import spock.lang.Unroll

class DispatchHierarchicalEventSpec extends Specification {
    InMemUnhandledEventListener unhandledEventListener = new InMemUnhandledEventListener()
    DispatchExceptionHandler exceptionHandler = new InMemEventExceptionHandler()
    Map<Class<?>, InMemEventListener<?>> listeners = [
            (A)     : new InMemEventListener<>("A"),
            (A1)    : new InMemEventListener<>("A1"),
            (B)     : new InMemEventListener<>("B"),
            (B1)    : new InMemEventListener<>("B1"),
            (B2)    : new InMemEventListener<>("B2"),
            (Object): new InMemEventListener<>("Object")
    ]

    EventBus eventBus = new EventBusBuilder()
            .subscribe(UnhandledEvent, unhandledEventListener)
            .setExceptionHandler(exceptionHandler)
            .build()

    def setup() {
        listeners.each {
            eventBus.subscribe(it.key as Class<Object>, it.value as EventListener<Object>)
        }
    }

    class A extends B implements A1 {}

    class B implements B1 {}

    class C implements B1 {}

    interface A1 {}

    interface B1 extends B2 {}

    interface B2 {}

    @Unroll
    def "should dispatch event to listeners subscribed on its type or subtypes: #event.getClass().simpleName"() {
        given:
            Set<String> expected = expectedListeners.collect { it.simpleName }
            Set<String> expectedSkipped = (listeners.keySet() - expectedListeners).collect { it.simpleName }
        when:
            eventBus.emit(event)
        then:
            executedListeners() == expected
        and:
            skippedListeners() == expectedSkipped
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
        where:
            event        || expectedListeners
            new A()      || [A, A1, B, B1, B2, Object]
            new B()      || [B, B1, B2, Object]
            new C()      || [B1, B2, Object]
            new Object() || [Object]
    }

    Set<String> executedListeners() {
        return listeners
                .findAll { it.value.wasExecuted() }
                .collect { it.getKey().simpleName }
    }

    Set<String> skippedListeners() {
        return listeners
                .findAll { !it.value.wasExecuted() }
                .collect { it.getKey().simpleName }
    }
}
