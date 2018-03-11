package com.waverley.fileBrowser.service.impl;

import com.waverley.fileBrowser.dao.api.LocalDAO;
import com.waverley.fileBrowser.dao.api.RemouteDAO;
import com.waverley.fileBrowser.dto.FileInfo;
import com.waverley.fileBrowser.dto.PropertyHolder;
import com.waverley.fileBrowser.enums.Option;
import com.waverley.fileBrowser.exceptions.UploadResult;
import com.waverley.fileBrowser.service.api.FileService;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.net.SimpleSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import static com.waverley.fileBrowser.enums.Option.NO;
import static com.waverley.fileBrowser.enums.Option.OVERWRITE;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private RemouteDAO remouteDAO;
    @Autowired
    private LocalDAO localDAO;
    @Autowired
    private PropertyHolder propertyHolder;

    private static final String TEMPLATE = "[0-9A-Za-z-)(_-]*";
    private static final String COPYWORD = "(copy_";

    @Override
    public SmbFileInputStream getOriginalFile(String fileName) throws UnsupportedEncodingException {
        return remouteDAO.showOriginalFile(fileName);
    }

    @Override
    public ArrayList<FileInfo> getContent(String folderURl) throws UnsupportedEncodingException {

        ArrayList<FileInfo> fileInfosArrayList = localDAO.getContent(folderURl);
        return fileInfosArrayList;
    }

    @Override
    public UploadResult uploadFile(ArrayList<MultipartFile> files, String folderDestination, Option option) throws UnsupportedEncodingException {
        UploadResult res = null;

        folderDestination = propertyHolder.getLocalURL() + folderDestination;

        if (folderDestination.contains("//")) {
            folderDestination = folderDestination.replace("//", "/");
        }

        List<String> equalFileNameList = localDAO.checkFolderforUploadFiles(files, folderDestination);
        if (equalFileNameList.size() == 0 || option != NO) {
            if (option == NO) {
                option = OVERWRITE;
            }
            switch (option) {
                case SKIP:
                    equalFileNameList = uploadAndSkip(files, equalFileNameList, folderDestination);
                    break;
                case OVERWRITE:
                    equalFileNameList = uploadAndOverwrite(files, folderDestination);
                    break;
                case RENAME:
                    equalFileNameList = uploadAndRename(files, equalFileNameList, folderDestination);
                    break;
                //todo maybe need to returne defoult
            }
            if (equalFileNameList.size() != 0) {
                res = new UploadResult("error", equalFileNameList);
            } else {
                res = new UploadResult("OK", new ArrayList<String>());
            }
        } else {
            res = new UploadResult("duplicate", equalFileNameList);
        }
        return res;
    }

    @Override
    public void smartCheck() throws SmbException {

        SmbFile smbFile = remouteDAO.getFile(propertyHolder.getRemouteURL());
        List<SmbFile> smbFileList = remouteDAO.getFolderContent(smbFile);
        List<File> fileList = localDAO.getAllFilesFoldersFromPreviewFolder(propertyHolder.getLocalURL());

        localDAO.smartCheckCompareSmbFilesAndFilesPreview(smbFileList, fileList);
    }

    @Override
    public void createLocalPreviews() throws SmbException {

        SmbFile smbFile = remouteDAO.getFile(propertyHolder.getRemouteURL());
        List<SmbFile> smbFileArrayList = remouteDAO.getFolderContent(smbFile);
        localDAO.deleteFolderContent(propertyHolder.getLocalURL());
        localDAO.createPreviewContent(smbFileArrayList, propertyHolder.getLocalURL());

    }

    @Override
    public ByteArrayOutputStream getArchiveFile(String files, OutputStream outputStream) throws UnsupportedEncodingException {

        String[] fileArray = files.split(";");
        List<String> listOfFiles = Arrays.asList(fileArray);

        //   ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(outputStream);


        for (int i = 0; i < listOfFiles.size(); i++) {

            String remouteFileAddress = propertyHolder.getRemouteURL() + listOfFiles.get(i);
            String[] fullname = remouteFileAddress.split("/");
            String actualRootFolder = fullname[fullname.length - 2] + "/";

            SmbFile smbFile = remouteDAO.getFile(remouteFileAddress);

            try {
                if (smbFile.isDirectory()) {
                    ArrayList<SmbFile> smbFileList = remouteDAO.getFolderContent(smbFile);
                    for (int j = 0; j < smbFileList.size(); j++) {
                        if (!smbFileList.get(j).isDirectory()) {
                            addFileToArchiveInOutputStream(smbFileList.get(j), zos, actualRootFolder);
                            //    addFileToArchiveInOutputStream2(smbFileList.get(j), baos, actualRootFolder);
                        }
                    }
                } else {
                    // addFileToArchiveInOutputStream2(smbFile, baos, actualRootFolder);
                    addFileToArchiveInOutputStream(smbFile, zos, actualRootFolder);
                }
            } catch (SmbException e) {
                e.printStackTrace();
            }
        }
        try {
            zos.close();
            //  baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String renameFile(String newName, String localUrl) throws UnsupportedEncodingException {

        String result = validateFileFolderName(newName);
        if (result != null) {
            // the method should stop work in this case and return warning message
            // return result;
        } else {
            localUrl = propertyHolder.getLocalURL() + localUrl;
            File oldNameF = new File(localUrl);
            if (!oldNameF.isDirectory()) {
                String oldname = oldNameF.getName();
                String[] endingArr = oldname.split("[.]");
                if (endingArr.length == 1) {

                    newName = newName + ".jpg";
                } else {
                    String ending = endingArr[1];
                    newName = newName + "." + ending;
                }
            }
            String[] m = localUrl.split("/");
            String a = StringUtils.join(m, "/", 0, m.length - 1);
            File newNameF = new File(a + "/" + newName);

            oldNameF.renameTo(newNameF);

            String oldRremouteURL = remouteDAO.convertLocalFileAddressToRemoteFileAddress(localUrl);
            String newRremouteURL = remouteDAO.convertLocalFileAddressToRemoteFileAddress(a + "/" + newName);

            SmbFile oldNameSMBfile = remouteDAO.getFile(oldRremouteURL);
            SmbFile newNameSMBfile = remouteDAO.getFile(newRremouteURL);
            try {
                oldNameSMBfile.renameTo(newNameSMBfile);
            } catch (SmbException e) {
                e.printStackTrace();
            }
            result = null;
        }
        return result;
    }

    @Override
    public String createNewFolder(String destination, String nameFolder) throws SmbException, UnsupportedEncodingException {

        String result = validateFileFolderName(nameFolder);
        if (result == null) {
            // the method should stop work in this case and return warning message
            // return result;
            //  } else {
            String slesh = destination.substring(destination.length() - 1);
            if (!slesh.equals("/")) {
                destination = destination + "/";
            }
            nameFolder = destination + nameFolder;

            remouteDAO.createFolder(propertyHolder.getRemouteURL() + nameFolder);
            localDAO.createFolder(propertyHolder.getLocalURL() + nameFolder);

        }
        return result;
    }

    @Override
    public UploadResult copy(String listOfFiles, String folderDestination, Option option, Option type) throws UnsupportedEncodingException {

        UploadResult res = null;
        folderDestination = propertyHolder.getLocalURL() + folderDestination;

        List<String> filesList = Arrays.asList(listOfFiles.split(";"));
        List<String> equalFileNameList = localDAO.checkFolderForCopyFiles(filesList, folderDestination);
        if (equalFileNameList.size() == 0 || option != NO) {
            if (option == NO) {
                option = OVERWRITE;
            }
            switch (option) {
                case SKIP:
                    copyAndSkip(filesList, equalFileNameList, folderDestination, type);
                    break;
                case OVERWRITE:
                    copyAndOverwrite(filesList, folderDestination, type);
                    break;
                case RENAME:
                    copyAndRename(filesList, equalFileNameList, folderDestination, type);
                    break;
            }
            res = new UploadResult("OK", new ArrayList<String>());

        } else {
            res = new UploadResult("duplicate", equalFileNameList);
        }
        return res;
    }

    @Override
    public void deleteContent(String filesFolders) throws UnsupportedEncodingException {

        String[] fileArray = filesFolders.split(";");

        for (int i = 0; i < fileArray.length; i++) {

// delete local files folders
            String local = propertyHolder.getLocalURL() + fileArray[i];
            File file = new File(local);
            if (file.isDirectory()) {
                localDAO.deleteFolderContent(local);
            }
            file.delete();

            String remouteFileAddress = propertyHolder.getRemouteURL() + fileArray[i];
            SmbFile smbFile = remouteDAO.getFile(remouteFileAddress);
            try {
                if (smbFile.isDirectory()) {
                    remouteDAO.deleteFolderContent(smbFile);
                } else {
                    smbFile.delete();
                }
            } catch (SmbException e) {
                e.printStackTrace();
            }
        }
    }

    private ArrayList<String> uploadAndSkip(List<MultipartFile> files, List<String> equalFileNameList, String folderDestination) throws UnsupportedEncodingException {
        ArrayList<String> badFilesName = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileName = file.getOriginalFilename();
            if (!equalFileNameList.contains(fileName)) {
                try {
                   // createImages(file, fileName, folderDestination);
                    createImagesUpload(file, fileName, folderDestination);
                } catch (IOException e) {
                    badFilesName.add(fileName);
                }
            }
        }
        return badFilesName;
    }


    private ArrayList<String> uploadAndOverwrite(List<MultipartFile> files, String folderDestination) throws UnsupportedEncodingException {
        ArrayList<String> badFilesName = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileName = file.getOriginalFilename();
            try {
//                createImages(file, fileName, folderDestination);
                createImagesUpload(file, fileName, folderDestination);
            } catch (IOException e) {
                badFilesName.add(fileName);
            }
        }
        return badFilesName;
    }

    private ArrayList<String> uploadAndRename(List<MultipartFile> files, List<String> equalFileNameList, String folderDestination) throws UnsupportedEncodingException {
        ArrayList<String> badFilesName = new ArrayList<>();
        List<String> folderFilesNamesList = localDAO.getFolderContentName(folderDestination);
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String fileName = file.getOriginalFilename();
            if (equalFileNameList.contains(fileName)) {
                fileName = renameAndCheckUploadFile(folderFilesNamesList, fileName, 1);
            }
            try {
//                createImages(file, fileName, folderDestination);

                createImagesUpload(file, fileName, folderDestination);
            } catch (IOException e) {
                badFilesName.add(fileName);
            }
        }
        return badFilesName;
    }

    private void copyAndSkip(List<String> arrOfFiles, List<String> equalFileNameList, String folderDestination, Option type) {
        for (int i = 0; i < arrOfFiles.size(); i++) {
            String file = arrOfFiles.get(i);
            String[] fileName = file.split("/");
            String name = getNameFromFileURL(file);
            if (!equalFileNameList.contains(fileName[fileName.length - 1])) {
                copyImages(file, folderDestination + name, type);
            }
        }
    }

    private void copyAndOverwrite(List<String> arrOfFiles, String folderDestination, Option type) {
        for (int i = 0; i < arrOfFiles.size(); i++) {
            String file = arrOfFiles.get(i);
            String name = getNameFromFileURL(file);
            copyImages(file, folderDestination + "/" + name, type);
        }
    }


    private void copyAndRename(List<String> arrOfFiles, List<String> equalFileNameList, String folderDestination, Option type) {
        List<String> folderFilesNamesList = localDAO.getFolderContentName(folderDestination);
        for (int i = 0; i < arrOfFiles.size(); i++) {

            String fileName = arrOfFiles.get(i);
            String name = getNameFromFileURL(fileName);

            if (equalFileNameList.contains(name)) {
                //copyImages(fileName, folderDestination + "///newName///" + name, type);
                name = renameAndCheckUploadFile(folderFilesNamesList, name, 1);
                copyImages(fileName, folderDestination + name, type);// todo create two diff methods for file and folder coping
            } else {
                //          copyImages(fileName, folderDestination , type);
                copyImages(fileName, folderDestination + "/" + name, type);
            }
        }
    }

    private String getNameFromFileURL(String fileURL) {

        String[] nameArr = fileURL.split("/");
        return nameArr[nameArr.length - 1];
    }


    private String renameAndCheckUploadFile(List<String> folderFilesNamesList, String fileNameI, int copy) {
        fileNameI = renameUploadFileNew(fileNameI, copy);
        if (folderFilesNamesList.contains(fileNameI)) {
            fileNameI = renameAndCheckUploadFile(folderFilesNamesList, fileNameI, copy + 1);
        }
        return fileNameI;
    }


    private String renameUploadFile(String fileName, int copy) {

        //separating ending "png" by "."
        String[] p1 = fileName.split("\\.");
        fileName = p1[0];
        // check the file name contains string _(copy_
        if (fileName.contains(COPYWORD)) {
            //separating filename by "_(COPYWORD"
            String[] p2 = fileName.split("_\\(copy_");
            //try receive only numbers, delete ")"
            String num = p2[1].substring(0, p2[1].length() - 1);
            try {
                //try convert string number to int number and add 1
                int a = Integer.parseInt(num) + 1;
                if (p1.length == 2) {
                    fileName = p2[0] + COPYWORD + a + ")." + p1[1];
                } else if (p1.length == 1) {
                    fileName = p2[0] + COPYWORD + a + ")";
                }
            } catch (Exception e) {
                // if in the brackets not a number, just add this _(copy_1) to the end of file name
                fileName = fileName + COPYWORD + copy + ")." + p1[1];
            }
        } else {
            //add this _(copy_1) to the end of file name
            if (p1.length == 2) {
                fileName = fileName + COPYWORD + copy + ")." + p1[1];
            } else if (p1.length == 1) {
                fileName = fileName + COPYWORD + copy + ")";
            }
        }

        return fileName;
    }

    private String renameUploadFileNew(String fileName, int copy) {

        //separating ending "png" by "."
//        String[] p1 = fileName.split("\\.");
//        fileName = p1[0];
        // check the file name contains string _(copy_
        if (fileName.startsWith(COPYWORD)) {

            try {
                String newFileName = fileName.substring(6, fileName.length());
                int num = newFileName.indexOf(")");

                String number = newFileName.substring(0, num);
                newFileName = newFileName.substring(num + 1);
                num = Integer.parseInt(number) + 1;

                fileName = COPYWORD + num + ")" + newFileName;

            } catch (Exception e) {
                // if in the brackets not a number, just add this _(copy_1) to the end of file name
                fileName = COPYWORD + copy + ")" + fileName;
            }
        } else {
            //add this _(copy_1) to the end of file name
//            if (p1.length == 2) {
//                fileName = fileName + COPYWORD + copy + ")." + p1[1];
//            } else if (p1.length == 1) {
//                fileName = fileName + COPYWORD + copy + ")";
//            }
            fileName = COPYWORD + copy + ")" + fileName;
        }

        return fileName;
    }

