package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.base.filesystem.manager.SftpFileManage;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpConfig;
import org.apache.commons.collections.MapUtils;
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
import java.security.PrivilegedExceptionAction;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class KerberosUtils {

    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String LOCAL_KEYTAB_DIR = USER_DIR + "/keytab";
    private static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KERBEROS_AUTH = "hadoop.security.authentication";
    private static final String SECURITY_TO_LOCAL = "hadoop.security.auth_to_local";
    private static final String KERBEROS_AUTH_TYPE = "kerberos";
    private static final String TIME_FILE = ".lock";
    private static final String KEYTAB_FILE = ".keytab";

    private static Map<String, String> principalMap = new ConcurrentHashMap<>();
    private static Map<SftpConfig, SftpFileManage> sftpMap = new ConcurrentHashMap<>();

    /**
     * @param config        任务外层配置
     * @param supplier
     * @param configuration 集群如yarn配置信息
     * @param <T>
     * @return
     * @throws Exception
     */
    public static synchronized <T> T login(BaseConfig config, Supplier<T> supplier, Configuration configuration) throws Exception {

        if (Objects.isNull(config) || !config.isOpenKerberos()) {
            return supplier.get();
        }

        String fileName = config.getPrincipalFile();
        String remoteDir = config.getRemoteDir();
        String localDir = LOCAL_KEYTAB_DIR + remoteDir;

        File path = new File(localDir);

        //本地文件是否和服务器时间一致 一致使用本地缓存
        boolean isOverrideDownLoad = checkLocalCache(config.getKerberosFileTimestamp(), path);

        String keytabPath;
        String krb5ConfPath = "";
        String krb5ConfName = config.getKrbName();
        if (isOverrideDownLoad) {
            logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());

            SftpFileManage sftpFileManage = sftpMap.computeIfAbsent(config.getSftpConf() , k -> new SftpFileManage(config.getSftpConf()));
            keytabPath = sftpFileManage.cacheOverloadFile(fileName, remoteDir, localDir);
            if (StringUtils.isNotBlank(krb5ConfName)) {
                krb5ConfPath = sftpFileManage.cacheOverloadFile(krb5ConfName, config.getRemoteDir(), localDir);
            }

            //删除本地时间戳标示文件 更新最新时间
            writeTimeLockFile(config.getKerberosFileTimestamp(),localDir);
        } else {
            //走本地缓存
            keytabPath = localDir + File.separator + fileName;
            if (StringUtils.isNotBlank(krb5ConfName)) {
                krb5ConfPath = localDir + File.separator + config.getKrbName();
            }
        }

        if (!new File(keytabPath).exists()) {
            throw new RdosDefineException(keytabPath + "keytab 文件不存在");
        }

        String principal = KerberosUtils.getPrincipal(keytabPath);
        config.setPrincipalName(principal);
        config.setPrincipalPath(keytabPath);

        logger.info("kerberos login, principal:{}, keytabPath:{}, krb5ConfPath:{}", principal, keytabPath, krb5ConfPath);
        return KerberosUtils.loginKerberosWithCallBack(
                configuration,
                keytabPath,
                principal,
                krb5ConfPath,
                supplier
        );
    }

    private static void writeTimeLockFile(Timestamp timestamp, String localFile) {
        if (null == timestamp) {
            return;
        }
        File file = new File(localFile);
        if (!file.exists()) {
            return;
        }
        File[] files = file.listFiles();
        if (null != files) {
            for (File listFile : files) {
                if (listFile.getName().endsWith(TIME_FILE)) {
                    logger.info("fileName:{},timestamp {}  localDir:{},delete {}", listFile.getName(), timestamp, listFile, listFile.delete());
                }
            }
        }
        File timeFile = new File(localFile + File.separator + timestamp.getTime() + TIME_FILE);
        try {
            logger.info("fileName:{},timestamp {}  localDir:{},delete {}", timeFile.getName(), timestamp.getTime(), localFile, timeFile.createNewFile());
        } catch (IOException e) {
            logger.error("create time lock file  {} error ", timeFile.getName(), e);
        }
    }

    private static boolean checkLocalCache(Timestamp dbUploadTime, File path) {
        boolean isOverrideDownLoad = true;
        if (!path.exists()) {
            path.mkdirs();
        } else if (null != dbUploadTime) {
            File[] files = path.listFiles();
            boolean isContainKeytabFile = false;
            if (null != files && files.length > 0) {
                for (File file : files) {
                    if (file.getName().endsWith(TIME_FILE) && file.getName().contains(dbUploadTime.getTime() + "")) {
                        isOverrideDownLoad = false;
                    }
                    if (file.getName().contains(KEYTAB_FILE)) {
                        isContainKeytabFile = true;
                    }
                }
                if (!isContainKeytabFile && !isOverrideDownLoad) {
                    //只有lock文件 没有keytab文件
                    isOverrideDownLoad = true;
                }
            }
        }
        return isOverrideDownLoad;
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
    private static synchronized <T> T loginKerberosWithCallBack(Configuration allConfig, String keytabPath, String principal, String krb5Conf, Supplier<T> supplier) {
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

        try {
            sun.security.krb5.Config.refresh();
            UserGroupInformation.setConfiguration(allConfig);

            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
            logger.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            logger.error("{}", keytabPath, e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }

    public static synchronized String getPrincipal(String filePath) {
        String nowPrincipal = principalMap.computeIfAbsent(filePath, k -> {
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
            logger.info("getPrincipal filePath:{} principal:{}", filePath, principal);
            return principal;
        });
        return nowPrincipal;
    }

    public static String getKeytabPath(BaseConfig config) {
        String fileName = config.getPrincipalFile();
        String remoteDir = config.getRemoteDir();
        String localDir = LOCAL_KEYTAB_DIR + remoteDir;

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        SftpFileManage sftpFileManage = sftpMap.computeIfAbsent(config.getSftpConf() , k -> new SftpFileManage(config.getSftpConf()));
        logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());

        String keytabPath = sftpFileManage.cacheOverloadFile(fileName, remoteDir, localDir);
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
