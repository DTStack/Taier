package com.dtstack.rdos.common.config;

import com.dtstack.rdos.common.util.AddressUtil;
import com.dtstack.rdos.common.util.MathUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.List;
/**
 * Created by sishu.yss on 2017/12/18.
 */
public class ConfigParse {

    public static final String TYPE_NAME_KEY = "typeName";

    private static Map<String,Object> configs = null;

    public static void setConfigs(Map<String,Object> config){
        if(ConfigParse.configs==null){
            ConfigParse.configs = config;
        }
    }

    public static int getSlots(){
        Object slots = configs.get("slots");
        return slots==null?10:Integer.parseInt(slots.toString());
    }


    public  static String getLocalAddress(){
        String localAddress = (String)configs.get("localAddress");
        if(StringUtils.isBlank(localAddress)){
            localAddress = String.format("%s:%s",AddressUtil.getOneIP(),"8090");
        }
        return localAddress;
    }

    public static String getNodeZkAddress(){
        Object nodeZkAddress = configs.get("nodeZkAddress");
        return (String)nodeZkAddress;
    }


    public static int getEventLoopPoolSize(){
        Object eventLoopPoolSize = configs.get("eventLoopPoolSize");
        return eventLoopPoolSize == null?2 * Runtime.getRuntime().availableProcessors():Integer.parseInt(eventLoopPoolSize.toString());
    }

    public static int getInstances(){
        Object instances = configs.get("instances");
        return instances == null?2 * Runtime.getRuntime().availableProcessors():Integer.parseInt(instances.toString());
    }

    public static int getWorkerPoolSize(){
        Object workerPoolSize = configs.get("workerPoolSize");
        return workerPoolSize == null?1000:Integer.parseInt(workerPoolSize.toString());
    }

    public static Object getSecurity(){
        Object isSecurity = configs.get("security");
        return isSecurity;
    }

    public static List<Map<String,Object>> getEngineTypeList(){

        List<Map<String, Object>> engineList =  (List<Map<String, Object>>) configs.get("engineTypes");
        return engineList == null ? Lists.newArrayList() : engineList;
    }

    public static int getExeQueueSize(){
        Object exeQueueSize = configs.get("exeQueueSize");
        return exeQueueSize == null ? 1 : MathUtil.getIntegerVal(exeQueueSize);
    }

    public static boolean isDebug(){
        Object isDebug = configs.get("isDebug");
        return isDebug == null ? false : MathUtil.getBoolean(isDebug);
    }

    public static Map<String,String> getPluginStoreInfo(){
        Object storeInfo = configs.get("pluginStoreInfo");
        return storeInfo == null?getDB():(Map<String, String>)storeInfo;
    }

    public static Map<String, String> getDB(){
        Map<String, String> db = (Map<String, String>)configs.get("db");
        return db;
    }

    public static int getTaskDistributeQueueWeight() {
        Object taskDistributeQueueWeight = configs.get("taskDistributeQueueWeight");
        return taskDistributeQueueWeight==null ? 10 : MathUtil.getIntegerVal(taskDistributeQueueWeight);
    }

    public static int getTaskDistributeZkWeight() {
        Object taskDistributeZkWeight = configs.get("taskDistributeZkWeight");
        return taskDistributeZkWeight==null ? 0 : MathUtil.getIntegerVal(taskDistributeZkWeight);
    }

    public static int getTaskDistributeDeviation() {
        Object taskDistributeDeviation = configs.get("taskDistributeDeviation");
        return taskDistributeDeviation==null ? 3 : MathUtil.getIntegerVal(taskDistributeDeviation);
    }

    public static int getShardSize() {
        Object shardSize = configs.get("shardSize");
        return shardSize==null ? 200 : MathUtil.getIntegerVal(shardSize);
    }

    public static int getQueueSize() {
        Object queueSize = configs.get("queueSize");
        return queueSize == null ? 500 : MathUtil.getIntegerVal(queueSize);
    }

    public static int getJobStoppedRetry(){
        Object retry = configs.get("jobStoppedRetry");
        return retry == null ? 10 : Integer.parseInt(retry.toString());
    }
    public static long getJobStoppedDelay(){
        Object delay = configs.get("jobStoppedDelay");
        return delay == null ? 3000 : Long.parseLong(delay.toString());
    }
}
