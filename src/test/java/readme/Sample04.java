package readme;

import com.coditory.quark.eventbus.EventBus;
import com.coditory.quark.eventbus.UnhandledEvent;

public class Sample04 {
    public static void main(String[] args) {
        EventBus eventBus = EventBus.builder()
                .subscribe(String.class, (event) -> System.out.println("String event: " + event))
                .subscribe(UnhandledEvent.class, (unhandled) -> System.out.println("Unhandled event: " + unhandled.event()))
                .build();
        eventBus.emit(42);
    }
}
