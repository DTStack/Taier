package com.dtstack.engine.flink180huawei.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import com.dtstack.engine.flink180huawei.FlinkConfig;
import org.apache.commons.collections.MapUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String JAVA_SECURITY_KRB5_CONF_KEY = "java.security.krb5.conf";

    private static final String LOGIN_FAILED_CAUSE_PASSWORD_WRONG =
            "(wrong password) keytab file and user not match, you can kinit -k -t keytab user in client server to check";

    private static final String LOGIN_FAILED_CAUSE_TIME_WRONG =
            "(clock skew) time of local server and remote server not match, please check ntp to remote server";

    private static final String LOGIN_FAILED_CAUSE_AES256_WRONG =
            "(aes256 not support) aes256 not support by default jdk/jre, need copy local_policy.jar and US_export_policy.jar from remote server in path /opt/huawei/Bigdata/jdk/jre/lib/security";

    private static final String LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG =
            "(no rule) principal format not support by default, need add property hadoop.security.auth_to_local(in core-site.xml) value RULE:[1:$1] RULE:[2:$1]";

    private static final String LOGIN_FAILED_CAUSE_TIME_OUT =
            "(time out) can not connect to kdc server or there is fire wall in the network";

    private static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");

    private static final String KEYWORD_PRINCIPAL = "Principal";

    private static final String KEYWORD_KEYTAB = "Path";

    private static final String DIR = "/keytab";

    private static final String USER_DIR = System.getProperty("user.dir");

    private static final String FLINK_PRINCIPAL = "flinkPrincipal";

    private static final String FLINK_KEYTABPATH = "flinkKeytabPath";

    private static final String FLINK_KRB5CONFPATH = "flinkKrb5ConfPath";

    private static final String ZK_PRINCIPAL = "zkPrincipal";

    private static final String ZK_KEYTABPATH = "zkKeytabPath";

    private static final String ZK_LOGINNAME = "zkLoginName";

    private static final String localhost = getLocalHostName();

    public static void login(FlinkConfig config) throws IOException {
        Map<String, String> kerberosConfig = config.getKerberosConfig();

        String localKeytab = config.getLocalKeytab();
        String remoteDir = config.getRemoteDir();

        for (String key : kerberosConfig.keySet()) {
            if (key.contains(KEYWORD_PRINCIPAL)){
                kerberosConfig.put(key, KerberosUtils.getServerPrincipal(MapUtils.getString(kerberosConfig, key), "0.0.0.0"));
            } else if (key.contains(KEYWORD_KEYTAB)){
                String keytabPath = "";
                if (localKeytab != null){
                    keytabPath = localKeytab + MapUtils.getString(kerberosConfig, key);
                    LOG.info("Read localKeytab on: " + keytabPath);
                } else {
                    String localPath = USER_DIR + DIR + remoteDir + File.separator + localhost;
                    File dirs = new File(localPath);
                    if (!dirs.exists()){
                        dirs.mkdirs();
                    }
                    SFTPHandler handler = null;
                    try {
                        handler = SFTPHandler.getInstance(config.getSftpConf());
                        keytabPath = loadFromSftp(MapUtils.getString(kerberosConfig, key), remoteDir, localPath, handler);
                        LOG.info("load file from sftp: " + keytabPath);
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    } finally {
                        if (handler != null){
                            handler.close();
                        }
                    }
                }
                kerberosConfig.put(key, keytabPath);
            }
        }

        LOG.info("Get hadoop configuration.");
        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(config.getHadoopConf());
        Configuration hadoopConf = customerConf.getConfiguration();

        String userPrincipal = kerberosConfig.get(FLINK_PRINCIPAL);
        String userKeytabPath = kerberosConfig.get(FLINK_KEYTABPATH);
        String krb5ConfPath = kerberosConfig.get(FLINK_KRB5CONFPATH);
        String zkLoginName = kerberosConfig.get(ZK_LOGINNAME);
        String zkPrincipal = kerberosConfig.get(ZK_PRINCIPAL);
        String zkKeytabPath = kerberosConfig.get(ZK_KEYTABPATH);

        KerberosUtils.setJaasConf(zkLoginName, userPrincipal, userKeytabPath);
        KerberosUtils.setZookeeperServerPrincipal("zookeeper.server.principal", zkPrincipal);
        KerberosUtils.login(userPrincipal, userKeytabPath, krb5ConfPath, hadoopConf);
    }

    public synchronized static void login(String userPrincipal, String userKeytabPath, String krb5ConfPath, Configuration conf)
            throws IOException {
        // 1.check input parameters
        if ((userPrincipal == null) || (userPrincipal.length() <= 0)) {
            LOG.error("input userPrincipal is invalid.");
            throw new IOException("input userPrincipal is invalid.");
        }

        if ((userKeytabPath == null) || (userKeytabPath.length() <= 0)) {
            LOG.error("input userKeytabPath is invalid.");
            throw new IOException("input userKeytabPath is invalid.");
        }

        if ((krb5ConfPath == null) || (krb5ConfPath.length() <= 0)) {
            LOG.error("input krb5ConfPath is invalid.");
            throw new IOException("input krb5ConfPath is invalid.");
        }

        if ((conf == null)) {
            LOG.error("input conf is invalid.");
            throw new IOException("input conf is invalid.");
        }

        // 2.check file exsits
        File userKeytabFile = new File(userKeytabPath);
        if (!userKeytabFile.exists()) {
            LOG.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
            throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
        }
        if (!userKeytabFile.isFile()) {
            LOG.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
            throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
        }

        File krb5ConfFile = new File(krb5ConfPath);
        if (!krb5ConfFile.exists()) {
            LOG.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
            throw new IOException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
        }
        if (!krb5ConfFile.isFile()) {
            LOG.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
            throw new IOException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
        }

        setKrb5Config(krb5ConfFile.getAbsolutePath());
        setConfiguration(conf);
        LOG.info("set conf and check krb5config");

        // 4.login and check for hadoop
        loginHadoop(userPrincipal, userKeytabFile.getAbsolutePath());

        LOG.info("Login success!!!!!!!!!!!!!!");
    }

    private static void setConfiguration(Configuration conf) throws IOException {
        UserGroupInformation.setConfiguration(conf);
    }

    private static void setKrb5Config(String krb5ConfFile)
            throws IOException {
        System.setProperty(JAVA_SECURITY_KRB5_CONF_KEY, krb5ConfFile);
        String ret = System.getProperty(JAVA_SECURITY_KRB5_CONF_KEY);
        if (ret == null) {
            LOG.error(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
            throw new IOException(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
        }
        if (!ret.equals(krb5ConfFile)) {
            LOG.error(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfFile + ".");
            throw new IOException(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfFile + ".");
        }
    }

    public static void setJaasConf(String loginContextName, String principal, String keytabFile)
            throws IOException {

        File userKeytabFile = new File(keytabFile);

        javax.security.auth.login.Configuration.setConfiguration(new JaasConfiguration(loginContextName, principal,
                userKeytabFile.getAbsolutePath()));

        javax.security.auth.login.Configuration conf = javax.security.auth.login.Configuration.getConfiguration();

        AppConfigurationEntry[] entrys = conf.getAppConfigurationEntry(loginContextName);

        boolean checkPrincipal = false;
        boolean checkKeytab = false;
        for (int i = 0; i < entrys.length; i++) {
            if (entrys[i].getOptions().get("principal").equals(principal)) {
                checkPrincipal = true;
            }

            if (IS_IBM_JDK) {
                if (entrys[i].getOptions().get("useKeytab").equals(keytabFile)) {
                    checkKeytab = true;
                }
            } else {
                if (entrys[i].getOptions().get("keyTab").equals(keytabFile)) {
                    checkKeytab = true;
                }
            }

        }

        if (!checkPrincipal) {
            LOG.error("AppConfigurationEntry named " + loginContextName + " does not have principal value of "
                    + principal + ".");
            throw new IOException("AppConfigurationEntry named " + loginContextName
                    + " does not have principal value of " + principal + ".");
        }

        if (!checkKeytab) {
            LOG.error("AppConfigurationEntry named " + loginContextName + " does not have keyTab value of "
                    + keytabFile + ".");
            throw new IOException("AppConfigurationEntry named " + loginContextName + " does not have keyTab value of "
                    + keytabFile + ".");
        }

    }

    public static void setZookeeperServerPrincipal(String zkServerPrincipalKey, String zkServerPrincipal)
            throws IOException {
        System.setProperty(zkServerPrincipalKey, zkServerPrincipal);
        String ret = System.getProperty(zkServerPrincipalKey);
        if (ret == null) {
            LOG.error(zkServerPrincipalKey + " is null.");
            throw new IOException(zkServerPrincipalKey + " is null.");
        }
        if (!ret.equals(zkServerPrincipal)) {
            LOG.error(zkServerPrincipalKey + " is " + ret + " is not " + zkServerPrincipal
                    + ".");
            throw new IOException(zkServerPrincipalKey + " is " + ret + " is not "
                    + zkServerPrincipal + ".");
        }
    }

    private static void loginHadoop(String principal, String keytabFile)
            throws IOException {
        try {
            UserGroupInformation.loginUserFromKeytab(principal, keytabFile);
        } catch (IOException e) {
            LOG.error("login failed with " + principal + " and " + keytabFile + ".");
            LOG.error("perhaps cause 1 is " + LOGIN_FAILED_CAUSE_PASSWORD_WRONG + ".");
            LOG.error("perhaps cause 2 is " + LOGIN_FAILED_CAUSE_TIME_WRONG + ".");
            LOG.error("perhaps cause 3 is " + LOGIN_FAILED_CAUSE_AES256_WRONG + ".");
            LOG.error("perhaps cause 4 is " + LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG + ".");
            LOG.error("perhaps cause 5 is " + LOGIN_FAILED_CAUSE_TIME_OUT + ".");

            throw e;
        }
    }

    /**
     * copy from hbase zkutil 0.94&0.98 A JAAS configuration that defines the login modules that we want to use for
     * login.
     */
    private static class JaasConfiguration extends javax.security.auth.login.Configuration {
        private static final Map<String, String> BASIC_JAAS_OPTIONS = new HashMap<String, String>();

        static {
            String jaasEnvVar = System.getenv("HBASE_JAAS_DEBUG");
            if (jaasEnvVar != null && "true".equalsIgnoreCase(jaasEnvVar)) {
                BASIC_JAAS_OPTIONS.put("debug", "true");
            }
        }

        private static final Map<String, String> KEYTAB_KERBEROS_OPTIONS = new HashMap<String, String>();

        static {
            if (IS_IBM_JDK) {
                KEYTAB_KERBEROS_OPTIONS.put("credsType", "both");
            } else {
                KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
                KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", "false");
                KEYTAB_KERBEROS_OPTIONS.put("doNotPrompt", "true");
                KEYTAB_KERBEROS_OPTIONS.put("storeKey", "true");
            }

            KEYTAB_KERBEROS_OPTIONS.putAll(BASIC_JAAS_OPTIONS);
        }


        private static final AppConfigurationEntry KEYTAB_KERBEROS_LOGIN = new AppConfigurationEntry(
                KerberosUtil.getKrb5LoginModuleName(), LoginModuleControlFlag.REQUIRED, KEYTAB_KERBEROS_OPTIONS);

        private static final AppConfigurationEntry[] KEYTAB_KERBEROS_CONF =
                new AppConfigurationEntry[]{KEYTAB_KERBEROS_LOGIN};

        private javax.security.auth.login.Configuration baseConfig;

        private final String loginContextName;

        private final boolean useTicketCache;

        private final String keytabFile;

        private final String principal;


        public JaasConfiguration(String loginContextName, String principal, String keytabFile) throws IOException {
            this(loginContextName, principal, keytabFile, keytabFile == null || keytabFile.length() == 0);
        }

        private JaasConfiguration(String loginContextName, String principal, String keytabFile, boolean useTicketCache) throws IOException {
            try {
                this.baseConfig = javax.security.auth.login.Configuration.getConfiguration();
            } catch (SecurityException e) {
                this.baseConfig = null;
            }
            this.loginContextName = loginContextName;
            this.useTicketCache = useTicketCache;
            this.keytabFile = keytabFile;
            this.principal = principal;

            initKerberosOption();
            LOG.info("JaasConfiguration loginContextName=" + loginContextName + " principal=" + principal
                    + " useTicketCache=" + useTicketCache + " keytabFile=" + keytabFile);
        }

        private void initKerberosOption() throws IOException {
            if (!useTicketCache) {
                if (IS_IBM_JDK) {
                    KEYTAB_KERBEROS_OPTIONS.put("useKeytab", keytabFile);
                } else {
                    KEYTAB_KERBEROS_OPTIONS.put("keyTab", keytabFile);
                    KEYTAB_KERBEROS_OPTIONS.put("useKeyTab", "true");
                    KEYTAB_KERBEROS_OPTIONS.put("useTicketCache", useTicketCache ? "true" : "false");
                }
            }
            KEYTAB_KERBEROS_OPTIONS.put("principal", principal);
        }

        public AppConfigurationEntry[] getAppConfigurationEntry(String appName) {
            if (loginContextName.equals(appName)) {
                return KEYTAB_KERBEROS_CONF;
            }
            if (baseConfig != null)
                return baseConfig.getAppConfigurationEntry(appName);
            return (null);
        }
    }

    public static String getServerPrincipal(String principalConfig, String hostname) throws IOException {
        String[] components = getComponents(principalConfig);
        return components != null && components.length == 3 && components[1].equals("_HOST") ? replacePattern(components, hostname) : principalConfig;
    }

    private static String[] getComponents(String principalConfig) {
        return principalConfig == null ? null : principalConfig.split("[/@]");
    }

    private static String replacePattern(String[] components, String hostname) throws IOException {
        String fqdn = hostname;
        if (hostname == null || hostname.isEmpty() || hostname.equals("0.0.0.0")) {
            fqdn = getLocalHostName();
        }

        return components[0] + "/" + fqdn.toLowerCase(Locale.US) + "@" + components[2];
    }

    static String getLocalHostName(){
        String localhost = "_HOST";
        try {
            localhost = InetAddress.getLocalHost().getCanonicalHostName();
            LOG.info("Localhost name is " + localhost);
        } catch (UnknownHostException e) {
            LOG.error("Get localhostname error: " + e);
        }
        return localhost;
    }

    private static String loadFromSftp(String fileName, String remoteDir, String localDir, SFTPHandler handler){
        String remoteFile = remoteDir + File.separator +  localhost + File.separator + fileName;
        String localFile = localDir + File.separator + fileName;
        if (new File(fileName).exists()){
            return fileName;
        } else {
            handler.downloadFile(remoteFile, localFile);
            return localFile;
        }
    }
}