//    private void createImages(MultipartFile file, String fileName, String folderDestination) throws IOException {
//        // TODO: 2/2/2018  переписать работу кнопки аплоад
//        // TODO: 2/2/2018 дописать спин во время аплоад
//        // file in stream
//        byte[] bytes = file.getBytes();
//        remouteDAO.createImage(bytes, fileName, folderDestination);
//        localDAO.createImagePreview(bytes, fileName, folderDestination);
//
//    }

    private void createImagesUpload(MultipartFile file, String newFileName, String folderDestination) throws IOException {

        remouteDAO.createImageUpload(file, newFileName,  folderDestination);
        localDAO.createImagePreviewUpdate(file, newFileName,  folderDestination);

    }

    private void copyImages(String filesAddress, String folderDestination, Option type) {
        remouteDAO.copyImage(filesAddress, folderDestination, type);
        localDAO.copyImagePreview(filesAddress, folderDestination, type);
    }


    @Override
    public ArrayList<String> getAllFolders() {

        ArrayList<File> fileList = new ArrayList<>();
        //  fileList = localDAO.getAllFilesFoldersFromPreviewFolder(propertyHolder.getLocalURL(), fileList);
        fileList.addAll(localDAO.getAllFilesFoldersFromPreviewFolder(propertyHolder.getLocalURL()));
        ArrayList<String> folders = new ArrayList<>();
        for (File file : fileList) {
            if (file.isDirectory()) {

                String a = file.getPath().replace("\\", "/");
                String[] path = a.split(propertyHolder.getLocalRootFolder());
                folders.add(path[1]);
            }
        }

        return folders;
    }


    private String validateFileFolderName(String fileFolderName) {

        String template = TEMPLATE;

        String result = null;

        if (fileFolderName.length() < 1 || fileFolderName.length() > 15) {
            result = "File or folder name length should be from 1 - 15 symbols";
        }

        Pattern pattern = Pattern.compile(template);
        Matcher matcher = pattern.matcher(fileFolderName);

        if (!matcher.matches()) {
            result = "File or folder name should contain only letters and numbers";
        }
        return result;
    }

    private void addFileToArchiveInOutputStream(SmbFile smbFile, ZipOutputStream zos, String actualRootFolder) {

        SmbFileInputStream smbFileInputStream = null;
        try {
            smbFileInputStream = new SmbFileInputStream(smbFile);
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String[] fullname = smbFile.getPath().split(actualRootFolder);
        String name = fullname[1];

        try {
            //ZipEntry entry = new ZipEntry(Name);
            ZipEntry entry = new ZipEntry(name);
            zos.putNextEntry(entry);

            byte[] bytes = new byte[1024];
            int length;
            while ((length = smbFileInputStream.read(bytes)) >= 0) {
                zos.write(bytes, 0, length);
            }

            zos.closeEntry();
            smbFileInputStream.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    private void addFileToArchiveInOutputStream2(SmbFile smbFile, ByteArrayOutputStream baos, String actualRootFolder) {

        SmbFileInputStream smbFileInputStream = null;
        try {
            smbFileInputStream = new SmbFileInputStream(smbFile);
        } catch (SmbException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        String[] fullname = smbFile.getPath().split(actualRootFolder);
        String name = fullname[1];

        byte[] bytes = new byte[1024];
        int length;
        try {
            while ((length = smbFileInputStream.read(bytes)) >= 0) {
                baos.write(bytes, 0, length);
            }

            baos.close();
            smbFileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


}
