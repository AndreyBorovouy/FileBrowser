package com.waverley.fileBrowser.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Andrey on 11/28/2017.
 */
@Component
@PropertySource("classpath:properties/admin.properties")
public class AdminPropertyHolder {

    @Value("${admin.login}")
    private String adminLogin;
    @Value("${fullCheck}")
    private boolean fullCheck;
    @Value("${smartCheck}")
    private boolean smartCheck;
    @Value("${upload}")
    private boolean upload;
    @Value("${download}")
    private boolean download;
    @Value("${access}")
    private boolean access;
    @Value("${copy}")
    private boolean copy;
    @Value("${move}")
    private boolean move;
    @Value("${delete}")
    private boolean delete;
    @Value("${createFolder}")
    private boolean createFolder;
    @Value("${rename}")
    private boolean rename;
    @Value("${editUser}")
    private boolean editUser;

    public boolean isEditUser() {
        return editUser;
    }

    public String getAdminLogin() {
        return adminLogin;
    }

    public boolean isFullCheck() {
        return fullCheck;
    }

    public boolean isSmartCheck() {
        return smartCheck;
    }

    public boolean isUpload() {
        return upload;
    }

    public boolean isDownload() {
        return download;
    }

    public boolean isAccess() {
        return access;
    }

    public boolean isCopy() {
        return copy;
    }

    public boolean isMove() {
        return move;
    }

    public boolean isDelete() {
        return delete;
    }

    public boolean isCreateFolder() {
        return createFolder;
    }

    public boolean isRename() {
        return rename;
    }


}
