package com.coditory.quark.eventbus


import com.coditory.quark.eventbus.base.InMemEventListener
import com.coditory.quark.eventbus.base.InMemEventExceptionHandler
import com.coditory.quark.eventbus.base.InMemUnhandledEventListener
import spock.lang.Specification

class DispatchEventSpec extends Specification {
    InMemEventListener<Integer> integerListener = new InMemEventListener<>()
    InMemEventListener<String> stringListener = new InMemEventListener<>()
    InMemUnhandledEventListener unhandledEventListener = new InMemUnhandledEventListener()
    DispatchExceptionHandler exceptionHandler = new InMemEventExceptionHandler()

    EventBus eventBus = new EventBusBuilder()
            .addEventListener(String, stringListener)
            .addEventListener(Integer, integerListener)
            .addUnhandledEventListener(unhandledEventListener)
            .setExceptionHandler(exceptionHandler)
            .build()

    def "should dispatch single event"() {
        when:
            eventBus.emit("Hello")
        then:
            stringListener.getEvents() == ["Hello"]
            integerListener.getEvents() == []
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should not register the same listener multiple times"() {
        given:
            eventBus.addEventListener(String, stringListener)
            eventBus.addEventListener(String, stringListener)
        when:
            eventBus.emit("Hello")
        then:
            stringListener.getEvents() == ["Hello"]
            integerListener.getEvents() == []
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should unregister one of two event listeners"() {
        given:
            eventBus.removeEventListener(String, stringListener)
        when:
            eventBus.emit("Hello")
        then:
            stringListener.getEvents() == []
            integerListener.getEvents() == []
        and:
            unhandledEventListener.unwrappedEvents == ["Hello"]
            exceptionHandler.wasNotExecuted()
    }

    def "should unregister one of two event listeners"() {
        given:
            EventListener<String> otherListener = new InMemEventListener<>()
            eventBus.addEventListener(String, otherListener)
        and:
            eventBus.removeEventListener(String, stringListener)
        when:
            eventBus.emit("Hello")
        then:
            stringListener.getEvents() == []
            otherListener.getEvents() == ["Hello"]
            integerListener.getEvents() == []
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should dispatch single event to multiple listeners"() {
        given:
            EventListener<String> otherListener = new InMemEventListener<>()
            eventBus.addEventListener(String, otherListener)
        when:
            eventBus.emit("Hello")
        then:
            stringListener.getEvents() == ["Hello"]
            otherListener.getEvents() == ["Hello"]
            integerListener.getEvents() == []
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should dispatch multiple events in order"() {
        when:
            eventBus.emit("one")
            eventBus.emit(111)
            eventBus.emit("two")
            eventBus.emit(222)
        then:
            stringListener.getEvents() == ["one", "two"]
            integerListener.getEvents() == [111, 222]
        and:
            unhandledEventListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }

    def "should dispatch dead event to deadEventListener"() {
        when:
            eventBus.emit(true)
        then:
            unhandledEventListener.unwrappedEvents == [true]
        and:
            stringListener.wasNotExecuted()
            integerListener.wasNotExecuted()
            exceptionHandler.wasNotExecuted()
    }
}
