package com.waverley.fileBrowser.dto;

import org.springframework.stereotype.Component;

@Component
public class FileInfo {

    private String localurl;
    private String name;
    private String image;

    public String getLocalurl() {
        return localurl;
    }

    public void setLocalurl(String localurl) {
        this.localurl = localurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    @Override
    public String toString() {
        return "FileInfo{" +
                "localurl='" + localurl + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }


}
