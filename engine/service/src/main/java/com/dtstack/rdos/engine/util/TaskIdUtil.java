package com.dtstack.rdos.engine.util;

/**
 * Created by sishu.yss on 2017/5/15.
 */
public class TaskIdUtil {

    private static  String interval = "_";

    public static String getZkTaskId(int computeType,int engineType,String taskId){
        return String.valueOf(computeType) + String.valueOf(engineType) +interval+taskId;
    }

    public static int getComputeType(String zkTaskId){
       return Integer.parseInt(String.valueOf(zkTaskId.charAt(0)));
    }

    public static int getEngineType(String zkTaskId){
        return Integer.parseInt(String.valueOf(zkTaskId.charAt(1)));
    }

    public static void main(String[] args){

       System.out.println(getZkTaskId(1,1,"wrwerw"));
    }

}
