package com.dtstack.taier.datasource.plugin.common.utils;

import com.dtstack.taier.datasource.api.dto.SSLConfig;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.dto.source.KafkaSourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;

/**
 * <p>ssl 工具类, 统一 ssl 处理, 后续作为所有 ssl 解析的统一入口
 *
 * @author ：wangchuan
 * date：Created in 上午10:23 2022/3/4
 * company: www.dtstack.com
 */
@Slf4j
public class SSLUtil {

    /**
     * ssl 配置文件默认名称
     */
    private static final String DEFAULT_SSL_CLIENT_CONF_NAME = "ssl-client.xml";

    /**
     * ssl-client.xml 中 keystore 的配置项 key
     */
    private static final String KEY_TYPE = "ssl.client.keystore.type";
    private static final String KEY_LOCATION = "ssl.client.keystore.location";
    private static final String KEY_PASSWORD = "ssl.client.keystore.password";

    /**
     * ssl-client.xml 中 truststore 的配置项 key
     */
    private static final String TRUST_TYPE = "ssl.client.truststore.type";
    private static final String TRUST_LOCATION = "ssl.client.truststore.location";
    private static final String TRUST_PASSWORD = "ssl.client.truststore.password";

    /**
     * kafka ssl
     */
    private static final String KAFKA_KEYSTORE_PATH_KEY = "SSLKeyStorePath";
    private static final String KAFKA_KEYSTORE_PASSWORD_KEY = "SSLKeyStorePassword";
    private static final String KAFKA_KEYSTORE_TIMESTAMP_KEY = "sslKeyStoreFileTimestamp";
    private static final String KAFKA_TRUSTSTORE_PATH_KEY = "SSLTrustStorePath";
    private static final String KAFKA_TRUSTSTORE_PASSWORD_KEY = "SSLTrustStorePassword";
    private static final String KAFKA_TRUSTSTORE_TIMESTAMP_KEY = "sslTrustStoreFileTimestamp";

    /**
     * KeyStoreType
     */
    private static final String KEY_PKCS12 = "pkcs12";
    private static final String KEY_CA = "ca";

    /**
     * 获取 kafka ssl 配置
     *
     * @param sourceDTO kafka sourceDTO
     * @return kafka ssl 配置
     */
    public static SSLConfiguration getKafkaSSLConfiguration(ISourceDTO sourceDTO) {
        KafkaSourceDTO kafkaSourceDTO = (KafkaSourceDTO) sourceDTO;
        SSLConfig sslConfig = kafkaSourceDTO.getSslConfig();
        if (Objects.isNull(sslConfig)) {
            return null;
        }

        Map<String, Object> otherConfig = sslConfig.getOtherConfig();
        if (MapUtils.isEmpty(otherConfig)) {
            return null;
        }

        String keystorePath = SftpUtil.downloadSftpDirFromSftp(sourceDTO, MapUtils.getString(otherConfig, KAFKA_KEYSTORE_PATH_KEY),
                PathUtils.getSSLConfDir(), getFileTimestamp(otherConfig, KAFKA_KEYSTORE_TIMESTAMP_KEY));
        String keystorePassword = MapUtils.getString(otherConfig, KAFKA_KEYSTORE_PASSWORD_KEY);

        String truststorePath = SftpUtil.downloadSftpDirFromSftp(sourceDTO, MapUtils.getString(otherConfig, KAFKA_TRUSTSTORE_PATH_KEY),
                PathUtils.getSSLConfDir(), getFileTimestamp(otherConfig, KAFKA_TRUSTSTORE_TIMESTAMP_KEY));
        String truststorePassword = MapUtils.getString(otherConfig, KAFKA_TRUSTSTORE_PASSWORD_KEY);

        return SSLConfiguration.builder()
                .keyStorePath(getStorePath(keystorePath))
                .keyStorePassword(keystorePassword)
                .trustStorePath(getStorePath(truststorePath))
                .trustStorePassword(truststorePassword).build();
    }

    /**
     * 解析出 ssl configuration
     *
     * @param sourceDTO 数据源信息
     * @return ssl configuration
     */
    public static SSLConfiguration getSSLConfiguration(ISourceDTO sourceDTO) {
        AbstractSourceDTO abstractSourceDTO = (AbstractSourceDTO) sourceDTO;
        SSLConfig sslConfig = abstractSourceDTO.getSslConfig();
        if (Objects.isNull(sslConfig)) {
            return null;
        }

        // 先将 sftp 上 ssl 配置文件夹下载到本地
        String localPath = SftpUtil.downloadSftpDirFromSftp(sourceDTO, sslConfig.getRemoteSSLDir(),
                PathUtils.getSSLConfDir(), sslConfig.getSslFileTimestamp());

        // ssl 配置文件相对路径, 默认为 ssl-client.xml
        String sslClientConf = StringUtils.isEmpty(sslConfig.getSslClientConf()) ?
                DEFAULT_SSL_CLIENT_CONF_NAME : sslConfig.getSslClientConf();

        // 拼接绝对路径
        String sslClientConfPath = localPath + File.separator + sslClientConf;
        File sslClientConfFile = new File(sslClientConfPath);

        SSLConfiguration sslConfiguration = new SSLConfiguration();
        // 路径不存在的话从 otherConfig 中取, key 和 xml 文件中的 key 保持一直
        if (sslClientConfFile.exists()) {
            // engine 上传的 ssl 证书一定会有 ssl-client.xml 文件
            Map<String, String> sslConfigMap = Xml2JsonUtil.xml2map(sslClientConfFile);
            setSSLConfig(sslConfiguration, sslConfigMap, localPath + File.separator);
        } else if (MapUtils.isNotEmpty(sslConfig.getOtherConfig())) {
            setSSLConfig(sslConfiguration, sslConfig.getOtherConfig(), localPath + File.separator);
        }
        log.debug("SSLConfiguration: {}", sslConfiguration.toString());
        return sslConfiguration;
    }

