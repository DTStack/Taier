package com.dtstack.taier.common.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONValidator;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.exception.DtCenterDefException;
import com.dtstack.taier.common.exception.RdosDefineException;
import com.dtstack.taier.common.sftp.SFTPHandler;
import com.google.common.collect.Maps;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * 有关解析数据源工具类
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/24
 */
public class DataSourceUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(DataSourceUtils.class);

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
    public static final String ADDRESS = "address";
    public static final String SASL_KERBEROS_SERVICE_NAME = "sasl.kerberos.service.name";

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

    private static final String SEPARATE = File.separator;

    protected static String localSslDir = String.format("%s/sslConf", System.getProperty("user.dir"));

    private static final String LOCK_SUFFIX = ".lock";
    public static final String BROKER_LIST = "brokerList";
    public static final String BOOT_STRAP_SERVERS = "bootstrapServers";

    /**
     * kafka SASL/PLAIN 认证
     */
    public static final String KAFKA_SASL_PLAIN_CONTENT = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"%s\" password=\"%s\";";

    /**
     * 当 username 和 password 都不为空的时候初始化 kafka SASL_PLAINTEXT(PLAIN) 认证
     *
     * @param username 用户名
     * @param password 密码
     * @param prefix   参数前缀
     * @return kafka SASL_PLAINTEXT(PLAIN) 认证参数
     */
    public static Map<String, String> initKafkaPlainIfOpen(String username, String password, String prefix) {
        String pre = StringUtils.isBlank(prefix) ? "" : prefix;
        Map<String, String> kafkaPlainMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
            kafkaPlainMap.put(pre + "security.protocol", "SASL_PLAINTEXT");
            kafkaPlainMap.put(pre + "sasl.mechanism", "PLAIN");
            kafkaPlainMap.put(pre + "sasl.jaas.config", String.format(KAFKA_SASL_PLAIN_CONTENT, username, password));
        }
        return kafkaPlainMap;
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
        kafkaSettings.put(SASL_KERBEROS_SERVICE_NAME, serviceName);
        return kafkaSettings;
    }

    public static String getJdbcUrl(JSONObject dataJson) {
        return dataJson.getString(JDBC);
    }

    public static String getJdbcUsername(JSONObject dataJson) {
        return dataJson.getString(USERNAME);
    }

    public static String getJdbcPassword(JSONObject dataJson) {
        return dataJson.getString(PASSWORD);
    }

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
            LOGGER.error("数据源信息解码异常", e);
            throw new RdosDefineException("数据源信息解码异常", e);
        }
    }

    public static String getAddress(JSONObject dataJson) {
        return dataJson.getString(ADDRESS);
    }

    /**
     * Base64加密dataJson，可选是否加密
     *
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

    public static String getBootStrapServers(JSONObject dataJson) {
        return StringUtils.isNotBlank(dataJson.getString(BROKER_LIST)) ? dataJson.getString(BROKER_LIST) : dataJson.getString(BOOT_STRAP_SERVERS);
    }

    /**
     * Base64加密dataJson字符串, 可选是否加密
     *
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
     *
     * @param dataJson
     * @param open
     */
    public static void setOpenKerberos(JSONObject dataJson, Boolean open) {
        dataJson.put(OPEN_KERBEROS, open);
    }

    /**
     * 设置ssl文件属性
     *
     * @param dataJson
     * @param fileName
     */
    public static void setKerberosFile(JSONObject dataJson, String fileName) {
        Map<String, String> kerberosFile = new HashMap<>();
        kerberosFile.put("name", fileName);
        kerberosFile.put("modifyTime", Timestamp.valueOf(LocalDateTime.now()).toString());
        dataJson.put(KERBEROS_FILE, kerberosFile);
        dataJson.put(KERBEROS_FILE_TIMESTAMP, new Timestamp(System.currentTimeMillis()));
    }

    /**
     * 判断当前传入的dataJson是否开启Kerberos认证
     *
     * @param dataJson
     * @return
     */
    public static Boolean judgeOpenKerberos(String dataJson) {
        if (Strings.isNullOrEmpty(dataJson)) {
            return false;
        }
        JSONObject dataJsonObj = getDataSourceJson(dataJson);
        JSONObject kerberosConfig = dataJsonObj.getJSONObject(FormNames.KERBEROS_CONFIG);
        return kerberosConfig != null;
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
        } catch (URISyntaxException e) {
            throw new SQLException("Invalid JDBC URL: " + url, e);
        }

        if (isNullOrEmpty(uri.getHost())) {
            throw new SQLException("No host specified: " + url);
        }
        if ((uri.getPort() != -1) && (uri.getPort() < 1) || (uri.getPort() > 65535)) {
            throw new SQLException("Invalid port number: " + url);
        }
        int port = uri.getPort() == -1 ? 80 : uri.getPort();
        return uri.getHost() + ":" + port;
    }

    /**
     * 该数据源存放文件夹命名（sftp和本地）
     *
     * @param dtCenterSourceId 数据源中心id
     * @return sftp 数据源命名
     */
    public static String getLocalSslDir(Long dtCenterSourceId) {
        return localSslDir + SEPARATE + "_" + Optional.ofNullable(dtCenterSourceId).orElse(0L);
    }

    /**
     * 从 SFTP 上下载 kerberos 配置文件到本地。
     * 先比较传入时间戳与 本地 lock 文件的时间戳，如果传入时间戳大于本地文件时间戳则重新下载
     * 如果传入时间戳为 null，则比较本地 kerberos 文件路径下的 lock 时间戳与 sftp lock 文件时间戳判断是否需要重新下载
     *
     * @param sftpDir       SFTP 上 kerberos 配置文件相对路径
     * @param localPath     本地 kerberos 目录
     * @param sftpMap       sftp 配置getLocalTimeLock
     * @param fileTimestamp 本地时间戳
     */
    public static void downloadFileFromSftp(String sftpDir, String localPath, Map<String, String> sftpMap, Timestamp fileTimestamp) {
        //需要读取配置文件
        //本地kerberos文件
        Long localTimeLock = getLocalTimeLock(localPath);
        if (fileTimestamp != null && localTimeLock >= fileTimestamp.getTime()) {
            return;
        }
        String sftpPath = sftpMap.get("path") + SEPARATE + sftpDir;
        SFTPHandler handler = null;
        try {
            handler = SFTPHandler.getInstance(sftpMap);
            //sftp服务器文件
            Long timeLock = getSftpTimeLock(handler, sftpPath);
            // 如果 timeLock 为空，则说明不存在 .lock 文件，则
            if (timeLock == 0L || localTimeLock < timeLock) {
                // 对文件本地删除和sftp下载进行加锁
                synchronized (sftpDir.intern()) {
                    // 需要下载替换当时的配置
                    delFile(new File(localPath));
                    handler.downloadDir(sftpPath, localPath);
                    // 如果 SFTP 不存在 .lock 文件，则手动创建一个
                    createIfNotExistLockFile(localPath);
                }
            }
        } catch (Exception e) {
            throw new DtCenterDefException(String.format("从 SFTP 下载配置文件异常: %s", e.getMessage()), e);
        } finally {
            if (handler != null) {
                handler.close();
            }
        }
    }

    /**
     * 获取本地 kerberos 配置文件时间戳，不存在则返回 0
     *
     * @param localKerberosConf 本地 kerberos 配置路径
     * @return lock 文件时间戳
     */
    private static Long getLocalTimeLock(String localKerberosConf) {
        File localKerberosConfFile = new File(localKerberosConf);
        if (localKerberosConfFile.exists() && localKerberosConfFile.isDirectory()) {
            String[] list = localKerberosConfFile.list();
            if (ArrayUtils.isEmpty(list)) {
                return 0L;
            }
            // 过滤出 .lock 文件
            Optional<String> lockFileOptional = Arrays.stream(list).filter(str -> str.endsWith(LOCK_SUFFIX)).findFirst();
            // 如果存在文件则取时间戳，主要目的是历史数据处理
            if (lockFileOptional.isPresent()) {
                String filename = lockFileOptional.get();
                filename = filename.substring(0, filename.length() - LOCK_SUFFIX.length());
                return Long.valueOf(filename);
            }
        }
        return 0L;
    }

    /**
     * 获取 SFTP 服务器上的 kerberos 配置文件时间戳，不存在则返回 0
     *
     * @param handler        SFTP 客户端
     * @param sourceSftpPath SFTP kerberos 配置文件目录
     * @return SFTP 时间戳
     * @throws SftpException sftp 异常
     */
    private static Long getSftpTimeLock(SFTPHandler handler, String sourceSftpPath) throws SftpException {
        Vector vector = handler.listFile(sourceSftpPath);
        for (Object obj : vector) {
            ChannelSftp.LsEntry lsEntry = (ChannelSftp.LsEntry) obj;
            if (lsEntry.getFilename().endsWith(LOCK_SUFFIX)) {
                String filename = lsEntry.getFilename();
                filename = filename.substring(0, filename.length() - LOCK_SUFFIX.length());
                return Long.valueOf(filename);
            }
        }
        return 0L;
    }

    /**
     * 删除本地文件或文件夹
     *
     * @param file 本地文件/文件夹路径
     */
    public static void delFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            if (file.getName().endsWith("__MACOSX")) {
                return;
            }
            File[] files = file.listFiles();
            if (files == null || files.length < 1) {
                return;
            }
            for (File f : files) {
                delFile(f);
            }
        }
        LOGGER.info("正在删除本地文件，文件路径：{}", file.getAbsolutePath());
        boolean delete = file.delete();
        if (!delete) {
            LOGGER.warn("本地文件删除失败，文件路径：{}", file.getAbsolutePath());
        }
    }

    /**
     * 如果 .lock 文件不存在，则新建
     *
     * @param srcDir 远程目录
     */
    private static void createIfNotExistLockFile(String srcDir) throws IOException {
        // 如果需要检验 .lock 文件，则说明如果存在，则不创建，返回 .lock 文件，不存在则创建
        Long localTimeLock = getLocalTimeLock(srcDir);
        if (localTimeLock != 0L) {
            return;
        }
        // 文件名称
        String filename = System.currentTimeMillis() + LOCK_SUFFIX;
        boolean createResult = new File(srcDir + SEPARATE + filename).createNewFile();
        if (!createResult) {
            LOGGER.warn("kerberos lock file 文件创建失败");
        }
    }

}
