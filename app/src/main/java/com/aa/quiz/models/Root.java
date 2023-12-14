package com.aa.quiz.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Root implements Serializable {
    public String category;
    public String id;
    public String correctAnswer;
    public ArrayList<String> incorrectAnswers;
    public Question question;
    public ArrayList<String> tags;
    public String type;
    public String difficulty;
    public ArrayList<Object> regions;
    public boolean isNiche;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public ArrayList<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(ArrayList<String> incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public ArrayList<Object> getRegions() {
        return regions;
    }

    public void setRegions(ArrayList<Object> regions) {
        this.regions = regions;
    }

    public boolean isNiche() {
        return isNiche;
    }

    public void setNiche(boolean niche) {
        isNiche = niche;
    }
}
