package com.yellowzero.listen.util;

import android.text.TextUtils;

import java.io.File;
import java.text.DecimalFormat;

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

    public static String getFileSizeString(File f) {
        return formatFileSize(getFileSize(f));
    }

    public static long getFileSize(File f) {
        long size = 0;
        File[] files = f.listFiles();
        if (files == null)
            return 0;
        for (File file : files)
            if (file.isDirectory())
                size = size + getFileSize(file);
            else
                size = size + file.length();
        return size;
    }

    public static String formatFileSize(long fileS) {//转换文件大小
        if (fileS == 0)
            return "0";
        DecimalFormat df = new DecimalFormat("#.00");
        if (fileS < 1024)
            return df.format((double) fileS) + "B";
        if (fileS < 1048576)
            return df.format((double) fileS / 1024) + "K";
        if (fileS < 1073741824)
            return df.format((double) fileS / 1048576) + "M";
        return df.format((double) fileS / 1073741824) + "G";
    }

    /**
     * 删除文件，可以是文件或文件夹
     *
     * @param fileName
     *            要删除的文件名
     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("删除文件失败:" + fileName + "不存在！");
            return false;
        } else {
            if (file.isFile())
                return deleteFile(fileName);
            else
                return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件
     *
     * @param fileName
     *            要删除的文件的文件名
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    /**
     * 删除目录及目录下的文件
     *
     * @param dir
     *            要删除的目录的文件路径
     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator))
            dir = dir + File.separator;
        File dirFile = new File(dir);
        // 如果dir对应的文件不存在，或者不是一个目录，则退出
        if (!dirFile.exists())
            return true;
        if (!dirFile.isDirectory())
            return false;
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]
                        .getAbsolutePath());
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            System.out.println("删除目录失败！");
            return false;
        }
        // 删除当前目录
        if (dirFile.delete()) {
            System.out.println("删除目录" + dir + "成功！");
            return true;
        } else {
            return false;
        }
    }
}
