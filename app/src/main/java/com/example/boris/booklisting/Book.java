package com.example.boris.booklisting;

/**
 * Created by boris on 8/25/2016.
 */
public class Book {
    private String title;
    private String author;
    private String url;
    private String date;
    public Book(String title, String author, String date, String url) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.date = date;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getUrl() {
        return url;
    }
    public String getDate() {
        return date;
    }
    public String toString() {
        return title+" "+author+" "+url+" "+date;
    }
}
