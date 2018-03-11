package com.waverley.fileBrowser.service.impl;

import com.waverley.fileBrowser.dto.AdminPropertyHolder;
import com.waverley.fileBrowser.dto.AnonymousPropertiesHolder;
import com.waverley.fileBrowser.dto.Read_writeUserPropertiesHolder;
import com.waverley.fileBrowser.model.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//BEST WAY FOR THIS TIME DON'T TUCH THIS CLASS

@Component
public class AuthService {
    @Autowired
    AnonymousPropertiesHolder anonymousPropertiesHolder;
    @Autowired
    Read_writeUserPropertiesHolder propertiesHolder;
    @Autowired
    private AdminPropertyHolder adminPropertyHolder;

    private static long timeChange;


    public boolean checkTimeChanging() {

        boolean result = false;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

            UserAuth userAuth = (UserAuth) authentication.getPrincipal();

            if (userAuth != null && userAuth.getTimeChanging() != timeChange && !userAuth.isSuperAdmin()) {

                String userLogin = userAuth.getUserLogin();
                result = true;
                if (userLogin.equals(anonymousPropertiesHolder.getAnonymousUser()) || userLogin.equals("") || userLogin.equals(null)) {
//                    SecurityContextHolder.getContext().getAuthentication().getAuthorities().retainAll(getGuestAuthority());
                    authenticate();

                } else if (!userLogin.equals(adminPropertyHolder.getAdminLogin())) {
//                    SecurityContextHolder.getContext().getAuthentication().getAuthorities().retainAll(getReadWriteAuthority());
                    authenticateReadWriteUser(userAuth.getUserLogin(), userAuth.getUserPassword());
                }
            }
            //if session expire
            //  else if(!authentication.isAuthenticated() && !userAuth.getUserLogin().equals(adminPropertyHolder.getAdminLogin())){

            //    authenticate();
            //}
        } else {
            authenticate();
        }

