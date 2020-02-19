package com.dtstack.sql.main;

import com.dtstack.sql.main.util.DtStringUtil;
import com.dtstack.sql.main.util.ZipUtil;
import com.google.common.base.Charsets;
import org.apache.spark.sql.CarbonSession;
import org.apache.spark.sql.SparkSession;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * carbondata 执行sql任务代理类
 * 需要单独打成jar 放到hdfs上 用于执行sql的时候调用
 * Date: 2019/01/21
 * Company: www.dtstack.com
 * @author xuchao
 */

public class CarbondataSqlProxy {

    private static final Logger logger = LoggerFactory.getLogger(SqlProxy.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final String DEFAULT_APP_NAME = "carbondata_default_name";

    private static final String SQL_KEY = "sql";

    private static final String APP_NAME_KEY = "appName";

    private static final String STORE_PATH_KEY = "storePath";

    public void runJob(String submitSql, String appName, String storePath){

        if(appName == null){
            appName = DEFAULT_APP_NAME;
        }

        if(storePath == null || "".equals(storePath.trim())){
            throw new RuntimeException("carbon data must specify storePath");
        }


        SparkSession.Builder builder = SparkSession.builder()
                .appName(appName)
                .enableHiveSupport();

        SparkSession carbonSession = CarbonSession.CarbonBuilder(builder)
                .getOrCreateCarbonSession(storePath);

        //解压sql
        String unzipSql = ZipUtil.unzip(submitSql);

        //屏蔽引号内的 分号
        List<String> sqlArray = DtStringUtil.splitIgnoreQuota(unzipSql, ';');
        for(String sql : sqlArray){
            if(sql == null || sql.trim().length() == 0){
                continue;
            }

            carbonSession.sql(sql);
        }

        carbonSession.close();
    }

    public static void main(String[] args) throws UnsupportedEncodingException {

        if(args.length < 1){
            logger.error("must set args for sql job!!!");
            throw new RuntimeException("must set args for sql job!!!");
        }

        CarbondataSqlProxy sqlProxy = new CarbondataSqlProxy();
        String argInfo = args[0];
        argInfo = URLDecoder.decode(argInfo, Charsets.UTF_8.name());

        Map<String, Object> argsMap = null;
        try{
            argsMap = OBJECT_MAPPER.readValue(argInfo, Map.class);
        }catch (Exception e){
            logger.error("", e);
            throw new RuntimeException("parse args json error, message: " + argInfo, e);
        }

        String sql = (String) argsMap.get(SQL_KEY);
        String appName = argsMap.get(APP_NAME_KEY) == null ? null : (String) argsMap.get(APP_NAME_KEY);
        String storePath = argsMap.get(STORE_PATH_KEY) == null ? null : (String)argsMap.get(STORE_PATH_KEY);
        sqlProxy.runJob(sql, appName, storePath);
    }
}

