package com.waverley.fileBrowser.service.impl;


import com.waverley.fileBrowser.dao.api.RemouteDAO;
import com.waverley.fileBrowser.dto.AdminPropertyHolder;
import com.waverley.fileBrowser.dto.AnonymousPropertiesHolder;
import com.waverley.fileBrowser.dto.PropertyHolder;
import com.waverley.fileBrowser.dto.Read_writeUserPropertiesHolder;
import com.waverley.fileBrowser.mapper.BasicMapper;
import com.waverley.fileBrowser.service.api.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class UserServiceImpl implements UserService {

    @Autowired
    RemouteDAO remouteDAO;
    @Autowired
    private AuthService authenticate;
    @Autowired
    private AdminPropertyHolder adminPropertyHolder;
    @Autowired
    private AnonymousPropertiesHolder anonymousPropertiesHolder;
    @Autowired
    private Read_writeUserPropertiesHolder propertiesHolder;


    private static final String TEMPLATE = "[0-9A-Za-z-)(_-]*";

    @Override
    public ModelAndView findAllUsers() {

        anonymousPropertiesHolder.getActualData();
        propertiesHolder.getActualData();
        ModelAndView modelAndView = new ModelAndView("/fileBrowserTestForm/userInformation.jsp");
        modelAndView.addObject("anonymous", anonymousPropertiesHolder);
        modelAndView.addObject("readWriteUser", propertiesHolder);
        modelAndView.addObject("startScanBySchedule", StartSmartCheckBySchedule.isResult());
        return modelAndView;

    }


    @Override
    public ModelAndView authanticationUser(String userLogin, String userPassword) {

        ModelAndView model = new ModelAndView();
        Pattern pattern = Pattern.compile(TEMPLATE);
        Matcher matcher = pattern.matcher(userLogin);
        boolean result;
        model.setViewName("/fileBrowserTestForm/userLogin.jsp");

        if (!matcher.matches()) {
            model.addObject("error", "Invalid username or password!");
        } else if (userLogin == null || userLogin.equals("") || userPassword == null || userPassword.equals("")) {
            model.addObject("error", "Username or password is empty!");
        } else

            // boolean result = remouteDAO.checkUser(userLogin, userPassword);

            if (!(result = remouteDAO.checkUser(userLogin, userPassword))) {
                model.addObject("error", "Invalid username or password!");
            } else if (result) {
                if (userLogin.equals(adminPropertyHolder.getAdminLogin())) {
                    authenticate.authenticateAdminUser(userLogin, userPassword);
                } else {
                    authenticate.authenticateReadWriteUser(userLogin, userPassword);
                }
                model = new ModelAndView("redirect:" + "/home");
            }
        return model;
    }

    @Override
    public void userRightsUpdate(String rights) {
        Properties readWriteProperties = new Properties();
        Properties anonymousProperties = new Properties();

        String s = AnonymousPropertiesHolder.class.getClassLoader().getResource("anonymousUser.properties").getPath();
        String sf = Read_writeUserPropertiesHolder.class.getClassLoader().getResource("read_writeUser.properties").getFile();
        FileInputStream is = null;
        try {
            is = new FileInputStream(s);
            anonymousProperties.load(is);
            is.close();
            is = new FileInputStream(sf);
            readWriteProperties.load(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String[] arrRights = rights.split(";");

        for (int i = 0; i < arrRights.length; i++) {
            String[] right = arrRights[i].split(":");

            switch (right[0]) {
                case "fullCheck":
                    readWriteProperties.setProperty("fullCheck", right[1]);
                    break;
                case "smartCheck":
                    readWriteProperties.setProperty("smartCheck", right[1]);
                    break;
                case "upload":
                    readWriteProperties.setProperty("upload", right[1]);
                    break;
                case "download":
                    readWriteProperties.setProperty("download", right[1]);
                    break;
                case "copy":
                    readWriteProperties.setProperty("copy", right[1]);
                    break;
                case "move":
                    readWriteProperties.setProperty("move", right[1]);
                    break;
                case "delete":
                    readWriteProperties.setProperty("delete", right[1]);
                    break;
                case "createFolder":
                    readWriteProperties.setProperty("createFolder", right[1]);
                    break;
                case "rename":
                    readWriteProperties.setProperty("rename", right[1]);
                    break;

                case "smartCheckA":
                    anonymousProperties.setProperty("smartCheck.anonymous", right[1]);
                    break;
                case "fullCheckA":
                    anonymousProperties.setProperty("fullCheck.anonymous", right[1]);
                    break;
                case "downloadA":
                    anonymousProperties.setProperty("download.anonymous", right[1]);
                    break;
                case "access":
                    anonymousProperties.setProperty("access.anonymous", right[1]);
                    break;

                case "startScanBySchedule":
                    StartSmartCheckBySchedule.setResult(Boolean.valueOf(right[1]));
                    break;

            }

        }

        updateFileProperties("read_writeUser.properties", readWriteProperties);
        updateFileProperties("anonymousUser.properties", anonymousProperties);
        //set time when rights was changed
        authenticate.setTimeChange(System.currentTimeMillis());
    }

    //сохраняяет в ArtifactID
    public void updateFileProperties(String address, Properties fileProperties) {
        String way = UserServiceImpl.class.getClassLoader().getResource(address).getPath();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(way);
            fileProperties.store(out, null);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
