package readme;

import com.coditory.quark.eventbus.EventBus;

public class Sample03 {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.builder()
                .subscribe(String.class, (event) -> { throw new RuntimeException("xxx"); })
                .subscribe(String.class, (event) -> System.out.println("String event: " + event))
                .setExceptionHandler(ctx -> System.out.println("Exception: " + ctx.exception()))
                .build();
        eventBus.emit("hello");
    }
}
