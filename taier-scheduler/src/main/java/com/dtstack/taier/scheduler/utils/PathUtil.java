package com.dtstack.taier.scheduler.utils;

/**
 * zk路径校验类
 *
 * @author qiuyun
 * @version 1.0
 * @date 2022-01-18 20:12
 */
public class PathUtil {
    private static final String PATH_SPLIT_CHAR = "/";

    private static final String PATH_SPLIT_CHAR1 = "\\";

    public static String getPath(String... strs){
        StringBuilder stringBuilder = new StringBuilder();
        for (String str : strs) {
            stringBuilder.append(PATH_SPLIT_CHAR).append(str.trim());
        }
        return stringBuilder.toString();
    }

    public static String[] splitPath(String path){
        path = path.substring(1);
        return path.split(PATH_SPLIT_CHAR);
    }

    public static void check(String src, String name){
        if(src == null || "".equals(src.trim())){
            throw new RuntimeException(name +" not empty!");
        }
        //判断“/”,"\"，因为zk是路径
        if(src.contains(PATH_SPLIT_CHAR) || src.contains(PATH_SPLIT_CHAR1)){
            throw new RuntimeException(name +" can not contains \"/\" and \"\\\"!");
        }
    }

}
