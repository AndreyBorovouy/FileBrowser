package com.waverley.fileBrowser.dto;

public class UserDTO {

    private int id;
    private String login;
    private String password;
    private String fullCheck;
    private String smartCheck;
    private String upload;
    private String download;
    private String copy;
    private String move;
    private String delete;
    private String createFolder;
    private String rename;
    private String createUser;
    private String editUser;
    private String deleteUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullCheck() {
        return fullCheck;
    }

    public void setFullCheck(String fullCheck) {
        this.fullCheck = fullCheck;
    }

    public String getSmartCheck() {
        return smartCheck;
    }

    public void setSmartCheck(String smartCheck) {
        this.smartCheck = smartCheck;
    }

    public String getUpload() {
        return upload;
    }

    public void setUpload(String upload) {
        this.upload = upload;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }

    public String getCopy() {
        return copy;
    }

    public void setCopy(String copy) {
        this.copy = copy;
    }

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public String getCreateFolder() {
        return createFolder;
    }

    public void setCreateFolder(String createFolder) {
        this.createFolder = createFolder;
    }

    public String getRename() {
        return rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getEditUser() {
        return editUser;
    }

    public void setEditUser(String editUser) {
        this.editUser = editUser;
    }

    public String getDeleteUser() {
        return deleteUser;
    }

    public void setDeleteUser(String deleteUser) {
        this.deleteUser = deleteUser;
    }


    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", fullCheck=" + fullCheck +
                ", smartCheck=" + smartCheck +
                ", upload=" + upload +
                ", download=" + download +
                ", copy=" + copy +
                ", move=" + move +
                ", delete=" + delete +
                ", createFolder=" + createFolder +
                ", rename=" + rename +
                ", createUser=" + createUser +
                ", editUser=" + editUser +
                ", deleteUser=" + deleteUser +
                '}';
    }

}
