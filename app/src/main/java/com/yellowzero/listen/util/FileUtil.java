package com.yellowzero.listen.util;

import android.text.TextUtils;

public class FileUtil {
    public static String getSuffix(String path) {
        if (TextUtils.isEmpty(path))
            return null;
        int index = path.lastIndexOf(".");
        if (index == -1) {
            return null;
        } else
            return path.substring(index + 1).toLowerCase();
    }

    public static String getPrefix(String fileName) {
        if (null == fileName) {
            return null;
        }
        int len = fileName.length();
        if (0 == len) {
            return fileName;
        }
        if (fileName.charAt(len - 1) == '/') {
            len--;
        }

        int begin = 0;
        int end = len;
        char c;
        for (int i = len - 1; i >= 0; i--) {
            c = fileName.charAt(i);
            if (len == end && '.' == c) {
                // 查找最后一个文件名和扩展名的分隔符：.
                end = i;
            }
            // 查找最后一个路径分隔符（/或者\），如果这个分隔符在.之后，则继续查找，否则结束
            if (c == '/') {
                begin = i + 1;
                break;
            }
        }
        return fileName.substring(begin, end);
    }
}
