package by.bsuir.models.server.services;

import by.bsuir.models.messages.Message;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;

public class History implements Serializable {
    private final Set<Message> messages = Collections.synchronizedSortedSet(new TreeSet<>());

    public void importExternalHistory(History history) {
        messages.addAll(history.messages);
    }

    public void add(Message message) {
        messages.add(message);
    }

    public void forEach(Consumer<? super Message> consumer) {
        synchronized (messages) {
            for (Message message : messages) {
                consumer.accept(message);
            }
        }
    }
}
