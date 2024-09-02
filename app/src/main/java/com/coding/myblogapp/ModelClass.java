package com.coding.myblogapp;

public class ModelClass {

    private String title;
    private String description;
    private String authorname;
    private String date;
    private String img;
    private String share_count;
    private String id;
    private String timestamp;

    public ModelClass(String title, String description, String authorname, String date, String img, String share_count, String id, String timestamp) {
        this.title = title;
        this.description = description;
        this.authorname = authorname;
        this.date = date;
        this.img = img;
        this.share_count = share_count;
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthorname() {
        return authorname;
    }

    public void setAuthorname(String authorname) {
        this.authorname = authorname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getShare_count() {
        return share_count;
    }

    public void setShare_count(String share_count) {
        this.share_count = share_count;
    }

    public ModelClass() {
        // Default constructor
    }
}
