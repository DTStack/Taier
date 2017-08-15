package com.dtstack.rdos.engine.util;

/**
 * Created by sishu.yss on 2017/5/15.
 */
public class TaskIdUtil {

    private static  String interval = "_";

    public static String getZkTaskId(int computeType, String engineType, String taskId){
        return String.valueOf(computeType) + engineType + interval + taskId;
    }

    public static String getTaskId(String zkTaskId){
        return zkTaskId.substring(zkTaskId.indexOf(interval) + 1, zkTaskId.length());
    }

    public static int getComputeType(String zkTaskId){
       return Integer.parseInt(String.valueOf(zkTaskId.charAt(0)));
    }

    public static String getEngineType(String zkTaskId){
        return zkTaskId.substring(1, zkTaskId.indexOf(interval));
    }

    public static void main(String[] args) {
        String str = "1flink_dfefef";
        System.out.println(getTaskId(str));
    }

}
