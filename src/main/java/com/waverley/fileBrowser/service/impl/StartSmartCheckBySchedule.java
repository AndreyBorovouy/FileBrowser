package com.waverley.fileBrowser.service.impl;

import com.waverley.fileBrowser.service.api.FileService;
import jcifs.smb.SmbException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Andrey on 1/3/2018.
 */
@Component
public class StartSmartCheckBySchedule {

    @Autowired
    static FileService fileService;
    private static boolean result = true;
    private static int date;

    private static StartSmartCheckBySchedule INSTANCE = new StartSmartCheckBySchedule();

    private static int a= 0;

    public StartSmartCheckBySchedule() {

    }

    @Scheduled(cron = "0 0 1 * *  ?")
    //- срабатывает в час ночи
    public static void startBySchedule() {

        Calendar calendar = Calendar.getInstance();

        int actualDate = calendar.get(Calendar.DAY_OF_MONTH);

        if (result && (date != actualDate)) {
            try {
                fileService.smartCheck();
            } catch (SmbException e) {
                e.printStackTrace();
            }
            date = actualDate;
        }
    }

    @Scheduled(fixedRate = 6000)
    public static void test() {
        Calendar calendar = Calendar.getInstance();

        int actualDate = calendar.get(Calendar.DAY_OF_MONTH);

        if (result && (date != actualDate)) {
            date = actualDate;
        }
    }

    public static boolean isResult() {
        return result;
    }

    public static void setResult(boolean result) {
        StartSmartCheckBySchedule.result = result;
    }
}
