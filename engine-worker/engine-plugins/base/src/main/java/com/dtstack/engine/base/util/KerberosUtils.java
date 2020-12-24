package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.sftp.SftpFileManage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Time;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class KerberosUtils {

    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KERBEROS_AUTH = "hadoop.security.authentication";
    private static final String SECURITY_TO_LOCAL = "hadoop.security.auth_to_local";
    private static final String KERBEROS_AUTH_TYPE = "kerberos";
    private static final String SECURITY_TO_LOCAL_DEFAULT = "RULE:[1:$1] RULE:[2:$1]";

    private static Map<String, UserGroupInformation> ugiMap = Maps.newConcurrentMap();
    private static Map<String, String> segment = Maps.newConcurrentMap();

    private static final String TIME_FILE = ".lock";
    private static final String KEYTAB_FILE = ".keytab";

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
        String localDir = ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + remoteDir;

        File localDirPath = new File(localDir);
        if (!localDirPath.exists()) {
            localDirPath.mkdirs();
        }

        logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());

        try {
            UserGroupInformation ugi;
            String segmentName = segment.computeIfAbsent(remoteDir, key -> {return new String(remoteDir);});
            synchronized (segmentName) {
                String keytabPath = "";
                String krb5ConfPath = "";
                String krb5ConfName = config.getKrbName();
                Boolean isMergeKrb5 = config.getMergeKrbContent() != null;

                //本地文件是否和服务器时间一致 一致使用本地缓存
                boolean isOverrideDownLoad = checkLocalCache(config.getKerberosFileTimestamp(), localDirPath);
                if (isOverrideDownLoad) {
                    SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(config.getSftpConf());
                    keytabPath = sftpFileManage.cacheOverloadFile(fileName, remoteDir, localDir);
                    if (isMergeKrb5) {
                        krb5ConfPath = localDir + ConfigConstant.SP + ConfigConstant.MERGE_KRB5_NAME;
                        Files.write(Paths.get(krb5ConfPath), Collections.singleton(config.getMergeKrbContent()));
                    } else {
                        krb5ConfPath = sftpFileManage.cacheOverloadFile(krb5ConfName, config.getRemoteDir(), localDir);
                    }
                    writeTimeLockFile(config.getKerberosFileTimestamp(),localDir);
                } else {
                    keytabPath = localDir + File.separator + fileName;
                    if (isMergeKrb5) {
                        krb5ConfPath = localDir + ConfigConstant.SP + ConfigConstant.MERGE_KRB5_NAME;
                    } else {
                        krb5ConfPath = localDir + ConfigConstant.SP + krb5ConfName;
                    }
                }

                String finalKrb5ConfPath = krb5ConfPath;
                String finalKeytabPath = keytabPath;
                String threadName = Thread.currentThread().getName();
                String principal = config.getPrincipal();
                if (StringUtils.isEmpty(principal)) {
                    principal = segment.computeIfAbsent(threadName, k -> {return KerberosUtils.getPrincipal(finalKeytabPath);});
                }
                String finalPrincipal = principal;
                logger.info("kerberos login, principal:{}, keytabPath:{}, krb5ConfPath:{}", principal, keytabPath, krb5ConfPath);

                ugi = ugiMap.computeIfAbsent(threadName, k -> {
                    return createUGI(finalKrb5ConfPath, configuration, finalPrincipal, finalKeytabPath);
                });
                KerberosTicket ticket = getTGT(ugi);
                if (!checkTGT(ticket) || isOverrideDownLoad) {
                    logger.info("Relogin after the ticket expired, principal: {}, current thread: {}", principal, Thread.currentThread().getName());
                    ugi = createUGI(finalKrb5ConfPath, configuration, finalPrincipal, finalKeytabPath);
                    ugiMap.put(threadName, ugi);
                }
                logger.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            }
            Preconditions.checkNotNull(ugi, "UserGroupInformation is null");
            return KerberosUtils.loginKerberosWithCallBack(ugi, supplier);
        } catch (Exception e) {
            throw new RdosDefineException(e.getMessage());
        }
    }

    /**
     * @see HadoopKerberosName#setConfiguration(org.apache.hadoop.conf.Configuration)
     * @param ugi
     * @param supplier
     * @param <T>
     * @return
     */
    private static <T> T loginKerberosWithCallBack(UserGroupInformation ugi, Supplier<T> supplier) {
        try {
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            logger.error("{}", e.getMessage());
            throw new RdosDefineException("doAs error: " + e.getMessage());
        }
    }

    private static void writeTimeLockFile(Timestamp timestamp, String localFile) {
        if (null == timestamp) {
            return;
        }
        File file = new File(localFile);
        if (!file.exists()) {
            return;
        }
        if (null != file.listFiles()) {
            for (File listFile : file.listFiles()) {
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

    private synchronized static UserGroupInformation createUGI(String krb5ConfPath, Configuration config, String principal, String keytabPath) {
        try {
            checkParams(principal, krb5ConfPath, keytabPath);
            // krb5ConfPath = mergeKrb5(krb5ConfPath, principal);
            if (StringUtils.isNotEmpty(krb5ConfPath)) {
                System.setProperty(KRB5_CONF, krb5ConfPath);
            }
            if (StringUtils.isEmpty(config.get(SECURITY_TO_LOCAL)) || "DEFAULT".equals(config.get(SECURITY_TO_LOCAL))) {
                config.set(SECURITY_TO_LOCAL, SECURITY_TO_LOCAL_DEFAULT);
            }
            if (!StringUtils.equals(config.get(KERBEROS_AUTH), KERBEROS_AUTH_TYPE)) {
                config.set(KERBEROS_AUTH, KERBEROS_AUTH_TYPE);
            }
            sun.security.krb5.Config.refresh();
            UserGroupInformation.setConfiguration(config);
            return UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
        } catch (Exception e) {
            logger.error("Create ugi error, {}", e.getMessage());
            throw new RdosDefineException(e);
        }
    }

    private static boolean checkTGT(KerberosTicket ticket) {
        if (ticket == null) {
            return false;
        }
        long start = ticket.getStartTime().getTime();
        long end = ticket.getEndTime().getTime();
        boolean expired = Time.now() < start + (long) ((end - start) * 0.80f);
        if (expired) {
            return true;
        }
        return false;
    }

    private static KerberosTicket getTGT(UserGroupInformation ugi) throws Exception {
        Class<? extends UserGroupInformation> ugiClass = ugi.getClass();
        Field subjectField = ugiClass.getDeclaredField("subject");
        subjectField.setAccessible(true);
        Subject subject = (Subject)subjectField.get(ugi);

        Set<KerberosTicket> tickets = subject
                .getPrivateCredentials(KerberosTicket.class);
        for (KerberosTicket ticket : tickets) {
            KerberosPrincipal principal = ticket.getServer();
            if (principal == null) {
                continue;
            }
            String krbtgt = "krbtgt/" + principal.getRealm() + "@" + principal.getRealm();
            if (StringUtils.equals(principal.getName(), krbtgt)) {
                return ticket;
            }
        }
        logger.warn("Not found Ticket, userName: {}", ugi.getUserName());
        return null;
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

    private static void checkParams(String principal, String krb5ConfPath, String keytabPath) {
        if (StringUtils.isEmpty(principal)) {
            throw new RdosDefineException("principal is null！");
        }
        if (StringUtils.isEmpty(krb5ConfPath)) {
            throw new RdosDefineException("krb5.conf not exists！");
        }
        if (StringUtils.isEmpty(keytabPath)) {
            throw new RdosDefineException("keytab not exists！");
        }
    }

    public static synchronized String[] getKerberosFile(BaseConfig config, String localDir) {
        String keytabFileName = config.getPrincipalFile();
        String krb5FileName = config.getKrbName();
        String remoteDir = config.getRemoteDir();
        Boolean isMergeKrb5 = config.getMergeKrbContent() != null;
        if (StringUtils.isEmpty(localDir)) {
            localDir = ConfigConstant.LOCAL_KEYTAB_DIR_PARENT + remoteDir;
        }

        File localDirPath = new File(localDir);
        if (!localDirPath.exists()) {
            localDirPath.mkdirs();
        }

        String keytabPath = "";
        String krb5ConfPath = "";
        boolean isOverrideDownLoad = checkLocalCache(config.getKerberosFileTimestamp(), localDirPath);
        if (isOverrideDownLoad) {
            SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(config.getSftpConf());
            keytabPath = sftpFileManage.cacheOverloadFile(keytabFileName, remoteDir, localDir);
            if (isMergeKrb5) {
                krb5ConfPath = localDir + ConfigConstant.SP + ConfigConstant.MERGE_KRB5_NAME;
                try {
                    Files.write(Paths.get(krb5ConfPath), Collections.singleton(config.getMergeKrbContent()));
                } catch (IOException e) {
                    throw new RdosDefineException(e);
                }
            } else {
                krb5ConfPath = sftpFileManage.cacheOverloadFile(krb5FileName, remoteDir, localDir);
            }
            writeTimeLockFile(config.getKerberosFileTimestamp(), localDir);
        } else {
            keytabPath = localDir + File.separator + keytabFileName;
            krb5ConfPath = localDir + File.separator + krb5FileName;
        }
        logger.info("Get keytabPath: {}, krb5ConfPath: {}", keytabPath, krb5ConfPath);
        return new String[]{keytabPath, krb5ConfPath};
    }

    public static String getKeytabPath(BaseConfig config) {
        String fileName = config.getPrincipalFile();
        String remoteDir = config.getRemoteDir();
        String localDir = USER_DIR + remoteDir;

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        SftpFileManage sftpFileManage = SftpFileManage.getSftpManager(config.getSftpConf());
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
