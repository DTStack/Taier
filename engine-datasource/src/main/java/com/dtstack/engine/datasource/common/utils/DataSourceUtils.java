package com.dtstack.engine.datasource.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.dtstack.dtcenter.common.util.Base64Util;
import com.dtstack.engine.datasource.common.constant.FormNames;
import com.dtstack.engine.datasource.common.exception.PubSvcDefineException;
import dt.insight.plat.lang.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 有关解析数据源工具类
 * @description:
 * @author: liuxx
 * @date: 2021/3/24
 */
@Slf4j
public class DataSourceUtils {

    public static final String KERBEROS_FILE = "kerberosFile";
    public static final String KERBEROS_CONFIG = "kerberosConfig";
    public static final String OPEN_KERBEROS = "openKerberos";
    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";
    /**
     * Kerberos 文件上传的时间戳
     */
    public static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";

    /**
     * 解析 dataJson 参数
     *
     * @param base64Str
     * @return
     */
    public static JSONObject getDataSourceJson(String base64Str) {
        if (Strings.isNullOrEmpty(base64Str)) {
            return new JSONObject();
        }
        try {
            boolean isJson = JSONValidator.from(base64Str).validate();
            if (isJson) {
                // 是json字符串
                return JSONObject.parseObject(base64Str);
            } else {
                // 非json字符串
                return JSONObject.parseObject(Base64Util.baseDecode(base64Str));
            }
        } catch (Exception e) {
            log.error("数据源信息解码异常", e);
            throw new PubSvcDefineException("数据源信息解码异常", e);
        }
    }



    /**
     * 解析 dataJson密文为Json字符串
     * @param base64Str
     * @return
     */
    public static String getDataSourceJsonStr(String base64Str) {
        return getDataSourceJson(base64Str).toJSONString();
    }

    /**
     * Base64加密dataJson，可选是否加密
     * @param dataJson json对象
     * @param isEncode 是否加密
     * @return
     */
    public static String getEncodeDataSource(JSONObject dataJson, Boolean isEncode) {
        if (Objects.isNull(dataJson)) {
            return "";
        }
        if (isEncode) {
            return Base64Util.baseEncode(dataJson.toJSONString());
        }
        return dataJson.toJSONString();
    }

    /**
     * Base64加密dataJson字符串, 可选是否加密
     * @param dataJson
     * @param isEncode
     * @return
     */
    public static String getEncodeDataSource(String dataJson, Boolean isEncode) {
        if (Strings.isNullOrEmpty(dataJson)) {
            return "";
        }
        if (JSONValidator.from(dataJson).validate() && isEncode) {
            // 是json字符串
            JSONObject jsonObject = JSONObject.parseObject(dataJson);
            return getEncodeDataSource(jsonObject, true);
        }
        return dataJson;
    }

    /**
     * 初始化 Kafka Kerberos 服务信息
     *
     * @param serviceName
     * @return
     */
    public static Map<String, String> initKafkaKerberos(String serviceName) {
        Map<String, String> kafkaSettings = new HashMap<>();
        kafkaSettings.put("security.protocol", "SASL_PLAINTEXT");
        kafkaSettings.put("sasl.mechansim", "GSSAPI");
        kafkaSettings.put(FormNames.SASL_KERBEROS_SERVICE_NAME, serviceName);
        return kafkaSettings;
    }

    /**
     * 解析数据库中存储的连接字段
     *
     * @param dataJson
     */
    public static void parseDataJson(JSONObject dataJson) {
        if (StringUtils.isNotBlank(dataJson.getString(FormNames.PASSWORD))) {
            dataJson.put("password", dataJson.getString(JDBC_PASSWORD));
            dataJson.remove(JDBC_PASSWORD);
        }
        if (StringUtils.isNotBlank(dataJson.getString(JDBC_USERNAME))) {
            dataJson.put("username", dataJson.getString(JDBC_USERNAME));
            dataJson.remove(JDBC_USERNAME);
        }
        if (StringUtils.isNotBlank(dataJson.getString(JDBC_URL))) {
            dataJson.put("jdbcUrl", dataJson.getString(JDBC_URL));
            dataJson.remove(JDBC_URL);
        }
    }