        return result;
    }

    public void authenticateAdminUser(String username, String password) throws UsernameNotFoundException {

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        ArrayList<String> userFields = new ArrayList<>();

        if (adminPropertyHolder.isFullCheck()) {
            userFields.add("fullCheck");
        }
        if (adminPropertyHolder.isSmartCheck()) {
            userFields.add("smartCheck");
        }
        if (adminPropertyHolder.isDownload()) {
            userFields.add("download");
        }
        if (adminPropertyHolder.isCopy()) {
            userFields.add("copy");
        }
        if (adminPropertyHolder.isCreateFolder()) {
            userFields.add("createFolder");
        }
        if (adminPropertyHolder.isDelete()) {
            userFields.add("delete");
        }
        if (adminPropertyHolder.isUpload()) {
            userFields.add("upload");
        }
        if (adminPropertyHolder.isMove()) {
            userFields.add("move");
        }
        if (adminPropertyHolder.isRename()) {
            userFields.add("rename");
        }
        if (adminPropertyHolder.isAccess()) {
            userFields.add("access");
        }
        if (adminPropertyHolder.isEditUser()) {
            userFields.add("editUser");
        }

        for (String rights : userFields) {
            grantedAuthorities.add(new SimpleGrantedAuthority(rights));
        }
        UserAuth userAuth = new UserAuth(username, password, timeChange);
        userAuth.setIsSuperAdmin(true);
        Authentication token =
                new UsernamePasswordAuthenticationToken(userAuth, password, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void authenticateReadWriteUser(String username, String password) throws UsernameNotFoundException {

        Set<GrantedAuthority> grantedAuthorities = getReadWriteAuthority();

//        ArrayList<String> userFields = new ArrayList<>();
//
//        if (propertiesHolder.isFullCheck()) {
//            userFields.add("fullCheck");
//        }
//        if (propertiesHolder.isSmartCheck()) {
//            userFields.add("smartCheck");
//        }
//        if (propertiesHolder.isDownload()) {
//            userFields.add("download");
//        }
//        if (propertiesHolder.isCopy()) {
//            userFields.add("copy");
//        }
//        if (propertiesHolder.isCreateFolder()) {
//            userFields.add("createFolder");
//        }
//        if (propertiesHolder.isDelete()) {
//            userFields.add("delete");
//        }
//        if (propertiesHolder.isUpload()) {
//            userFields.add("upload");
//        }
//        if (propertiesHolder.isMove()) {
//            userFields.add("move");
//        }
//        if (propertiesHolder.isRename()) {
//            userFields.add("rename");
//        }
//        if (propertiesHolder.isAccess()) {
//            userFields.add("access");
//        }
//
//        for (String rights : userFields) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(rights));
//        }
        Authentication token =
                new UsernamePasswordAuthenticationToken(new UserAuth(username, password, timeChange), password, grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public void authenticate() {

        String username = null;

        if (SecurityContextHolder.getContext().getAuthentication() != null) {

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }
        }
        if ((username == null || username.equals(anonymousPropertiesHolder.getAnonymousUser()) || username.equals("anonymousUser"))) {

            Set<GrantedAuthority> grantedAuthorities = getGuestAuthority();
//            ArrayList<String> userFields = new ArrayList<>();
//
//            if (anonymousPropertiesHolder.isFullCheck()) {
//                userFields.add("fullCheck");
//            }
//            if (anonymousPropertiesHolder.isSmartCheck()) {
//                userFields.add("smartCheck");
//            }
//            if (anonymousPropertiesHolder.isDownload()) {
//                userFields.add("download");
//            }
//            if (anonymousPropertiesHolder.isAccess()) {
//                userFields.add("access");
//            }
//            for (String rights : userFields) {
//                grantedAuthorities.add(new SimpleGrantedAuthority(rights));
//            }

            Authentication token =
                    new UsernamePasswordAuthenticationToken(new UserAuth(anonymousPropertiesHolder.getAnonymousUser(), anonymousPropertiesHolder.getAnonymousPass(), timeChange), anonymousPropertiesHolder.getAnonymousPass(), grantedAuthorities);

            SecurityContextHolder.getContext().setAuthentication(token);

        }
    }

    private Set<GrantedAuthority> getGuestAuthority() {

        anonymousPropertiesHolder.getActualData();

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        ArrayList<String> userFields = new ArrayList<>();

        if (anonymousPropertiesHolder.isFullCheck()) {
            userFields.add("fullCheck");
        }
        if (anonymousPropertiesHolder.isSmartCheck()) {
            userFields.add("smartCheck");
        }
        if (anonymousPropertiesHolder.isDownload()) {
            userFields.add("download");
        }
        if (anonymousPropertiesHolder.isAccess()) {
            userFields.add("access");
        }
        for (String rights : userFields) {
            grantedAuthorities.add(new SimpleGrantedAuthority(rights));
        }
        return grantedAuthorities;
    }

    private Set<GrantedAuthority> getReadWriteAuthority() {

        propertiesHolder.getActualData();

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        ArrayList<String> userFields = new ArrayList<>();

        if (propertiesHolder.isFullCheck()) {
            userFields.add("fullCheck");
        }
        if (propertiesHolder.isSmartCheck()) {
            userFields.add("smartCheck");
        }
        if (propertiesHolder.isDownload()) {
            userFields.add("download");
        }
        if (propertiesHolder.isCopy()) {
            userFields.add("copy");
        }
        if (propertiesHolder.isCreateFolder()) {
            userFields.add("createFolder");
        }
        if (propertiesHolder.isDelete()) {
            userFields.add("delete");
        }
        if (propertiesHolder.isUpload()) {
            userFields.add("upload");
        }
        if (propertiesHolder.isMove()) {
            userFields.add("move");
        }
        if (propertiesHolder.isRename()) {
            userFields.add("rename");
        }
        if (propertiesHolder.isAccess()) {
            userFields.add("access");
        }

        for (String rights : userFields) {
            grantedAuthorities.add(new SimpleGrantedAuthority(rights));
        }

        return grantedAuthorities;

    }


    public long getTimeChange() {
        return timeChange;
    }

    public void setTimeChange(long timeChange) {
        this.timeChange = timeChange;

    }

}



