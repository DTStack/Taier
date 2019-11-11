package com.dtstack.engine.dtscript.execution.spark160.sparkyarn.enums;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

public class ExceptionInfoConstrant {

    private static Logger logger = LoggerFactory.getLogger(ExceptionInfoConstrant.class);

    public final static String SPARK_ENGINE_DOWN_RESTART_EXCEPTION = "Current state is not alive: STANDBY";

    public final static String TREENODE_RESTART_EXCEPTION = "org.apache.spark.sql.catalyst.errors.package$TreeNodeException: execute, tree";

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
