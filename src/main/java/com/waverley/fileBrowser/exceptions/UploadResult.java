package com.waverley.fileBrowser.exceptions;

import java.util.List;

/**
 * Created by anton.kovalenko on 10/12/17.
 */
public class UploadResult {
    private String name;
    private List<String> files;

    public UploadResult(String name, List<String> files) {
        this.name = name;
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public List<String> getFiles() {
        return files;
    }
}
