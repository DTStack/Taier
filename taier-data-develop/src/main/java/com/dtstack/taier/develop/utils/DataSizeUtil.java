package com.dtstack.taier.develop.utils;

import java.text.DecimalFormat;

/**
 * 数据大小工具类
 */
public class DataSizeUtil {

    public static final String[] UNIT_NAMES = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};

    /**
     * 可读的文件大小<br>
     * 参考 http://stackoverflow.com/questions/3263892/format-file-size-as-mb-gb-etc
     *
     * @param size Long类型大小
     * @return 大小
     */
    public static String format(Long size) {
        if (size == null || size <= 0) {
            return "0";
        }
        int digitGroups = Math.min(UNIT_NAMES.length - 1, (int) (Math.log10(size) / Math.log10(1024)));
        return new DecimalFormat("#,##0.##")
                .format(size / Math.pow(1024, digitGroups)) + UNIT_NAMES[digitGroups];
    }

    public static void main(String[] args) {
        System.out.println(format(100L));
    }
}
