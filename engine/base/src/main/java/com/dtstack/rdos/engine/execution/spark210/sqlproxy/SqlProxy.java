package com.dtstack.rdos.engine.execution.spark210.sqlproxy;

import com.dtstack.rdos.commom.exception.RdosException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.parquet.Strings;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * spark sql 代理执行类
 * 需要单独打成jar 放到hdfs上 用于执行sql的时候调用
 * Date: 2017/4/11
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SqlProxy {

    private static final Logger logger = LoggerFactory.getLogger(SqlProxy.class);

    private static final ObjectMapper objMapper = new ObjectMapper();

    private static final String DEFAULT_APP_NAME = "spark_default_name";

    private static String jobMem = "512m";//默认job使用内存大小512m

    public void runJob(String submitSql, String appName){

        if(appName == null){
            appName = DEFAULT_APP_NAME;
        }
        SparkSession spark = SparkSession
                .builder()
                .config("spark.executor.memory", jobMem)
                .appName(appName)
                .enableHiveSupport()
                .getOrCreate();

        String[] sqlArray = submitSql.split(";");
        for(String sql : sqlArray){
            if(Strings.isNullOrEmpty(sql)){
                continue;
            }

            spark.sql(sql);
        }

        spark.close();
    }

    public static void main(String[] args) {

        if(args.length < 1){
            logger.error("must set args for sql job!!!");
            throw new RdosException("must set args for sql job!!!");
        }

        SqlProxy sqlProxy = new SqlProxy();
        String argInfo = args[0];
        Map<String, Object> argsMap = null;
        try{
            argsMap = objMapper.readValue(argInfo, Map.class);
        }catch (Exception e){
            logger.error("", e);
            throw new RdosException("parse args json error, message " + e.getMessage());
        }

        String sql = (String) argsMap.get("sql");
        String appName = argsMap.get("appName") == null ? null : (String) argsMap.get("appName");
        sqlProxy.runJob(sql, appName);
    }
}
