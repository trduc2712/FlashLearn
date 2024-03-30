package com.trduc.flashlearn;

import java.util.List;

public class Flashcards {

    private String flashcardsId;
    private List<Flashcard> flashcards;

    public Flashcards() {

    }

    public Flashcards(String flashcardsId, List<Flashcard> flashcards) {
        this.flashcardsId = flashcardsId;
        this.flashcards = flashcards;
    }

    public String getFlashcardsId() {
        return flashcardsId;
    }

    public void setFlashcardsId(String flashcardsId) {
        this.flashcardsId = flashcardsId;
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void setFlashcards(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }
}
