package com.trduc.flashlearn.Models;

public class FlashcardSets {

    private String id, name, topic;

    public FlashcardSets() {
    }

    public FlashcardSets(String id, String name, String topic) {
        this.id = id;
        this.name = name;
        this.topic = topic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
