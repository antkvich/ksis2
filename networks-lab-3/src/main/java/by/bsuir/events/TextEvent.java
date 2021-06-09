package by.bsuir.events;

import by.bsuir.models.messages.TextMessage;

public class TextEvent extends Event {
    private final TextMessage textMessage;

    public TextEvent(TextMessage textMessage) {
        this.textMessage = textMessage;
    }

    public TextMessage getTextMessage() {
        return textMessage;
    }
}
