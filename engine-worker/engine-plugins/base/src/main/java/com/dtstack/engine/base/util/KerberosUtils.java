package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SFTPHandler;
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
    private static final String LOCAL_KEYTAB_DIR = USER_DIR + "/kerberos/keytab";
    private static final String LOCAL_KRB5_DIR = USER_DIR + "/kerberos/krb5";
    private static final String KRB5_FILE_NAME = "krb5.conf";
    private static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KERBEROS_AUTH = "hadoop.security.authentication";
    private static final String SECURITY_TO_LOCAL = "hadoop.security.auth_to_local";
    private static final String KERBEROS_AUTH_TYPE = "kerberos";
    private static final String SECURITY_TO_LOCAL_DEFAULT = "RULE:[1:$1] RULE:[2:$1]";
    private static final String MODIFIED_TIME_KEY = "modifiedTime";

    private static ObjectMapper objectMapper = new ObjectMapper();
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
        String localDir = LOCAL_KEYTAB_DIR + remoteDir;

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

                //本地文件是否和服务器时间一致 一致使用本地缓存
                boolean isOverrideDownLoad = checkLocalCache(config.getKerberosFileTimestamp(), localDirPath);
                if (isOverrideDownLoad) {
                    SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
                    keytabPath = handler.loadOverrideFromSftp(fileName, remoteDir, localDir, false);
                    if (StringUtils.isNotBlank(krb5ConfName)) {
                        krb5ConfPath = handler.loadOverrideFromSftp(krb5ConfName, config.getRemoteDir(), localDir, true);
                    }
                    try {
                        handler.close();
                    } catch (Exception e) {
                    }
                    writeTimeLockFile(config.getKerberosFileTimestamp(),localDir);
                } else {
                    keytabPath = localDir + File.separator + fileName;
                    if (StringUtils.isNotBlank(krb5ConfName)) {
                        krb5ConfPath = localDir + File.separator + config.getKrbName();
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
                    logger.info("Relogin after the ticket expired, principal {}", principal);
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
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
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
            krb5ConfPath = mergeKrb5(krb5ConfPath, principal);
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
        long start = ticket.getStartTime().getTime();
        long end = ticket.getEndTime().getTime();
        boolean expired = Time.now() < start + (long) ((end - start) * 0.80f);
        if (ticket != null && expired) {
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

    public static String mergeKrb5(String krb5ConfPath, String principal) throws Exception {
        if (StringUtils.isEmpty(krb5ConfPath)) {
            return "";
        }
        File krb5Dir = new File(LOCAL_KRB5_DIR);
        if (!krb5Dir.exists()) {
            krb5Dir.mkdirs();
        }
        String localKrb5Path = LOCAL_KRB5_DIR + File.separator + KRB5_FILE_NAME;
        File localKrb5File = new File(localKrb5Path);
        if (!localKrb5File.exists()) {
            synchronized(KerberosUtils.class) {
                if (!localKrb5File.exists()) {
                    FileUtils.copyFile(new File(krb5ConfPath), localKrb5File);
                    return localKrb5Path;
                }
            }
        }

        boolean isMerge = false;
        for (int i=0; i < 3; i++) {
            try{
                Map<String, HashMap<String, String>> remoteKrb5Content = readKrb5(krb5ConfPath);
                Map<String, HashMap<String, String>> localKrb5Content = readKrb5(localKrb5Path);

                String modifiedTime = localKrb5Content.get(MODIFIED_TIME_KEY).get(MODIFIED_TIME_KEY);
                Set<String> mapKeys = mergeKrb5ContentKey(remoteKrb5Content, localKrb5Content);
                String localKrb5ContentStr = objectMapper.writeValueAsString(localKrb5Content);
                if (StringUtils.isEmpty(localKrb5ContentStr)) {
                    throw new RdosDefineException("krb5.conf is null!");
                }
                Map<String, HashMap<String, String>> newContent = (HashMap<String, HashMap<String, String>>)objectMapper.readValue(localKrb5ContentStr, HashMap.class);

                for (String key: mapKeys) {
                    HashMap<String, String> remoteKrb5Section = remoteKrb5Content.get(key);
                    if (remoteKrb5Section == null) {
                        continue;
                    }
                    newContent.merge(key, remoteKrb5Content.get(key), new BiFunction() {
                        @Override
                        public Map<String, String> apply(Object oldValue, Object newValue) {
                            Map<String, String> oldMap = (Map<String, String>) oldValue;
                            Map<String, String> newMap = (Map<String, String>) newValue;
                            if (oldMap == null) {
                                return newMap;
                            } else if (newMap == null) {
                                return oldMap;
                            } else {
                                oldMap.putAll(newMap);
                                return oldMap;
                            }
                        }
                    });
                }

                checkRealm(newContent.get("domain_realm"), principal);
                if (!localKrb5Content.equals(newContent)) {
                    writeKrb5(localKrb5Path, newContent, modifiedTime);
                }
                isMerge = true;
                break;
            } catch (Exception e) {
                logger.error("merge krb5.conf fail!, {}", e.getMessage());
            }
        }

        if (!isMerge) {
            throw new RdosDefineException("Merge krb5.conf fail!");
        }
        return localKrb5Path;
    }

    private static void checkParams(String principal, String krb5ConfPath, String keytabPath) {
        if (StringUtils.isEmpty(principal)) {
            throw new RdosDefineException("principal is null！");
        }
        if (StringUtils.isEmpty(krb5ConfPath)) {
            throw new RdosDefineException("krb5ConfPath is null！");
        }
        if (StringUtils.isEmpty(keytabPath)) {
            throw new RdosDefineException("keytabPath is null！");
        }
    }

    private static void checkRealm(HashMap<String, String> domainRealm, String principal) {
        if (domainRealm == null || domainRealm.size() == 0) {
            throw new RdosDefineException("domain_realm is not set！");
        }
        String user = "";
        String hostname = "";
        if (StringUtils.contains(principal, "/")) {
            String[] conts = StringUtils.split(principal, "/");
            user = conts[0];
            if (conts.length >= 2) {
                if (StringUtils.contains(conts[1], "@")) {
                    String[] split = StringUtils.split(conts[1], "@");
                    hostname = split[0];
                }
            }
        }
        if (StringUtils.isNotEmpty(hostname) && !domainRealm.containsKey(hostname)) {
            logger.error("{} not in domain_realm, current principal is {}", hostname, principal);
            throw new RdosDefineException(hostname + "not in domain_realm!");
        }
    }

    private static Set<String> mergeKrb5ContentKey(Map<String, HashMap<String, String>> remoteKrb5Content, Map<String, HashMap<String, String>> localKrb5Content) {
        remoteKrb5Content.remove(MODIFIED_TIME_KEY);
        localKrb5Content.remove(MODIFIED_TIME_KEY);

        Set<String> mapKeys = new HashSet<>();
        mapKeys.addAll(remoteKrb5Content.keySet());
        mapKeys.addAll(localKrb5Content.keySet());
        return mapKeys;
    }

    public static Map<String, HashMap<String, String>> readKrb5(String krb5Path) {
        Map<String, HashMap<String, String>> krb5Contents = new HashMap<>();

        String section = "";
        boolean flag = true;
        String currentKey = "";
        StringBuffer content = new StringBuffer();

        List<String> lines = new ArrayList<>();
        File krb5File = new File(krb5Path);

        try(
                InputStreamReader inputReader = new InputStreamReader(new FileInputStream(krb5File));
                BufferedReader br = new BufferedReader(inputReader);
        ){
            long modifiedTime = krb5File.lastModified();
            krb5Contents.put(MODIFIED_TIME_KEY, new HashMap<String, String>(){{
                put(MODIFIED_TIME_KEY, String.valueOf(modifiedTime));
            }});
            for (;;) {
                String line = br.readLine();
                if (line == null) {
                    break;
                }
                lines.add(line);
            }
        } catch (Exception e){
            logger.error("krb5.conf read error: {}", e.getMessage());
            throw new RdosDefineException("krb5.conf read error");
        }

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

    public static void writeKrb5(String filePath, Map<String, HashMap<String, String>> krb5, String modifiedTime) throws Exception {
        File file = new File(filePath);
        String newModifiedTime = String.valueOf(file.lastModified());
        if (!StringUtils.equals(newModifiedTime, modifiedTime)) {
            throw new RdosDefineException("krb5.conf modified time changed!");
        }

        StringBuffer content = new StringBuffer();
        for (String key : krb5.keySet()) {
            if (StringUtils.isNotEmpty(key)) {
                String keyStr = String.format("[%s]", key);
                content.append(keyStr).append(System.lineSeparator());
            }
            Map<String, String> options = krb5.get(key);
            for(String option : options.keySet()) {
                String optionStr = String.format("%s = %s", option, options.get(option));
                content.append(optionStr).append(System.lineSeparator());
            }
        }
        Files.write(Paths.get(filePath), Collections.singleton(content));
    }

    public static synchronized String[] getKerberosFile(BaseConfig config, String localDir) {
        String keytabFileName = config.getPrincipalFile();
        String krb5FileName = config.getKrbName();
        String remoteDir = config.getRemoteDir();
        if (StringUtils.isEmpty(localDir)) {
            localDir = LOCAL_KEYTAB_DIR + remoteDir;
        }

        File localDirPath = new File(localDir);
        if (!localDirPath.exists()) {
            localDirPath.mkdirs();
        }

        String keytabPath = "";
        String krb5ConfPath = "";
        boolean isOverrideDownLoad = checkLocalCache(config.getKerberosFileTimestamp(), localDirPath);
        if (isOverrideDownLoad) {
            SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
            keytabPath = handler.loadOverrideFromSftp(keytabFileName, remoteDir, localDir, false);
            krb5ConfPath = handler.loadOverrideFromSftp(krb5FileName, remoteDir, localDir, true);
            writeTimeLockFile(config.getKerberosFileTimestamp(), localDir);
        } else {
            keytabPath = localDir + File.separator + keytabFileName;
            krb5ConfPath = localDir + File.separator + krb5FileName;
        }
        logger.info("Get keytabPath: {}, krb5ConfPath: {}", keytabPath, krb5ConfPath);
        return new String[]{keytabPath, krb5ConfPath};
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
