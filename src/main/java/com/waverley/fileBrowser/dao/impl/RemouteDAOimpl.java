package com.waverley.fileBrowser.dao.impl;

import com.waverley.fileBrowser.dao.api.LocalDAO;
import com.waverley.fileBrowser.dao.api.RemouteDAO;
import com.waverley.fileBrowser.dto.PropertyHolder;
import com.waverley.fileBrowser.enums.Option;
import com.waverley.fileBrowser.helper.Toolses;
import com.waverley.fileBrowser.model.UserAuth;
import jcifs.smb.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import static com.waverley.fileBrowser.enums.Option.COPY;
import static com.waverley.fileBrowser.enums.Option.MOVE;

/**
 * Created by Andrey on 6/8/2017.
 */

@Component
public class RemouteDAOimpl implements RemouteDAO {

    @Autowired
    private PropertyHolder propertyHolder;

    @Autowired
    private Toolses toolses;

    @Autowired
    LocalDAO localDAO;

    @Override
    public SmbFile getFile(String path) {
        Authentication au = SecurityContextHolder.getContext().getAuthentication();

        UserAuth userAuth = (UserAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        String user = userAuth.getUserLogin();
        String pass = userAuth.getUserPassword();

        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", user, pass);


        SmbFile smbFile = null;
        try {
            smbFile = new SmbFile(path, auth);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {

            String pathF = smbFile.getPath();
            String lastSymb = pathF.substring(pathF.length() - 1);

            boolean res = lastSymb.equals("/");
            if (smbFile.isDirectory() && !res) {
                smbFile = getFile(path + "/");
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }
        return smbFile;
    }

    @Override
    public boolean checkUser(String userLogin, String userPassword) {

        String path = propertyHolder.getRemouteURL();
        String user = userLogin;
        String pass = userPassword;
        boolean result = false;
        NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication("", user, pass);

        SmbFile smbFile = null;

        try {
            smbFile = new SmbFile(path, auth);
            result = smbFile.canRead();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SmbException f) {
            f.printStackTrace();
        }
        return result;
    }

    //get all files and folders from server throw smb protocol
    @Override
    public ArrayList<SmbFile> getFolderContent(SmbFile smbFile) throws SmbException {

        SmbFile[] smbFiles = new SmbFile[0];

        smbFiles = smbFile.listFiles();

        ArrayList smbFilesList = new ArrayList();
        for (int i = 0; i < smbFiles.length; ++i) {
            if (!smbFiles[i].getName().contains(".ini")) {
                smbFilesList.add(smbFiles[i]);
            }
            try {
                if (smbFiles[i].isDirectory()) {
                    smbFilesList.addAll(this.getFolderContent(smbFiles[i]));
                }
            } catch (SmbException e) {
                e.printStackTrace();
            }
        }
        return smbFilesList;
    }

    @Override
    public void createFolder(String foldername) throws SmbException {

        SmbFile smbFile = null;
        smbFile = getFile(foldername);
        smbFile.mkdir();
    }

    @Override
    public void createImage(byte[] bytes, String fileName, String urlDirectoryForFolder) throws IOException {

        urlDirectoryForFolder = convertLocalFileAddressToRemoteFileAddress(urlDirectoryForFolder);
        String fileURL = urlDirectoryForFolder + fileName;
        SmbFileOutputStream smbFileDestOutputStream = smbFileDest(fileURL);
        try {
            smbFileDestOutputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                smbFileDestOutputStream.flush();
                smbFileDestOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createImageUpload(MultipartFile file, String newFileName, String urlDirectoryForFolder) {

        urlDirectoryForFolder = convertLocalFileAddressToRemoteFileAddress(urlDirectoryForFolder);
        String fileURL = urlDirectoryForFolder  +"/"+ newFileName;
        SmbFileOutputStream smbFileDestOutputStream = smbFileDest(fileURL);
        try {

        //    smbFileDestOutputStream.write(bytes);
            InputStream inputStream = file.getInputStream();
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                smbFileDestOutputStream.write(bytes, 0, read);
            }
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.getInputStream().close();
                smbFileDestOutputStream.flush();
                smbFileDestOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void copyImage(String fileName, String urlDirectoryForFolder, Option type) {
        urlDirectoryForFolder = convertLocalFileAddressToRemoteFileAddress(urlDirectoryForFolder);
        fileName = propertyHolder.getRemouteURL() + fileName;
        //      if (!urlDirectoryForFolder.substring(urlDirectoryForFolder.length() - 1).equals("/")) {
        //        urlDirectoryForFolder = urlDirectoryForFolder + "/";
        //     }
        SmbFile smbFile = getFile(fileName);
        SmbFile smbFileDest;
//
//        if (urlDirectoryForFolder.contains("///newName///")) {
//            urlDirectoryForFolder = urlDirectoryForFolder.replace("///newName///", "/");
        smbFileDest = getFile(urlDirectoryForFolder);
//        } else {
//            smbFileDest = getFile(urlDirectoryForFolder + smbFile.getName());
//        }
        try {

            if (type == COPY) {
                if (smbFile.isDirectory() && smbFileDest.getPath().contains(smbFile.getPath())) {
                    String specialFolderForCopy = "specialFolderForCopy/";
                    SmbFile smbFileSpecial = getFile(propertyHolder.getRemouteURL() + specialFolderForCopy);

                    smbFile.copyTo(smbFileSpecial);

                    smbFileSpecial.renameTo(smbFileDest);

                    smbFileDest.delete();

                } else {

                    smbFile.copyTo(smbFileDest);
                }


            } else if (type == MOVE) {
                if (smbFileDest.exists()) {
                    smbFileDest.delete();
                }
                smbFile.renameTo(smbFileDest);
            }

        } catch (SmbException e) {
            e.printStackTrace();
        }

    }

//    @Override
//    public void moveImage(String fileName, String urlDirectoryForFolder) {
//
//        urlDirectoryForFolder = convertLocalFileAddressToRemoteFileAddress(urlDirectoryForFolder);
//        fileName = convertLocalFileAddressToRemoteFileAddress(fileName);
//        SmbFile smbFile = getFile(fileName);
//        SmbFile smbFileDest;
//        if (!urlDirectoryForFolder.contains("_(copy_")) {
//            smbFileDest = getFile(urlDirectoryForFolder + smbFile.getName());
//        } else {
//            smbFileDest = getFile(urlDirectoryForFolder);
//        }
//        try {
//            smbFile.renameTo(smbFileDest);
//        } catch (SmbException e) {
//            e.printStackTrace();
//        }
//    }


    private SmbFileOutputStream smbFileDest(String fileName) {

        SmbFileOutputStream smbFileDestOutputStream = null;
        try {
            smbFileDestOutputStream = new SmbFileOutputStream(getFile(fileName));
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return smbFileDestOutputStream;
    }

    @Override
    public SmbFileInputStream showOriginalFile(String fileName) {

        fileName = checkForQuotes(fileName);

        String[] directories;
        if (fileName.contains(propertyHolder.getLocalRootFolder())) {
            String r = propertyHolder.getLocalRootFolder() + "\\\\";
            directories = fileName.split(r);
            fileName = directories[1];
        }

        String  path = propertyHolder.getRemouteURL() + fileName;
            path = path.replace("\\", "/");

            SmbFileInputStream inputStream = null;
            try {
                inputStream = new SmbFileInputStream(getFile(path));

            } catch (SmbException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return inputStream;

    }

    public String checkForQuotes(String fileName){

        if(fileName.startsWith("'") && fileName.endsWith("'")){
            fileName = fileName.substring(1, fileName.length()-1);
        }


        return fileName;
    }

    @Override
    public String convertLocalFileAddressToRemoteFileAddress(String localFileAddress) {

        String remouteURLdirectory = localFileAddress.replace(propertyHolder.getLocalURL(), propertyHolder.getRemouteURL());

        return remouteURLdirectory;

    }

    @Override
    public void deleteFolderContent(SmbFile smbFile) {

        String slesh = smbFile.getPath().substring(smbFile.getPath().length() - 1);
        try {
            if (slesh.equals("/")) {
                smbFile.delete();
            } else {
                getFile(smbFile.getPath() + "/").delete();
            }
        } catch (SmbException e) {
            e.printStackTrace();
        }
    }
}
