package com.waverley.fileBrowser.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;



//@PropertySource("classpath:anonymousUser.properties")
@Component
public class AnonymousPropertiesHolder {

    private boolean fullCheck;
    private boolean smartCheck;
    private boolean download ;
    private boolean access;
    private String anonymousUser;
    private String anonymousPass;

    Properties anonymousProperties;

//    String s = AnonymousPropertiesHolder.class.getClassLoader().getResource("anonymousUser.properties").getPath();
//    FileInputStream is = null;

    public AnonymousPropertiesHolder() {
        /*
        anonymousProperties = new Properties();

        String s = AnonymousPropertiesHolder.class.getClassLoader().getResource("anonymousUser.properties").getPath();
        FileInputStream is = null;
        try {
            is = new FileInputStream(s);
            anonymousProperties.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //    @Value("${fullCheck.anonymous}")
        fullCheck = Boolean.parseBoolean(anonymousProperties.getProperty("fullCheck.anonymous"));
        smartCheck = Boolean.parseBoolean(anonymousProperties.getProperty("smartCheck.anonymous"));
        download = Boolean.parseBoolean(anonymousProperties.getProperty("download.anonymous"));
        access = Boolean.parseBoolean(anonymousProperties.getProperty("access.anonymous"));
        anonymousUser = anonymousProperties.getProperty("login.anonymous");
        anonymousPass = anonymousProperties.getProperty("password.anonymous");
*/
    }

    @PostConstruct
    public void getActualData(){
        Properties anonymousProperties = new Properties();

        String s = AnonymousPropertiesHolder.class.getClassLoader().getResource("anonymousUser.properties").getPath();
        FileInputStream is = null;
        try {
            is = new FileInputStream(s);
            anonymousProperties.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //    @Value("${fullCheck.anonymous}")
        fullCheck = Boolean.parseBoolean(anonymousProperties.getProperty("fullCheck.anonymous"));
        smartCheck = Boolean.parseBoolean(anonymousProperties.getProperty("smartCheck.anonymous"));
        download = Boolean.parseBoolean(anonymousProperties.getProperty("download.anonymous"));
        access = Boolean.parseBoolean(anonymousProperties.getProperty("access.anonymous"));
        anonymousUser = anonymousProperties.getProperty("login.anonymous");
        anonymousPass = anonymousProperties.getProperty("password.anonymous");
    }

//    //    @Value("${fullCheck.anonymous}")
//    private boolean fullCheck = Boolean.parseBoolean(anonymousProperties.getProperty("fullCheck.anonymous"));
//  //  @Value("${smartCheck.anonymous}")
//    private boolean smartCheck = Boolean.parseBoolean(anonymousProperties.getProperty("smartCheck.anonymous"));
//  //  @Value("${download.anonymous}")
//    private boolean download = Boolean.parseBoolean(anonymousProperties.getProperty("download.anonymous"));
// //   @Value("${access.anonymous}")
//    private boolean access = Boolean.parseBoolean(anonymousProperties.getProperty("access.anonymous"));
// //   @Value("${login.anonymous}")
//    private String anonymousUser = anonymousProperties.getProperty("login.anonymous");
//  //  @Value("${password.anonymous}")
//    private String anonymousPass=anonymousProperties.getProperty("password.anonymous ");


    public boolean isFullCheck() {
        return fullCheck;
    }

    public boolean isSmartCheck() {
        return smartCheck;
    }

    public boolean isDownload() {
        return download;
    }

    public boolean isAccess() {
        return access;
    }

    public String getAnonymousUser() {
        return anonymousUser;
    }

    public String getAnonymousPass() {
        return anonymousPass;
    }



}
