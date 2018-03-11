package com.waverley.fileBrowser.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

@Component
public class Read_writeUserPropertiesHolder {

    private boolean fullCheck;
    private boolean smartCheck;
    private boolean upload;
    private boolean download;
    private boolean access;
    private boolean copy;
    private boolean move;
    private boolean delete;
    private boolean createFolder;
    private boolean rename;


    public Read_writeUserPropertiesHolder() {

    }

    @PostConstruct
    public void getActualData(){
        Properties userReadWriteProperties = new Properties();

        String s = Read_writeUserPropertiesHolder.class.getClassLoader().getResource("read_writeUser.properties").getPath();
        FileInputStream is = null;
        try {
            is = new FileInputStream(s);
            userReadWriteProperties.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fullCheck = Boolean.parseBoolean(userReadWriteProperties.getProperty("fullCheck"));
        smartCheck = Boolean.parseBoolean(userReadWriteProperties.getProperty("smartCheck"));
        upload = Boolean.parseBoolean(userReadWriteProperties.getProperty("upload"));
        download =  Boolean.parseBoolean(userReadWriteProperties.getProperty("download"));
        access =Boolean.parseBoolean(userReadWriteProperties.getProperty("access"));
        copy =Boolean.parseBoolean(userReadWriteProperties.getProperty("copy"));
        move =Boolean.parseBoolean(userReadWriteProperties.getProperty("move"));
        delete =Boolean.parseBoolean(userReadWriteProperties.getProperty("delete"));
        createFolder =Boolean.parseBoolean(userReadWriteProperties.getProperty("createFolder"));
        rename =Boolean.parseBoolean(userReadWriteProperties.getProperty("rename"));

    }

    public boolean isFullCheck() {
        return fullCheck;
    }

    public boolean isSmartCheck() {
        return smartCheck;
    }

    public boolean isUpload() {
        return upload;
    }

    public boolean isDownload() {
        return download;
    }

    public boolean isAccess() {
        return access;
    }

    public boolean isCopy() {
        return copy;
    }

    public boolean isMove() {
        return move;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isCreateFolder() {
        return createFolder;
    }

    public boolean isRename() {
        return rename;
    }

}
