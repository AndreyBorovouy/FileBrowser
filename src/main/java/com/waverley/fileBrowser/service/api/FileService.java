package com.waverley.fileBrowser.service.api;

import com.waverley.fileBrowser.dto.FileInfo;
import com.waverley.fileBrowser.enums.Option;
import com.waverley.fileBrowser.exceptions.UploadResult;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFileInputStream;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * Created by Andrey on 9/29/2017.
 */
public interface FileService {

    SmbFileInputStream getOriginalFile(String fileName) throws UnsupportedEncodingException;

    ArrayList<FileInfo> getContent(String folderURl) throws UnsupportedEncodingException;

    UploadResult uploadFile(ArrayList<MultipartFile> files, String folderDestination, Option option) throws UnsupportedEncodingException;

     void smartCheck() throws SmbException;

     void createLocalPreviews() throws SmbException;



    ByteArrayOutputStream getArchiveFile(String files, OutputStream outputStream) throws UnsupportedEncodingException;

    String renameFile(String newName, String localurl) throws UnsupportedEncodingException;

     String createNewFolder(String destination, String nameFolder) throws SmbException, UnsupportedEncodingException;

    UploadResult  copy(String listOfFiles, String folderDestination, Option option, Option type) throws UnsupportedEncodingException;

     void deleteContent(String filesFolders) throws UnsupportedEncodingException;

    ArrayList<String> getAllFolders();

}
