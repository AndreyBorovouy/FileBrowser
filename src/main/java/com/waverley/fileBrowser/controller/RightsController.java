package com.waverley.fileBrowser.controller;

import com.waverley.fileBrowser.dto.UserDTO;
import com.waverley.fileBrowser.enums.Option;
import com.waverley.fileBrowser.exceptions.UploadResult;
import com.waverley.fileBrowser.service.api.FileService;
import com.waverley.fileBrowser.service.api.UserService;
import jcifs.smb.SmbException;
import org.hibernate.annotations.SourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Andrey on 8/11/2017.
 */
@Controller
public class RightsController {

    @Autowired
    private FileService fileService;
    @Autowired
    private UserService userService;


    @PreAuthorize("hasRole('smartCheck')")
    @RequestMapping(value = "/smartCheck", method = RequestMethod.POST)
    @ResponseBody
    public void smartCheck(HttpServletRequest request) throws SmbException {

        fileService.smartCheck();
    }

    @PreAuthorize("hasRole('fullCheck')")
    @RequestMapping(value = "/recreatePreviews", method = RequestMethod.POST)
    @ResponseBody
    public void createPreviewEnvironment() throws SmbException {
        fileService.createLocalPreviews();
    }

    @PreAuthorize("hasRole('upload')")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Object upload(@RequestParam(value = "file") ArrayList<MultipartFile> formData, String optionParam, String rootFolder) throws UnsupportedEncodingException {
        optionParam = optionParam.toUpperCase();
        Option option = Option.valueOf(optionParam);

        UploadResult uploadResult = fileService.uploadFile(formData, rootFolder, option);
        return uploadResult;
    }


    @PreAuthorize("hasRole('download')")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public void getArchiveFile(HttpServletRequest request,
                               HttpServletResponse response, String files) throws UnsupportedEncodingException {
        response.setContentType("application/zip");
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = fileService.getArchiveFile(files, outputStream);
    //    response.setContentType("application/zip");
      //  try {
        //    response.getOutputStream().write(baos.toByteArray());
        //    response.flushBuffer();
      //  } catch (IOException e) {
       //     e.printStackTrace();
        //}
    }

    @PreAuthorize("hasRole('createFolder')")
    @RequestMapping(value = "/createFolder", method = RequestMethod.POST)
    @ResponseBody
    public String createNewFolder(String destination, String nameFodler) throws SmbException, UnsupportedEncodingException {

        return fileService.createNewFolder(destination, nameFodler);
    }

    @PreAuthorize("hasRole('copy')")
    @RequestMapping(value = "/copy", method = RequestMethod.GET)
    @ResponseBody
    public Object copy(String listOfFiles, String destURL, String optionParam, String typeAction) throws UnsupportedEncodingException {
        optionParam = optionParam.toUpperCase();
        typeAction = typeAction.toUpperCase();
        Option option = Option.valueOf(optionParam);
        Option type = Option.valueOf(typeAction);

        return fileService.copy(listOfFiles, destURL, option, type);

    }

    @PreAuthorize("hasRole('delete')")
    @RequestMapping(value = "/deleteFilesFolders", method = RequestMethod.GET)
    @ResponseBody
    public void deleteFilesFolders(String files) throws UnsupportedEncodingException {

        fileService.deleteContent(files);
    }

    @PreAuthorize("hasRole('rename')")
    @RequestMapping(value = "/rename", method = RequestMethod.GET)
    @ResponseBody
    public String rename(String newName, String localurl) throws UnsupportedEncodingException {
        return fileService.renameFile(newName, localurl);
    }


    @PreAuthorize("hasRole('editUser')")
    @RequestMapping(value = "/editUsers", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView editUser() {

        return userService.findAllUsers();
    }

    @PreAuthorize("hasRole('editUser')")
    @RequestMapping(value = "/createNewUser", method = RequestMethod.GET)
    @ResponseBody
    public void createNewUser(String rights ) {


        try {
            userService.userRightsUpdate(rights);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
