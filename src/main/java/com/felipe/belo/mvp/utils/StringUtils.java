package com.felipe.belo.mvp.utils;

public  abstract class  StringUtils {

    public  static String normalizeString(String string){
        return string == null ? null : string.trim().toLowerCase();
    }
}