    /**
     * 获取 Kerberos 参数信息
     *
     * @param base64Str
     * @param check
     * @return
     */
    public static JSONObject getOriginKerberosConfig(String base64Str, boolean check) {
        JSONObject originDataJson = getDataSourceJson(base64Str);
        return getOriginKerberosConfig(originDataJson, check);
    }

    /**
     * 获取 Kerberos 参数
     *
     * @param dataJson
     * @param check
     * @return
     */
    public static JSONObject getOriginKerberosConfig(JSONObject dataJson, boolean check) {
        JSONObject kerberosConfig = dataJson.getJSONObject(KERBEROS_CONFIG);
        if (check && kerberosConfig == null) {
            throw new PubSvcDefineException("kerberos配置缺失");
        }
        return kerberosConfig;
    }

    public static void getOriginKerberosConfig(JSONObject dataJson, String sourceDataJson) {
        if (Strings.isBlank(sourceDataJson)) {
            return;
        }
        JSONObject originDataJson = getDataSourceJson(sourceDataJson);
        dataJson.put(OPEN_KERBEROS, originDataJson.get(OPEN_KERBEROS));
        dataJson.put(KERBEROS_FILE, originDataJson.getJSONObject(KERBEROS_FILE));
    }

    /**
     * 设置openKerberos开启属性
     * @param dataJson
     * @param open
     */
    public static void setOpenKerberos(JSONObject dataJson, Boolean open) {
        dataJson.put(OPEN_KERBEROS, open);
    }

    /**
     * 设置kerberos文件属性
     * @param dataJson
     * @param fileName
     */
    public static void setKerberosFile(JSONObject dataJson, String fileName) {
        Map<String,String> kerberosFile = new HashMap<>();
        kerberosFile.put("name", fileName);
        kerberosFile.put("modifyTime", Timestamp.valueOf(LocalDateTime.now()).toString());
        dataJson.put(KERBEROS_FILE, kerberosFile);
        dataJson.put(KERBEROS_FILE_TIMESTAMP, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * 判断当前传入的dataJson是否开启Kerberos认证
     * @param dataJson
     * @return
     */
    public static Boolean judgeOpenKerberos(String dataJson) {
        if (Strings.isNullOrEmpty(dataJson)) {
            return false;
        }
        JSONObject dataJsonObj = getDataSourceJson(dataJson);
        Boolean result = CommonUtils.getObjFromJson(dataJsonObj, OPEN_KERBEROS, Boolean.class);
        return Objects.nonNull(result) ? result : false;
    }


    public static void main(String[] args) {
        String json1 = "eyJvcGVuS2VyYmVyb3MiOmZhbHNlLCJqZGJjVXJsIjoiamRiYzpoaXZlMjovLzE3Mi4xNi4xMDAuMjE0OjEwMDAwIiwiZGVmYXVsdEZTIjoiaGRmczovL25zMSJ9";
        String json2 = "eyJvcGVuS2VyYmVyb3MiOmZhbHNlLCJqZGJjVXJsIjoiamRiYzpoaXZlMjovLzE3Mi4xNi4xMDAuMjE0OjEwMDAwIiwiZGVmYXVsdEZTIjoiaGRmczovL25zMSJ9";
        String json3 = "{\"openKerberos\":false,\"hadoopConfig\":\"{\n" +
                "    \"dfs.ha.namenodes.ns1\": \"nn1,nn2\",\n" +
                "    \"dfs.namenode.rpc-address.ns1.nn2\": \"172.16.101.227:9000\",\n" +
                "    \"dfs.client.failover.proxy.provider.ns1\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\n" +
                "    \"dfs.namenode.rpc-address.ns1.nn1\": \"172.16.101.196:9000\",\n" +
                "    \"dfs.nameservices\": \"ns1\"\n" +
                "}\",\"jdbcUrl\":\"jdbc:hive2://172.16.100.214:8191/default\",\"defaultFS\":\"hdfs://ns1\"}";

//        System.out.println(getDataSourceJson(json1));
//        System.out.println(getDataSourceJson(json2));
//        System.out.println(getDataSourceJson(json3));

        JSONObject dataSourceJson = getDataSourceJson(json1);
        System.out.println(dataSourceJson);
    }


}
