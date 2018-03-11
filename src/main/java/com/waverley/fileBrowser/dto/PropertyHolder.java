package com.waverley.fileBrowser.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by Andrey on 6/8/2017.
 */

@Component
@PropertySource("classpath:properties/application.properties")
public class PropertyHolder {

    @Value("${local.URL}")
    private String localURL;
    @Value("${remoute.URL}")
    private String remouteURL;
    @Value("${local.rootFolder}")
    private String localRootFolder;
    @Value("${remoute.rootFolder}")
    private String remouteRootFolder;


    public String getLocalURL() {
        return localURL;
    }

    public String getRemouteURL() {
        return remouteURL;
    }

    public String getLocalRootFolder() {
        return localRootFolder;
    }

    public String getRemouteRootFolder() {
        return remouteRootFolder;
    }





}
