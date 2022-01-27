package com.dtstack.taiga.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.dtstack.taiga.common.constant.FormNames;
import com.dtstack.taiga.common.exception.RdosDefineException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;

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
    public static final String SSL_FILE = "sslFile";
    public static final String OPEN_KERBEROS = "openKerberos";
    public static final String JDBC_URL = "jdbc.url";
    public static final String JDBC_USERNAME = "jdbc.username";
    public static final String JDBC_PASSWORD = "jdbc.password";
    public static final String HADOOP_CONFIG = "hadoopConfig";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String JDBC = "jdbcUrl";
    public static final String DEFAULT_FS = "defaultFS";
    /**
     * Kerberos 文件上传的时间戳
     */
    public static final String KERBEROS_FILE_TIMESTAMP = "kerberosFileTimestamp";

    /**
     * ssl文件上传的时间戳
     */
    public static final String SSL_FILE_TIMESTAMP = "sslFileTimestamp";

    public static final String JDBC_URL_PREFIX = "jdbc:";

    public static final String PRESTO_URL_START = JDBC_URL_PREFIX + "trino:";


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
            throw new RdosDefineException("数据源信息解码异常", e);
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
            throw new RdosDefineException("kerberos配置缺失");
        }
        return kerberosConfig;
    }

    public static void getOriginSSLConfig(JSONObject dataJson, String sourceDataJson) {
        if (Strings.isBlank(sourceDataJson)) {
            return;
        }
        JSONObject originDataJson = getDataSourceJson(sourceDataJson);
        dataJson.put(SSL_FILE, originDataJson.get(SSL_FILE));
        dataJson.put(SSL_FILE_TIMESTAMP,originDataJson.get(SSL_FILE_TIMESTAMP));
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
    public static void setSslFile(JSONObject dataJson, String fileName) {
        Map<String,String> kerberosFile = new HashMap<>();
        kerberosFile.put("name", fileName);
        kerberosFile.put("modifyTime", Timestamp.valueOf(LocalDateTime.now()).toString());
        dataJson.put(SSL_FILE, kerberosFile);
        dataJson.put(SSL_FILE_TIMESTAMP, new Timestamp(System.currentTimeMillis()));
    }


    /**
     * 设置ssl文件属性
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
        JSONObject kerberosConfig = dataJsonObj.getJSONObject(FormNames.KERBEROS_CONFIG);
        return kerberosConfig !=null;
    }


    public static String parsePrestoUrl(String url) throws SQLException {
        if (!url.startsWith(PRESTO_URL_START)) {
            throw new SQLException("Invalid JDBC URL: " + url);
        }
        if (url.equals(PRESTO_URL_START)) {
            throw new SQLException("Empty JDBC URL: " + url);
        }
        URI uri;
        try {
            uri = new URI(url.substring(JDBC_URL_PREFIX.length()));
        }
        catch (URISyntaxException e) {
            throw new SQLException("Invalid JDBC URL: " + url, e);
        }

        if (isNullOrEmpty(uri.getHost())) {
            throw new SQLException("No host specified: " + url);
        }
        if ((uri.getPort() != -1) && (uri.getPort() < 1) || (uri.getPort() > 65535)) {
            throw new SQLException("Invalid port number: " + url);
        }
        int port = uri.getPort() == -1 ? 80:uri.getPort();
        return uri.getHost()+":"+port;
    }



    public static void main(String[] args) throws SQLException {
//        String json1 = "eyJvcGVuS2VyYmVyb3MiOmZhbHNlLCJqZGJjVXJsIjoiamRiYzpoaXZlMjovLzE3Mi4xNi4xMDAuMjE0OjEwMDAwIiwiZGVmYXVsdEZTIjoiaGRmczovL25zMSJ9";
//        String json2 = "eyJvcGVuS2VyYmVyb3MiOmZhbHNlLCJqZGJjVXJsIjoiamRiYzpoaXZlMjovLzE3Mi4xNi4xMDAuMjE0OjEwMDAwIiwiZGVmYXVsdEZTIjoiaGRmczovL25zMSJ9";
//        String json3 = "{\"openKerberos\":false,\"hadoopConfig\":\"{\n" +
//                "    \"dfs.ha.namenodes.ns1\": \"nn1,nn2\",\n" +
//                "    \"dfs.namenode.rpc-address.ns1.nn2\": \"172.16.101.227:9000\",\n" +
//                "    \"dfs.client.failover.proxy.provider.ns1\": \"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\",\n" +
//                "    \"dfs.namenode.rpc-address.ns1.nn1\": \"172.16.101.196:9000\",\n" +
//                "    \"dfs.nameservices\": \"ns1\"\n" +
//                "}\",\"jdbcUrl\":\"jdbc:hive2://172.16.100.214:8191/default\",\"defaultFS\":\"hdfs://ns1\"}";

//        System.out.println(getDataSourceJson(json1));
//        System.out.println(getDataSourceJson(json2));
//        System.out.println(getDataSourceJson(json3));

//        JSONObject dataSourceJson = getDataSourceJson(json1);
//        System.out.println(dataSourceJson);

//        String encodeDataSource = getEncodeDataSource("{\"jdbcUrl\": \"jdbc:hive2://172.16.100.83:10004/dev\", \"password\": \"\", \"username\": \"admin\", \"defaultFS\": \"hdfs://ns1\", \"hadoopConfig\": \"{\\\"dfs.ha.namenodes.ns1\\\":\\\"nn1,nn2\\\",\\\"dfs.namenode.rpc-address.ns1.nn2\\\":\\\"kudu2:9000\\\",\\\"dfs.client.failover.proxy.provider.ns1\\\":\\\"org.apache.hadoop.hdfs.server.namenode.ha.ConfiguredFailoverProxyProvider\\\",\\\"dfs.namenode.rpc-address.ns1.nn1\\\":\\\"kudu1:9000\\\",\\\"dfs.nameservices\\\":\\\"ns1\\\"}\", \"hasHdfsConfig\": true}", true);
//        System.out.println(encodeDataSource);


//        String str = getEncodeDataSource("{\n" +
//                " \n" +
//                "  \"connection-user\":\"drpeco\",\n" +
//                "  \"connection-url\":\"jdbc:mysql://172.16.23.234:3306?serverTimezone=UTC\",\n" +
//                "  \"connection-password\":\"DT@Stack#123\"\n" +
//                "}", true);
//        System.out.println(str);

//        String str = getEncodeDataSource("{\n" +
//                " \"hive.metastore.uri\":\"thrift://172.16.100.83:9083\",\n" +
//                " \"hive-other\":{\n" +
//                "        \"hive.allow-drop-table\":true,\n" +
//                "        \"hive.allow-rename-table\":true\n" +
//                "    }\n" +
//                "}", true);
//        System.out.println(str);

    }


}
