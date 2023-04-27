package com.example.novelreading;

public class BookDetailsClass {

    public String id;
    public String bookname;
    public String author;
    public String completeness;
    public String uptime;
    public String newchapter=null;
    public String imgsrc;
    public String starturl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCompleteness() {
        return completeness;
    }

    public void setCompleteness(String completeness) {
        this.completeness = completeness;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public String getNewchapter() {
        return newchapter;
    }

    public void setNewchapter(String newchapter) {
        this.newchapter = newchapter;
    }

    public String getImgsrc() {
        return imgsrc;
    }

    public void setImgsrc(String imgsrc) {
        this.imgsrc = imgsrc;
    }

    public String getStarturl() {
        return starturl;
    }

    public void setStarturl(String starturl) {
        this.starturl = starturl;
    }
}
