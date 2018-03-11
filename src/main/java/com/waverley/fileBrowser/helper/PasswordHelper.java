package com.waverley.fileBrowser.helper;

import org.springframework.security.crypto.password.PasswordEncoder;

import javax.inject.Named;
import java.security.MessageDigest;

/**
 * Created by Andrey on 11/17/2017.
 */
@Named
public class PasswordHelper implements PasswordEncoder {

    private MessageDigest md;

    @Override
    public String encode(CharSequence rawPassword) {

        if (md == null) {
            return rawPassword.toString();
        }
        md.update(rawPassword.toString().getBytes());
        byte byteData[] = md.digest();
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            String hex = Integer.toHexString(0xff & byteData[i]);
            if (hex.length() == 1) {
                hexString.append("0");
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public boolean matches(CharSequence rowPassword, String encodedPassword) {
        return rowPassword.equals(encodedPassword);
    }
}
