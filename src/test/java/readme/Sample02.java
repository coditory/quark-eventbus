package readme;

import com.coditory.quark.eventbus.EventBus;
import com.coditory.quark.eventbus.EventHandler;

public class Sample02 {
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
