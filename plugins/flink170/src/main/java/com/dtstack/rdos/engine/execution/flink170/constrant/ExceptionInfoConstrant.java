package com.dtstack.engine.dtscript.execution.flink170.constrant;

import com.dtstack.rods.engine.execution.base.resource.EngineResourceInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by sishu.yss on 2018/6/27.
 */
public class ExceptionInfoConstrant {

    private static Logger logger = LoggerFactory.getLogger(ExceptionInfoConstrant.class);

    public final static String FLINK_ENGINE_DOWN_RESTART_EXCEPTION = "Could not connect to the leading JobManager";

    public final static String FLINK_NO_RESOURCE_AVAILABLE_RESTART_EXCEPTION = "org.apache.flink.runtime.jobmanager.scheduler.NoResourceAvailableException: Not enough free slots available to run the job";

    public final static String FLINK_TASK_LOST_RESTART_EXCEPTION = "TaskManager was lost/killed";

    public final static String FLINK_AKKA_VERSION_RESTART_EXCEPTION = "No configuration setting found for key 'akka.version'";

    public final static String FLINK_RELEASE_SHARED_SLOT_RESTART_EXCEPTION = "Releasing shared slot parent";

    public final static String JDBC_LINK_FAILURE_RESTART_EXCEPTION = "com.mysql.jdbc.exceptions.jdbc4.CommunicationsException: Communications link failure";

    public final static String JOBMGR_NOT_RESPONSE_RESTART_EXCEPTION = "JobTimeoutException: JobManager did not respond within";

    public final static String FUTURES_TIME_OUT_RESTART_EXCEPTION = "java.util.concurrent.TimeoutException: Futures timed out after";

    public final static String  UPLOAD_RESTART_EXCEPTION= "Could not upload the program's JAR files to the JobManager";

    public final static String  IN_INITIALIZER_RESTART_EXCEPTION = "java.lang.ExceptionInInitializerError";

    public final static String  LEASE_EXPIRED_RESTART_EXCEPTION = "org.apache.hadoop.ipc.RemoteException(org.apache.hadoop.hdfs.server.namenode.LeaseExpiredException)";

    public final static String  AKKA_ASK_TIMEOUT_RESTART_EXCEPTION = "akka.pattern.AskTimeoutException";

    public final static String RESOURCE_OVER_LIMIT_RESTART_EXCEPTION = EngineResourceInfo.LIMIT_RESOURCE_ERROR;


    private static List<String> needRestartExceptions = Lists.newArrayList();

    static {
        try{
            Field[] fields = ExceptionInfoConstrant.class.getDeclaredFields();
            for(Field f:fields){
                String name = f.getName();
                if(name.indexOf("RESTART_EXCEPTION")>=0){
                    needRestartExceptions.add(f.get(f.getName()).toString());
                }
            }
        }catch (Throwable e){
            logger.error("",e);
        }
    }


    public static List<String> getNeedRestartException(){
        return needRestartExceptions;
    }

    public static void main(String[] args){
        System.out.println(getNeedRestartException());
    }
}
