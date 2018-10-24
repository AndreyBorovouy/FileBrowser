package com.waverley.fileBrowser.controller;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.io.ByteStreams;
import com.waverley.fileBrowser.dto.FileInfo;
import com.waverley.fileBrowser.service.impl.FileServiceImpl;
import jcifs.smb.SmbFileInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by Andrey on 5/25/2017.
 */

@Controller
public class ContentController {

    @Autowired
    private FileServiceImpl fileService;

    @RequestMapping(value = "/content", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<FileInfo> openFolder(String folderURL) throws UnsupportedEncodingException {

        ArrayList<FileInfo> fileInfosArrayList = fileService.getContent(folderURL);
        return fileInfosArrayList;
    }

    @RequestMapping(value = "/originalFile", method = RequestMethod.GET)
    @ResponseBody
    public void showOriginalFile(HttpServletResponse response, String fileURL) throws UnsupportedEncodingException {

        SmbFileInputStream inputStream = fileService.getOriginalFile(fileURL);
        try (OutputStream output = response.getOutputStream()) {
            ByteStreams.copy(inputStream, output);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping(value = "/folders", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<String> fetchFolders() {

        ArrayList<String> folders = fileService.getAllFolders();
        return folders;
    }

}
