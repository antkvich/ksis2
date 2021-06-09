package by.bsuir.events;

public interface EventHandler<T extends Event> {

    void handle(T event);
}
