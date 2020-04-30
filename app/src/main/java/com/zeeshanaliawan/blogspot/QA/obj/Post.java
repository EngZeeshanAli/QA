package com.zeeshanaliawan.blogspot.QA.obj;

public class Post {
    private String id, title, desc, img, dateTime, poster;

    public Post() {
    }

    public Post(String title, String desc, String dateTime, String poster) {
        this.title = title;
        this.desc = desc;
        this.dateTime = dateTime;
        this.poster = poster;
    }

    public Post(String title, String desc, String img, String dateTime, String poster) {
        this.title = title;
        this.desc = desc;
        this.img = img;
        this.dateTime = dateTime;
        this.poster = poster;
    }

    public Post(String id, String title, String desc, String img, String dateTime, String poster) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.img = img;
        this.dateTime = dateTime;
        this.poster = poster;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getImg() {
        return img;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getPoster() {
        return poster;
    }
}
