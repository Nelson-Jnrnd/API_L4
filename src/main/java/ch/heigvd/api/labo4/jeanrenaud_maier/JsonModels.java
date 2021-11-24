package ch.heigvd.api.labo4.jeanrenaud_maier;

import ch.heigvd.api.labo4.jeanrenaud_maier.smtp_client.Person;

import java.util.List;

class Messages {
    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public Messages(List<Message> messages) {
        this.messages = messages;
    }


    List<Message> messages;
}

class Message {
    private final String object;
    private final String content;

    public Message(String object, String content) {
        this.object = object;
        this.content = content;
    }

    public String getObject() {
        return object;
    }

    public String getContent() {
        return content;
    }
}

class Victims {
    public void setVictims(List<Person> victims) {
        this.victims = victims;
    }

    List<Person> victims;

    public Victims(List<Person> victims) {
        this.victims = victims;
    }

    public List<Person> getVictims() {
        return victims;
    }
}
