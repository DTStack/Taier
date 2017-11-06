package com.dtstack.rdos.engine.util;

import com.dtstack.rdos.common.util.MathUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by sishu.yss on 2017/5/15.
 */
public class TaskIdUtil {

    private static final Logger logger = LoggerFactory.getLogger(TaskIdUtil.class);

    private final static String interval = "_";

    private final static String MIGRATION_FLAG = "1";

    public static String getZkTaskId(int computeType, String engineType, String taskId){
        return String.valueOf(computeType) + engineType + interval + taskId;
    }

    public static String getTaskId(String zkTaskId){
        String[] splitArr = zkTaskId.split(interval);
        if(splitArr.length < 2){
            logger.error("it's illegal zkTaskId {}.", zkTaskId);
            return "";
        }

        return splitArr[1].trim();
    }

    public static String convertToMigrationJob(String zkTaskId){
        String[] splitArr = zkTaskId.split(interval);
        if(splitArr.length < 3){
            return zkTaskId + interval + "1";
        }

        splitArr[2] = MIGRATION_FLAG;
        return String.join(interval, splitArr);
    }

    public static int getComputeType(String zkTaskId){
       return Integer.parseInt(String.valueOf(zkTaskId.charAt(0)));
    }

    public static String getEngineType(String zkTaskId){
        return zkTaskId.substring(1, zkTaskId.indexOf(interval));
    }

    public static boolean isMigrationJob(String zkTaskId){
        String[] splitArr = zkTaskId.split(interval);
        if(splitArr.length < 3){
            return false;
        }

        if(MathUtil.getIntegerVal(splitArr[2]) == 0){
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        String str = "1flink_dfefef";
        String str2 = "1flink_dfefef_0";
        System.out.println(isMigrationJob(str2));
        System.out.println(convertToMigrationJob(str));
    }

}
