package com.zeeshanaliawan.blogspot.QA.obj;

import android.widget.EditText;

public class MyUser {
    private String name, email,  skill, img, id;

    public MyUser() {
    }

    public MyUser(String name, String email, String skill) {
        this.name = name;
        this.email = email;
        this.skill = skill;
    }

    public MyUser(String name, String email, String skill, String id) {
        this.name = name;
        this.email = email;
        this.skill = skill;
        this.id = id;
    }

    public MyUser(String name, String email,  String skill, String img, String id) {
        this.name = name;
        this.email = email;
        this.skill = skill;
        this.img = img;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }



    public String getSkill() {
        return skill;
    }

    public String getImg() {
        return img;
    }

    public String getId() {
        return id;
    }

}
