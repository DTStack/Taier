package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SFTPHandler;
import com.google.common.collect.Maps;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosName;
import org.apache.hadoop.util.Time;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class KerberosUtils {

    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String LOCAL_KEYTAB_DIR = USER_DIR + "/kerberos/keytab";
    private static final String LOCAL_KRB5_DIR = USER_DIR + "/kerberos/krb5";
    private static final String KRB5_CONF = "java.security.krb5.conf";
    private static final String KERBEROS_AUTH = "hadoop.security.authentication";
    private static final String SECURITY_TO_LOCAL = "hadoop.security.auth_to_local";
    private static final String KERBEROS_AUTH_TYPE = "kerberos";
    private static final String SECURITY_TO_LOCAL_DEFAULT = "RULE:[1:$1] RULE:[2:$1]";
    private static final String MODIFIED_TIME_KEY = "modifiedTime";

    private static Map<String, UserGroupInformation> ugiMap = Maps.newConcurrentMap();

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
        String localDir = String.format("%s/%s", LOCAL_KEYTAB_DIR, UUID.randomUUID());

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        try {
            logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());
            SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
            String keytabPath = handler.loadOverrideFromSftp(fileName, remoteDir, localDir, false);

            String krb5ConfName = config.getKrbName();
            String krb5ConfPath = "";
            if (StringUtils.isNotBlank(krb5ConfName)) {
                krb5ConfPath = handler.loadOverrideFromSftp(krb5ConfName, config.getRemoteDir(), localDir, true);
            }
            //krb5ConfPath = mergeKrb5(krb5ConfPath);

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
        } catch (Exception e) {
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        } finally {
            File file = new File(localDir);
            if (file.exists()){
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    logger.error("Delete dir failed: " + e);
                }
            }
        }
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


        try {
            String threadName = Thread.currentThread().getName();
            UserGroupInformation ugi = ugiMap.computeIfAbsent(threadName, k -> {
                return createUGI(krb5Conf, allConfig, principal, keytabPath);
            });
            KerberosTicket ticket = getTGT(ugi);
            if (!checkTGT(ticket)) {
                logger.info("Relogin after the ticket expired, principal {}", principal);
                ugi = createUGI(krb5Conf, allConfig, principal, keytabPath);
                ugiMap.put(threadName, ugi);
            }

            logger.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            logger.error("{}", keytabPath, e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }

    private static UserGroupInformation createUGI(String krb5Conf, Configuration config, String principal, String keytabPath) {
        try {
            synchronized (principal) {
                if (StringUtils.isNotEmpty(krb5Conf)) {
                    System.setProperty(KRB5_CONF, krb5Conf);
                }
                if (StringUtils.isEmpty(config.get(SECURITY_TO_LOCAL))) {
                    config.set(SECURITY_TO_LOCAL, SECURITY_TO_LOCAL_DEFAULT);
                }
                if (!StringUtils.equals(config.get(KERBEROS_AUTH), KERBEROS_AUTH_TYPE)) {
                    config.set(KERBEROS_AUTH, KERBEROS_AUTH_TYPE);
                }
                sun.security.krb5.Config.refresh();
                UserGroupInformation.setConfiguration(config);
                return UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
            }
        } catch (Exception e) {
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
            String krbtgt = "krbtgt/" + principal.getRealm() + "@" + principal.getRealm();
            if (principal != null && StringUtils.equals(principal.getName(), krbtgt)) {
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

    private static Set<String> mergeKrb5ContentKey(Map<String, HashMap<String, String>> remoteKrb5Content, Map<String, HashMap<String, String>> localKrb5Content) {
        remoteKrb5Content.remove(MODIFIED_TIME_KEY);
        localKrb5Content.remove(MODIFIED_TIME_KEY);

        Set<String> mapKeys = new HashSet<>();
        mapKeys.addAll(remoteKrb5Content.keySet());
        mapKeys.addAll(localKrb5Content.keySet());
        return mapKeys;
    }

    public static Map<String, HashMap<String, String>> readKrb5(String krb5Path) throws Exception {

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
        String localDir = String.format("%s/%s", LOCAL_KEYTAB_DIR, UUID.randomUUID());

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        logger.info("fileName:{}, remoteDir:{}, localDir:{}, sftpConf:{}", fileName, remoteDir, localDir, config.getSftpConf());
        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String keytabPath = handler.loadOverrideFromSftp(fileName, remoteDir, localDir, true);
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
