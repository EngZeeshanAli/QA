package com.zeeshanaliawan.blogspot.QA.obj;

public class Answer {
    String id, answer, time, poster;

    public Answer() {
    }

    public Answer(String answer, String time, String poster) {
        this.answer = answer;
        this.time = time;
        this.poster = poster;
    }

    public Answer(String id, String answer, String time, String poster) {
        this.id = id;
        this.answer = answer;
        this.time = time;
        this.poster = poster;
    }

    public String getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public String getTime() {
        return time;
    }

    public String getPoster() {
        return poster;
    }
}