    /**
     * 设置 ssl 配置
     *
     * @param sslConfiguration ssl 配置对象
     * @param sslConfigMap     ssl 配置 map
     * @param storePathPrefix  store 地址前缀
     */
    private static void setSSLConfig(SSLConfiguration sslConfiguration, Map<String, ?> sslConfigMap,
                                     String storePathPrefix) {
        sslConfiguration.setKeyStoreType(MapUtils.getString(sslConfigMap, KEY_TYPE));
        String keyLocation = MapUtils.getString(sslConfigMap, KEY_LOCATION);
        sslConfiguration.setKeyStorePath(StringUtils.isBlank(keyLocation) ? null : storePathPrefix + keyLocation);
        sslConfiguration.setKeyStorePassword(MapUtils.getString(sslConfigMap, KEY_PASSWORD));
        sslConfiguration.setTrustStoreType(MapUtils.getString(sslConfigMap, TRUST_TYPE));
        String trustLocation = MapUtils.getString(sslConfigMap, TRUST_LOCATION);
        sslConfiguration.setTrustStorePath(StringUtils.isBlank(storePathPrefix) ? null : storePathPrefix + trustLocation);
        sslConfiguration.setTrustStorePassword(MapUtils.getString(sslConfigMap, TRUST_PASSWORD));
    }

    /**
     * 获取 keystore、truststore 文件绝对路径
     *
     * @param sslDir ssl本地目录
     * @return keystore、truststore 文件绝对路径
     */
    private static String getStorePath(String sslDir) {
        if (StringUtils.isEmpty(sslDir)) {
            return null;
        }
        File file = new File(sslDir);
        AssertUtils.isTrue(file.exists(), String.format("path %s does not exist", sslDir));
        AssertUtils.isTrue(file.isDirectory(), String.format("path %s is not directory", sslDir));
        File[] files = file.listFiles();
        AssertUtils.isTrue(files != null, String.format("No files under path %s", sslDir));
        for (File f : files) {
            if (f.isFile() && (f.getName().endsWith(".jks") || f.getName().endsWith(".keystore") || f.getName().endsWith(".truststore"))) {
                return f.getAbsolutePath();
            }
        }
        throw new SourceException("SSL authentication file not found.");
    }

    /**
     * 根据认证方式获取KeyStore
     *
     * @param type         ssl证书格式
     * @param path         证书文件路径
     * @param keyStorePass 使用证书文件的密码
     * @return KeyStore
     */
    public static KeyStore getKeyStoreByType(String type, Path path, String keyStorePass) {
        KeyStore keyStore;
        InputStream is = null;
        try {
            if (KEY_PKCS12.equalsIgnoreCase(type)) {
                log.info("init RestClient, type: pkcs#12.");
                keyStore = KeyStore.getInstance("pkcs12");
                is = Files.newInputStream(path);
                keyStore.load(is, keyStorePass.toCharArray());
            } else if (KEY_CA.equalsIgnoreCase(type)) {
                log.info("init RestClient, type: use CA certificate that is available as a PEM encoded file.");
                CertificateFactory factory = CertificateFactory.getInstance("X.509");
                Certificate trustedCa;
                is = Files.newInputStream(path);
                trustedCa = factory.generateCertificate(is);
                keyStore = KeyStore.getInstance("pkcs12");
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", trustedCa);
            } else {
                throw new UnsupportedOperationException("can not support this type : " + type);
            }
            return keyStore;
        } catch (IOException | KeyStoreException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    /**
     * 获取 otherConfig 中的文件上传时间戳
     *
     * @param otherConfig  ssl 其他配置
     * @param timestampKey 上传文件的时间戳 key
     * @return 时间戳
     */
    private static Timestamp getFileTimestamp(Map<String, Object> otherConfig, String timestampKey) {
        Object timestampObj = MapUtils.getObject(otherConfig, KAFKA_KEYSTORE_TIMESTAMP_KEY);
        if (Objects.isNull(timestampObj)) {
            return null;
        } else if (timestampObj instanceof Long) {
            return new Timestamp((Long) timestampObj);
        } else if (timestampObj instanceof Timestamp) {
            return (Timestamp) timestampObj;
        }
        return null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString
    public static class SSLConfiguration {

        /**
         * 密钥库的类型。默认类型由 Java keystore.type安全属性提供
         */
        private String keyStoreType;

        /**
         * 连接到启用了证书身份验证的集群时使用。指定PEM或JKS文件的路径
         */
        private String keyStorePath;

        /**
         * KeyStore 的密码(如果有)
         */
        private String keyStorePassword;

        /**
         * 要使用的 Java TrustStore 文件的位置。验证 HTTPS 服务器证书
         */
        private String trustStorePath;

        /**
         * TrustStore 的密码
         */
        private String trustStorePassword;

        /**
         * TrustStore 的类型。默认类型由 Java keystore.type安全属性提供
         */
        private String trustStoreType;
    }
}
