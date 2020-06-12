package com.stoken.stoken.model;

public class DataModel {
    private String id, title, filename, link, created_at, updated_at;
    private int type;

    public int getType() {
        return type;
    }

    public String getDocumentID() {
        return id;
    }

    public String getDocumentTitle() {
        return title;
    }

    public String getDocumentFileName() {
        return filename;
    }

    public String getDocumentLink() {
        return link;
    }

    public String getDocumentCreatedTime() {
        return created_at;
    }

    public String getDocumentUpdatedTime() {
        return updated_at;
    }
}
