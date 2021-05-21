package com.jaej.demo.model;

public class About {
    private String paragraph;
    private boolean isHead;

    public About(boolean isHead, String paragraph) {
        this.paragraph = paragraph;
        this.isHead = isHead;
    }

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public boolean isHead() {
        return isHead;
    }

    public void setHead(boolean head) {
        isHead = head;
    }
}
