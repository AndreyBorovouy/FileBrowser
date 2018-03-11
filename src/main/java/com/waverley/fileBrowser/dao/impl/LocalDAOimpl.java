package com.waverley.fileBrowser.dao.impl;

import com.waverley.fileBrowser.dao.api.LocalDAO;
import com.waverley.fileBrowser.dto.FileInfo;
import com.waverley.fileBrowser.dto.PropertyHolder;
import com.waverley.fileBrowser.enums.Option;
import com.waverley.fileBrowser.helper.Toolses;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.waverley.fileBrowser.enums.Option.COPY;
import static com.waverley.fileBrowser.enums.Option.MOVE;

/**
 * Created by Andrey on 6/8/2017.
 */
@Component
public class LocalDAOimpl implements LocalDAO {

    public static String staus;

    @Autowired
    private FileInfo fileInfo;
    @Autowired
    private Toolses toolses;
    @Autowired
    private PropertyHolder propertyHolder;

    //use recurtion get all files folders from local directory

    @Override
    public List<File> getAllFilesFoldersFromPreviewFolder(String pathFolderWithPreview) {
        List<File> res = new LinkedList<>();
        File file = new File(pathFolderWithPreview);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            res.add(files[i]);
            if (files[i].isDirectory()) {
                String pathFolderWithPreviewInside = files[i].getPath();
                ArrayList<File> filesList2 = new ArrayList<File>();
                res.addAll(getAllFilesFoldersFromPreviewFolder(pathFolderWithPreviewInside));
            }
        }
        return res;
    }

    @Override
    public List<String> getFolderContentName(String folderAddress) {
        List<String> folderFilesNamesList = new ArrayList<String>();
        File dir = new File(folderAddress);
        for (File file : dir.listFiles()) {
            folderFilesNamesList.add(file.getName());
        }
        return folderFilesNamesList;
    }

    @Override
    public ArrayList<String> checkFolderforUploadFiles(List<MultipartFile> files, String folderDestination) throws UnsupportedEncodingException {
        ArrayList<String> equalFileNameList = new ArrayList<String>();
        List<String> folderFilesNamesList = getFolderContentName(folderDestination);
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileName = file.getOriginalFilename();
            if (folderFilesNamesList.contains(fileName)) {
                equalFileNameList.add(fileName);
            }
        }
        return equalFileNameList;
    }

    @Override
    public ArrayList<String> checkFolderForCopyFiles(List<String> files, String folderDestination) {
        ArrayList<String> equalFileNameList = new ArrayList<String>();
        List<String> folderFilesNamesList = getFolderContentName(folderDestination);
        for (int i = 0; i < files.size(); i++) {

            String[] fileName = files.get(i).split("/");
            //fileName

            if (folderFilesNamesList.contains(fileName[fileName.length - 1])) {
                equalFileNameList.add(fileName[fileName.length - 1]);
            }
        }
        return equalFileNameList;
    }

    //create previews and folders in locale directories
    @Override
    public void createPreviewContent(List<SmbFile> smbFileArrayList, String rootFolder) {

        for (int i = 0; i < smbFileArrayList.size(); i++) {

            //devide full url of the smbFile, for receivinig relatively way of the file
            String[] partesOftheSourceWay = smbFileArrayList.get(i).getCanonicalPath().split(propertyHolder.getRemouteRootFolder());
            //combine local root derectory with  elatively way of the file
            String filenameWithWay = rootFolder.concat(partesOftheSourceWay[1]);
            filenameWithWay = filenameWithWay.replaceAll("//", "/");
            try {
                //create New File

                boolean res = false;
                if (!smbFileArrayList.get(i).getName().substring(0, 1).equals("\\.")){
                    res = true;
                };

                if (!smbFileArrayList.get(i).isDirectory() && (res)) {

                    int bytes = smbFileArrayList.get(i).getContentLength();
                    //getting original file size
                    int kilobytes = (bytes / 1024);
                    int megabytes = (kilobytes / 1024);

//                    new File(filenameWithWay).createNewFile();
                    try (SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFileArrayList.get(i));) {
                        if(checkFileTypeBeforeShow(smbFileArrayList.get(i).getName())) {
                            toolses.saveScaledImage(smbFileInputStream, filenameWithWay);
                            smbFileInputStream.close();
                        }else{
                            toolses.createDefoultPreview(filenameWithWay);
                        }
                    }

                } else if (smbFileArrayList.get(i).isDirectory()) {
                    new File(filenameWithWay).mkdir();
                } else {

                }
            } catch (IOException e) {
                e.printStackTrace();
                //create New Folder
                partesOftheSourceWay = filenameWithWay.split("/");
                String[] folderDestination = ArrayUtils.remove(partesOftheSourceWay, partesOftheSourceWay.length - 1);
                String addressOfTheFile = StringUtils.join(folderDestination, "/");
                new File(addressOfTheFile).mkdir();
                //create New File again
                try {
                    new File(filenameWithWay).createNewFile();
                    try (SmbFileInputStream smbFileInputStream = new SmbFileInputStream(smbFileArrayList.get(i));) {
                        toolses.saveScaledImage(smbFileInputStream, filenameWithWay);
                        smbFileInputStream.close();
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void smartCheckCompareSmbFilesAndFilesPreview(List<SmbFile> smbFileList, List<File> fileList) {
        ArrayList<String> previewNames = new ArrayList<>();
        ArrayList<String> smbFileNames = new ArrayList<>();

        for (int i = 0; i < smbFileList.size(); i++) {
            String[] partesOfsmbFile = smbFileList.get(i).getCanonicalPath().split(propertyHolder.getRemouteRootFolder());
            smbFileNames.add(partesOfsmbFile[1]);
        }

        for (int i = 0; i < fileList.size(); i++) {

            String changeSlash = fileList.get(i).getPath().replace("\\", "/");
            String[] partesOftheFilepreview = changeSlash.split(propertyHolder.getLocalRootFolder());

            //check address of local directory it can be without /
            if (fileList.get(i).isDirectory()) {
                String slesh = fileList.get(i).getPath().substring(fileList.get(i).getPath().length() - 1);
                if (!slesh.equals("/")) {
                    partesOftheFilepreview[1] = partesOftheFilepreview[1] + "/";
                }
            }
            previewNames.add(partesOftheFilepreview[1]);
        }

        // Create Preview
        ArrayList<SmbFile> smbFileForPreview = new ArrayList<>();
        for (int i = 0; i < smbFileNames.size(); i++) {
            if (!previewNames.contains(smbFileNames.get(i))) {
                smbFileForPreview.add(smbFileList.get(i));
            }
        }

        createPreviewContent(smbFileForPreview, propertyHolder.getLocalURL());

        // Delete the preview

        for (int i = 0; i < previewNames.size(); i++) {
            if (!smbFileNames.contains(previewNames.get(i))) {
                try {
                    if (!fileList.get(i).delete()) {
                        FileUtils.deleteDirectory(new File(fileList.get(i).getPath()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void createImagePreviewUpdate(MultipartFile file, String newFileName, String localURL) {
        String filenameWithWay = localURL.concat("/" + newFileName);
        if(checkFileType(file.getContentType()) != null) {
            try (InputStream inputStream = file.getInputStream()) {
                toolses.saveScaledImage(inputStream, filenameWithWay);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            try {
                toolses.createDefoultPreview(filenameWithWay);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void copyImagePreview(String fileSource, String folderDest, Option type) {

        String path = folderDest;

        fileSource = propertyHolder.getLocalURL() + fileSource;
        File file = new File(fileSource);
        File dest;

//        if (path.contains("///newName///")) {
//            path = path.replace("///newName///", "/");
        dest = new File(path);
//        } else {
//            dest = new File(path + "/" + file.getName());
//        }

        if (type == COPY) {
            try {
                if (file.isDirectory()) {
                    FileUtils.copyDirectory(file, dest);
                } else {
                    Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (type == MOVE) {
            if (dest.exists()) {
                if (dest.isDirectory()) {
                    deleteFolderContent(dest.getPath());
                    dest.delete();
                } else {
                    dest.delete();
                }

            }
            file.renameTo(dest);
        }
    }

    @Override
    public void createFolder(String nameFolder) {

        new File(nameFolder).mkdir();
    }

    @Override
    public ArrayList<FileInfo> getContent(String folderURl) {

        if (!folderURl.contains(propertyHolder.getLocalURL())) {
            folderURl = propertyHolder.getLocalURL() + folderURl;
        }

        ArrayList<FileInfo> fileInfosArrayList = new ArrayList<>();
        File[] files = new File(folderURl).listFiles();

        if (files == null) {
            // the method should stop work in this case
            fileInfosArrayList = new ArrayList<FileInfo>();
        } else {
            for (File file : files) {
                if (!file.isDirectory()) {
                    try (InputStream fileInputStream = new FileInputStream(file)) {

                        fileInfo = new FileInfo();
                        String furl = file.getCanonicalPath().replaceAll("\\\\", "/").split(propertyHolder.getLocalRootFolder())[1];
                        String name = file.getName();
                        byte[] data = IOUtils.toByteArray(fileInputStream);
                        String image = new String(Base64.encode(data));
                        if(checkFileTypeBeforeShow(name)) {

                            fileInfo.setName(name);
                            fileInfo.setLocalurl(furl);
                            fileInfo.setImage(image);
                        }else{
                            fileInfo.setName(name + "enotherSpecialFile");
                            fileInfo.setLocalurl(furl);
                            fileInfo.setImage(image);
                        }
                        fileInfosArrayList.add(fileInfo);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (file.isDirectory()) {
                    try {
                        fileInfo = new FileInfo();
                        String furl = file.getCanonicalPath().replaceAll("\\\\", "/").split(propertyHolder.getLocalRootFolder())[1];

                        String name = file.getName();

                        fileInfo.setName(name);
                        fileInfo.setLocalurl(furl);
                        fileInfo.setImage("folder");
                        fileInfosArrayList.add(fileInfo);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return fileInfosArrayList;
    }

    @Override
    public void deleteFolderContent(String folderURl) {

        File file = new File(folderURl);
        String path = file.getPath();
        try {
            FileUtils.deleteDirectory(new File(file.getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean result = false;
        while (!result) {
            result = new File(path).mkdir();
        }

    }

    public String checkFileType(String fileName) {

        String result = null;
        List<String> formats = Arrays.asList("jpg", "jpeg", "tif", "tiff", "png", "gif", "bmp", "dib");

        for (String format : formats) {
            if (fileName.toLowerCase().contains(format)) {
                result = format;
                break;
            }
        }

        return result;

    }

    public boolean checkFileTypeBeforeShow(String fileName) {

        boolean result = false;

        String[] parts = fileName.split("\\.");
        if(parts.length>1){
            String type = checkFileType(parts[parts.length-1]);
            if(type != null){
                result = true;
            }
        }
        return result;

    }

}
