package com.mygumi.insider.dto;

import lombok.ToString;

@ToString
public class DetailImagePath {

    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
