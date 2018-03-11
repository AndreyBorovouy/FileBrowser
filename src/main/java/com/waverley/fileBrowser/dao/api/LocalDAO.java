package com.waverley.fileBrowser.dao.api;

import com.waverley.fileBrowser.dto.FileInfo;
import com.waverley.fileBrowser.enums.Option;
import jcifs.smb.SmbFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrey on 9/29/2017.
 */
public interface LocalDAO {

    //use recurtion get all files folders from local directory

    List<String> getFolderContentName(String folderAddress);

    List<String> checkFolderforUploadFiles(List<MultipartFile> files, String folderDestination) throws UnsupportedEncodingException;

    List<String> checkFolderForCopyFiles(List<String> files, String folderDestination);

    //create previews and folders in locale directories
    void createPreviewContent(List<SmbFile> smbFileArrayList, String rootFolder);

    void smartCheckCompareSmbFilesAndFilesPreview(List<SmbFile> smbFileList, List<File> fileList);



    void copyImagePreview(String fileSource, String folderDest, Option type);

    void createFolder(String nameFolder);

    ArrayList<FileInfo> getContent(String folderURl);

    void deleteFolderContent(String folderURl);

    List<File> getAllFilesFoldersFromPreviewFolder(String localURL);

    void createImagePreviewUpdate(MultipartFile file, String newFileName, String folderDestination);

    boolean checkFileTypeBeforeShow(String fileName);
}
