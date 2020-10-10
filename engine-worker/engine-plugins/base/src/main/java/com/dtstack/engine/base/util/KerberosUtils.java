package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SFTPHandler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class KerberosUtils {

    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String LOCAL_KEYTAB_DIR = USER_DIR + "/kerberos/keytab";
    private static final String LOCAL_KRB5_DIR = USER_DIR + "/kerberos/krb5";
    private static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KERBEROS_AUTH = "hadoop.security.authentication";
    private static final String SECURITY_TO_LOCAL = "hadoop.security.auth_to_local";
    private static final String KERBEROS_AUTH_TYPE = "kerberos";

    /**
     * @param config        任务外层配置
     * @param supplier
     * @param configuration 集群如yarn配置信息
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T login(BaseConfig config, Supplier<T> supplier, Configuration configuration) throws Exception {

        if (Objects.isNull(config) || !config.isOpenKerberos()) {
            return supplier.get();
        }

        String fileName = config.getPrincipalFile();
        String remoteDir = config.getRemoteDir();
        String localDir = LOCAL_KEYTAB_DIR + remoteDir;

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());
        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String keytabPath = handler.loadOverrideFromSftp(fileName, remoteDir, localDir, false);

        String krb5ConfName = config.getKrbName();
        String krb5ConfPath = "";
        if (StringUtils.isNotBlank(krb5ConfName)) {
            krb5ConfPath = handler.loadOverrideFromSftp(krb5ConfName, config.getRemoteDir(), localDir, true);
        }
        krb5ConfPath = mergeKrb5(krb5ConfPath);
        try {
            handler.close();
        } catch (Exception e) {
        }

        String principal = config.getPrincipal();
        if (StringUtils.isEmpty(principal)) {
            principal = KerberosUtils.getPrincipal(keytabPath);
        }

        logger.info("kerberos login, principal:{}, keytabPath:{}, krb5ConfPath:{}", principal, keytabPath, krb5ConfPath);
        return KerberosUtils.loginKerberosWithCallBack(
                configuration,
                keytabPath,
                principal,
                krb5ConfPath,
                supplier
        );
    }

    /**
     * @see HadoopKerberosName#setConfiguration(org.apache.hadoop.conf.Configuration)
     * @param allConfig
     * @param keytabPath
     * @param principal
     * @param krb5Conf
     * @param supplier
     * @param <T>
     * @return
     */
    private static <T> T loginKerberosWithCallBack(Configuration allConfig, String keytabPath, String principal, String krb5Conf, Supplier<T> supplier) {
        if (StringUtils.isNotEmpty(krb5Conf)) {
            System.setProperty(KRB5_CONF, krb5Conf);
        }
        /*如果需要走/etc/krb5.conf认证  在allConfig添加hadoop.security.authentication kerberos 即可
            case KERBEROS:
            case KERBEROS_SSL:
          如果krb5.conf 不在对应的/etc/下 需要手动指定目录的  在配置文件中hadoop.security.auth_to_local
          需要手动配置rules
          <property>
            <name>hadoop.security.auth_to_local</name>
            <value>
            RULE:[1:$1@$0](^.*@DTSTACK\.COM)s/^(.*)@DTSTACK\.COM/$1/g
            RULE:[2:$1@$0](^.*@DTSTACK\.COM)s/^(.*)@DTSTACK\.COM/$1/g
            </value>
          </property>
        */
        if (Objects.isNull(allConfig.get(SECURITY_TO_LOCAL))) {
            allConfig.set(KERBEROS_AUTH, KERBEROS_AUTH_TYPE);
        }
        UserGroupInformation.setConfiguration(allConfig);
        try {
            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
            logger.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            logger.error("{}", keytabPath, e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }

    public static String getPrincipal(String filePath) {
        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(new File(filePath));
        } catch (IOException e) {
            logger.error("Principal {} parse error e: {}!", filePath, e.getMessage());
            throw new RdosDefineException("keytab文件解析异常", e);
        }
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal = "";
        if (principals.size() != 0) {
            principal = principals.get(0).getName();
        } else {
            logger.error("Principal must not be null!");
        }
        logger.info("filePath:{} principal:{}", filePath, principal);
        return principal;
    }

    public static String mergeKrb5(String krb5ConfPath) throws Exception {
        if (StringUtils.isEmpty(krb5ConfPath)) {
            return "";
        }
        File krb5Dir = new File(LOCAL_KRB5_DIR);
        if (!krb5Dir.exists()) {
            krb5Dir.mkdirs();
        }
        String localKrb5Path = LOCAL_KRB5_DIR + File.separator + "krb5.conf";
        File localKrb5File = new File(localKrb5Path);
        if (!localKrb5File.exists()) {
            synchronized(KerberosUtils.class) {
                if (!localKrb5File.exists()) {
                    FileUtils.copyFile(new File(krb5ConfPath), localKrb5File);
                    return localKrb5Path;
                }
            }
        }

        Map<String, Map<String, String>> newContent = readKrb5(localKrb5Path);
        Map<String, Map<String, String>> remoteKrb5Content = readKrb5(krb5ConfPath);
        Map<String, Map<String, String>> localKrb5Content = readKrb5(localKrb5Path);

        Set<String> mapKeys = new HashSet<>();
        mapKeys.addAll(remoteKrb5Content.keySet());
        mapKeys.addAll(localKrb5Content.keySet());

        for (String key: mapKeys) {
            newContent.merge(key, remoteKrb5Content.get(key), new BiFunction() {
                @Override
                public Map<String, String> apply(Object oldValue, Object newValue) {
                    Map<String, String> oldMap = (Map<String, String>) oldValue;
                    Map<String, String> newMap = (Map<String, String>) newValue;
                    oldMap.putAll(newMap);
                    return oldMap;
                }
            });
        }

        if (!localKrb5Content.equals(newContent)) {
            writeKrb5(localKrb5Path, newContent);
        }
        return localKrb5Path;
    }

    public static Map<String, Map<String, String>> readKrb5(String krb5Path) throws Exception {

        Map<String, Map<String, String>> krb5Contents = new HashMap<>();

        String section = "";
        boolean flag = true;
        String currentKey = "";
        StringBuffer content = new StringBuffer();

        List<String> lines = Files.readAllLines(Paths.get(krb5Path));
        for (String line : lines) {
            line = StringUtils.trim(line);
            if (StringUtils.isNotEmpty(line) && !StringUtils.startsWith(line, "#") && !StringUtils.startsWith(line, ";")) {
                if (line.startsWith("[") && line.endsWith("]")){
                    section = line.substring(1, line.length() - 1).trim();
                } else {
                    if (line.contains("{")) {
                        flag = false;
                        content = new StringBuffer();
                        if (line.contains("=")) {
                            currentKey = line.split("=")[0].trim();
                            line = line.split("=")[1].trim();
                        }
                    }

                    if (flag) {
                        String[] cons = line.split("=");
                        String key = cons[0].trim();
                        String value = "";
                        if (cons.length > 1) {
                            value = cons[1].trim();
                        }
                        currentKey = key;
                        Map map = krb5Contents.computeIfAbsent(section, k -> new HashMap<String, String>());
                        map.put(key, value);
                    } else {
                        content.append(line).append(System.lineSeparator());
                    }

                    if (line.contains("}")) {
                        flag = true;
                        String value = content.toString();
                        Map map = krb5Contents.computeIfAbsent(section, k -> new HashMap<String, String>());
                        map.put(currentKey, value);
                    }
                }
            }
        }
        return krb5Contents;
    }

    public static void writeKrb5(String filePath, Map<String, Map<String, String>> krb5) throws Exception {
        StringBuffer content = new StringBuffer();
        for (String key : krb5.keySet()) {
            String keyStr = String.format("[%s]", key);
            content.append(keyStr).append(System.lineSeparator());
            Map<String, String> options = krb5.get(key);
            for(String option : options.keySet()) {
                String optionStr = String.format("%s = %s", option, options.get(option));
                content.append(optionStr).append(System.lineSeparator());
            }
        }
        Files.write(Paths.get(filePath), Collections.singleton(content));
    }

    public static String getKeytabPath(BaseConfig config) {
        String fileName = config.getPrincipalFile();
        String remoteDir = config.getRemoteDir();
        String localDir = LOCAL_KEYTAB_DIR + remoteDir;

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());
        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String keytabPath = handler.loadFromSftp(fileName, remoteDir, localDir);
        logger.info("keytabPath:{}", keytabPath);
        return keytabPath;
    }

    public static Configuration convertMapConfToConfiguration(Map<String,Object> allConfig) {
        if(MapUtils.isEmpty(allConfig)){
            return null;
        }
        Configuration conf = new Configuration();
        for (String key : allConfig.keySet()) {
            conf.set(key, String.valueOf(allConfig.get(key)));
        }
        return conf;
    }
}
