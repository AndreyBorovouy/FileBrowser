package com.waverley.fileBrowser.service.api;

import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Andrey on 9/29/2017.
 */
public interface UserService {

     ModelAndView findAllUsers();

    ModelAndView authanticationUser(String userLogin, String userPassword);

    void userRightsUpdate(String rights) throws IOException;
}
