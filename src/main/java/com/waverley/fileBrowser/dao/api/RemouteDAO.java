package com.waverley.fileBrowser.dao.api;

import com.waverley.fileBrowser.enums.Option;
import jcifs.smb.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Andrey on 9/29/2017.
 */
public interface RemouteDAO{

     SmbFile getFile(String path);

    boolean checkUser(String userLogin, String userPassword);

    //get all files and folders from server throw smb protocol
     ArrayList<SmbFile> getFolderContent(SmbFile smbFile) throws SmbException;

     void createFolder(String foldername) throws SmbException;

     void createImage(byte[] bytes, String fileName, String urlDirectoryForFolder) throws IOException;

     void copyImage(String fileName, String urlDirectoryForFolder, Option type);

   //  SmbFileOutputStream smbFileDest(String fileName);

     SmbFileInputStream showOriginalFile(String fileName);

     String convertLocalFileAddressToRemoteFileAddress(String localFileAddress);

     void deleteFolderContent(SmbFile smbFile);

    void createImageUpload(MultipartFile file, String filename, String folderDestination);
}
