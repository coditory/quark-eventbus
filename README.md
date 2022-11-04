# Quark EventBus
[![Build](https://github.com/coditory/quark-eventbus/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/quark-eventbus/actions/workflows/build.yml)
[![Coverage Status](https://coveralls.io/repos/github/coditory/quark-eventbus/badge.svg)](https://coveralls.io/github/coditory/quark-eventbus)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.quark/quark-eventbus/badge.svg)](https://mvnrepository.com/artifact/com.coditory.quark/quark-eventbus)

> Quark EventBus is a simple, single purpose java library for dispatching events

The idea was to create a small, single-jar library, similar to
the EventBus provided by [Spring Framework](https://docs.spring.io/spring-framework/docs/5.3.9/javadoc-api/org/springframework/context/ApplicationEvent.html)
or [Guava](https://github.com/google/guava/wiki/EventBusExplained)
, that is:

- lightweight, no dependencies
- single purpose and is not part of a framework
- provides both functional and annotation based API

This EventBus is threadsafe and synchronous.
It deliberately provides no concurrent api to keep it as simple as possible.

## Installation

Add to your `build.gradle`:

```gradle
dependencies {
    implementation "com.coditory.quark:quark-eventbus:0.0.3"
}
```

## Usage

### EventListener subscription

```java
public class Application {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.create();
        eventBus.subscribe(String.class, (event) -> System.out.println("String event: " + event));
        eventBus.subscribe(Number.class, (event) -> System.out.println("Integer event: " + event));
        eventBus.subscribe(Integer.class, (event) -> System.out.println("Integer event: " + event));
        eventBus.emit("hello");
        eventBus.emit(42);
    }
}
// Output:
// String event: hello
// Integer event: 42
// Number event: 42
```

### EventHandler subscription

```java
public class Application {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.create();
        eventBus.subscribe(new TwoHandlers());
        eventBus.emit("hello");
        eventBus.emit(42);
    }

    static class TwoHandlers {
        @EventHandler
        void handle(String event) {
            System.out.println("String event: " + event);
        }

        @EventHandler
        void handle(Integer event) {
            System.out.println("Integer event: " + event);
        }
    }
}
// Output:
// String event: hello
// Integer event: 42
```