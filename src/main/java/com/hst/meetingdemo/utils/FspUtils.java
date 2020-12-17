package com.hst.meetingdemo.utils;

import java.text.DecimalFormat;

public class FspUtils {
    public static boolean isSameText(String str1, String str2) {
        if(str1 == null && str2 == null) {
            return true;
        }
        else if (str1 == null || str2 == null) {
            return false;
        }

        return str1.equals(str2);
    }

    public static boolean isEmptyText(String str) {
        return str == null || str.isEmpty();
    }


    public static final long sKB =  1024;
    public static final long sMB =  sKB * sKB;
    public static final long sGB =  sMB * sKB;

    private static final String sUnitB = "B";
    private static final String sUnitKB = "K";
    private static final String sUnitMB = "M";
    private static final String sUnitGB = "G";

    private static final DecimalFormat sFormat = new DecimalFormat("#.0");

    /**
     * 字节大小转换成 K M之类可读大小
     * @param byteSize byte
     * @return human size
     */
    public static String convertBytes2HumanSize(long byteSize) {
        String sSize;
        if(byteSize > sGB) {
            sSize = sFormat.format((float)byteSize / sGB ) + sUnitGB;
        } else if(byteSize > sMB) {
            sSize = sFormat.format((float)byteSize / sMB ) + sUnitMB;
        } else if(byteSize > sKB) {
            sSize = sFormat.format((float)byteSize / sKB ) + sUnitKB;
        } else {
            sSize = byteSize + sUnitB;
        }
        return sSize;
    }
}
