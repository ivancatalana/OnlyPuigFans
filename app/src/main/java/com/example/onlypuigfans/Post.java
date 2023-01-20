package com.example.onlypuigfans;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;

public class Post {
    public String uid;
    public String author;
    public String dateTimePost;
    public String ordenadaDateTime;
   // public String date
    public String authorPhotoUrl;
    public String content;
    public Map<String, Boolean> likes = new HashMap<>();


    // Constructor vacio requerido por Firestore
    public Post() {}

    public Post(String uid, String author, String dateTimePost,String ordenadaDateTime,  String authorPhotoUrl, String content) {
        this.uid = uid;
        this.author = author;
        this.dateTimePost=dateTimePost;
        this.ordenadaDateTime=ordenadaDateTime;
        this.authorPhotoUrl = authorPhotoUrl;
        this.content = content;
    }

}