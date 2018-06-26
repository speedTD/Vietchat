package com.developer.dinhduy.chatapp;

public class User {

    public User(String name, String picture, String about, String thumbPicture) {
        Name = name;
        Picture = picture;
        About = about;
        ThumbPicture = thumbPicture;
    }
    public User(){
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getPicture() {
        return Picture;
    }
    public void setPicture(String picture) {
        Picture = picture;
    }
    public String getAbout() {
        return About;
    }
    public void setAbout(String about) {
        About = about;
    }

    public String getThumbPicture() {
        return ThumbPicture;
    }

    public void setThumbPicture(String thumbPicture) {
        ThumbPicture = thumbPicture;
    }

    private  String Name;
    private  String Picture;
    private  String About;
    private  String ThumbPicture;

}
