package com.ccz.myvillage.common;

public class StrUtils {

    public static String getFileExt(String fileName) {
        String extension = "";
        int i = fileName.lastIndexOf('.');
        if(i >0) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }
}
