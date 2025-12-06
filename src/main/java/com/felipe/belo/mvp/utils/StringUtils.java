package com.felipe.belo.mvp.utils;

/**
 * String utility helpers.
 */
public abstract class StringUtils {

    /**
     * Normalizes a string by trimming and lowercasing it. Returns null if input is null.
     *
     * @param string input string
     * @return normalized string or null
     */
    public static String normalizeString(String string){
        return string == null ? null : string.trim().toLowerCase();
    }

    /** Hidden constructor. */
    private StringUtils() {}
}