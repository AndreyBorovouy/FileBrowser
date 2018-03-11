package com.waverley.fileBrowser.service.api;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface SecurityService {

    String findLoggedInUsername();

    void autoLogin(String username, String password);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
