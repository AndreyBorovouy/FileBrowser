package com.waverley.fileBrowser.service.impl;

import com.waverley.fileBrowser.model.UserAuth;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

//BEST WAY FOR THIS TIME DON'T TUCH THIS CLASS
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        ArrayList<String> userFields = new ArrayList<>();

        userFields.add("fullCheck");
        userFields.add("smartCheck");
        userFields.add("download");
        userFields.add("upload");
        userFields.add("copy");
        userFields.add("move");
        userFields.add("delete");
        userFields.add("createFolder");
        userFields.add("editUser");
        userFields.add("rename");
        userFields.add("access");

        for (String rights : userFields) {
            grantedAuthorities.add(new SimpleGrantedAuthority(rights));
        }

        return new org.springframework.security.core.userdetails.User("admin", "1234", grantedAuthorities);

    }
}
