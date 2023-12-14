package com.aa.quiz.models;

import java.io.Serializable;

public class Question implements Serializable {
    public String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
