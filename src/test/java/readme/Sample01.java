package readme;

import com.coditory.quark.eventbus.EventBus;
import com.coditory.quark.eventbus.Subscribe;

public class Sample01 {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.create();
        eventBus.subscribe(String.class, (event) -> System.out.println("String event: " + event));
        eventBus.subscribe(Integer.class, (event) -> System.out.println("Integer event: " + event));
        eventBus.subscribe(Number.class, (event) -> System.out.println("Number event: " + event));
        eventBus.emit("hello");
        eventBus.emit(42);
    }
}
