# Quark EventBus
[![Build](https://github.com/coditory/quark-eventbus/actions/workflows/build.yml/badge.svg)](https://github.com/coditory/quark-eventbus/actions/workflows/build.yml)
[![Coverage Status](https://coveralls.io/repos/github/coditory/quark-eventbus/badge.svg)](https://coveralls.io/github/coditory/quark-eventbus)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.coditory.quark/quark-eventbus/badge.svg)](https://mvnrepository.com/artifact/com.coditory.quark/quark-eventbus)

> Super simple, lightweight, single purpose, in-memory EventBus java library. Similar to eventBuses provided by [Spring Framework](https://docs.spring.io/spring-framework/docs/5.3.9/javadoc-api/org/springframework/context/ApplicationEvent.html)
or [Guava](https://github.com/google/guava/wiki/EventBusExplained) but without the unrelated parts.

- lightweight, no dependencies
- single purpose, not part of a framework
- provides both functional and annotation based API
- public API annotated with `@NotNull` and `@Nullable` for better [kotlin integration](https://kotlinlang.org/docs/java-to-kotlin-nullability-guide.html#platform-types)
- integrates with [event bus](https://github.com/coditory/quark-context)

This EventBus is threadsafe and synchronous. It deliberately provides no asynchronous api to keep it super simple.

## Installation

Add to your `build.gradle`:

```gradle
dependencies {
    implementation "com.coditory.quark:quark-eventbus:0.0.4"
}
```

## Usage

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

### Exception handling

```java
public class Application {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.builder()
                .subscribe(String.class, (event) -> { throw new RuntimeException("xxx"); })
                .subscribe(String.class, (event) -> System.out.println("String event: " + event))
                .setExceptionHandler(ctx -> System.out.println("Exception: " + ctx.exception()))
                .build();
        eventBus.emit("hello");
    }
}
// Output:
// Exception: java.lang.RuntimeException: xxx
// String event: hello
```

### Handling unhandled events

```java
public class Application {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.builder()
                .subscribe(String.class, (event) -> System.out.println("String event: " + event))
                .subscribe(UnhandledEvent.class, (unhandled) -> System.out.println("Unhandled event: " + unhandled.event()))
                .build();
        eventBus.emit(42);
    }
}
// Output:
// Unhandled event: 42
```