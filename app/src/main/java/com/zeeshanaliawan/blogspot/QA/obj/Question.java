package com.zeeshanaliawan.blogspot.QA.obj;

public class Question {
    private String title, description, img;

    public Question() {
    }

    public Question(String title, String description, String img) {
        this.title = title;
        this.description = description;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImg() {
        return img;
    }
}
