package com.dtstack.rdos.engine.execution.spark210.sqlproxy;

import com.google.common.base.Charsets;
import org.apache.parquet.Strings;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

    public void runJob(String submitSql, String appName){

        if(appName == null){
            appName = DEFAULT_APP_NAME;
        }

        SparkSession spark = SparkSession
                .builder()
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

    public static void main(String[] args) throws UnsupportedEncodingException {

        if(args.length < 1){
            logger.error("must set args for sql job!!!");
            throw new RuntimeException("must set args for sql job!!!");
        }

        SqlProxy sqlProxy = new SqlProxy();
        String argInfo = args[0];
        argInfo = URLDecoder.decode(argInfo, Charsets.UTF_8.name());
        logger.info("----sql:{}", argInfo);


        Map<String, Object> argsMap = null;
        try{
            argsMap = objMapper.readValue(argInfo, Map.class);
        }catch (Exception e){
            logger.error("", e);
            throw new RuntimeException("parse args json error, message " + e.getMessage());
        }

        String sql = (String) argsMap.get("sql");
        String appName = argsMap.get("appName") == null ? null : (String) argsMap.get("appName");
        sqlProxy.runJob(sql, appName);
    }
}
