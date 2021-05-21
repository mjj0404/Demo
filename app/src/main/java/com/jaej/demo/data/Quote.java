package com.jaej.demo.data;

public class Quote {
    String text;
    String author;

    public Quote(String text, String author) {
        this.text = text;
        this.author = author;
    }

    public Quote() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
