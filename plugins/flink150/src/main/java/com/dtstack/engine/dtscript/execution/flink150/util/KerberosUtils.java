package com.dtstack.engine.dtscript.execution.flink150.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.apache.log4j.Logger;

public class KerberosUtils {

    private static final Logger LOG = Logger.getLogger(KerberosUtils.class);

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

        // 3.set and check krb5config
        setKrb5Config(krb5ConfFile.getAbsolutePath());
        setConfiguration(conf);

        // 4.login and check for hadoop
        loginHadoop(userPrincipal, userKeytabFile.getAbsolutePath());

        LOG.info("Login success!!!!!!!!!!!!!!");
    }

    private static void setConfiguration(Configuration conf) throws IOException {
        UserGroupInformation.setConfiguration(conf);
    }

    private static boolean checkNeedLogin(String principal)
            throws IOException {
        if (!UserGroupInformation.isSecurityEnabled()) {
            LOG.error("UserGroupInformation is not SecurityEnabled, please check if core-site.xml exists in classpath.");
            throw new IOException(
                    "UserGroupInformation is not SecurityEnabled, please check if core-site.xml exists in classpath.");
        }
        UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();

        if ((currentUser != null) && (currentUser.hasKerberosCredentials())) {
            if (checkCurrentUserCorrect(principal)) {
                LOG.info("current user is " + currentUser + "has logined.");
                if (!currentUser.isFromKeytab()) {
                    LOG.error("current user is not from keytab.");
                    throw new IOException("current user is not from keytab.");
                }
                return false;
            } else {
                LOG.error("current user is " + currentUser + "has logined. please check your enviroment , especially when it used IBM JDK or kerberos for OS count login!!");
                throw new IOException("current user is " + currentUser + " has logined. And please check your enviroment!!");
            }
        }

        return true;
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
        if ((loginContextName == null) || (loginContextName.length() <= 0)) {
            LOG.error("input loginContextName is invalid.");
            throw new IOException("input loginContextName is invalid.");
        }

        if ((principal == null) || (principal.length() <= 0)) {
            LOG.error("input principal is invalid.");
            throw new IOException("input principal is invalid.");
        }

        if ((keytabFile == null) || (keytabFile.length() <= 0)) {
            LOG.error("input keytabFile is invalid.");
            throw new IOException("input keytabFile is invalid.");
        }

        File userKeytabFile = new File(keytabFile);
        if (!userKeytabFile.exists()) {
            LOG.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
            throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
        }

        javax.security.auth.login.Configuration.setConfiguration(new JaasConfiguration(loginContextName, principal,
                userKeytabFile.getAbsolutePath()));

        javax.security.auth.login.Configuration conf = javax.security.auth.login.Configuration.getConfiguration();
        if (!(conf instanceof JaasConfiguration)) {
            LOG.error("javax.security.auth.login.Configuration is not JaasConfiguration.");
            throw new IOException("javax.security.auth.login.Configuration is not JaasConfiguration.");
        }

        AppConfigurationEntry[] entrys = conf.getAppConfigurationEntry(loginContextName);
        if (entrys == null) {
            LOG.error("javax.security.auth.login.Configuration has no AppConfigurationEntry named " + loginContextName
                    + ".");
            throw new IOException("javax.security.auth.login.Configuration has no AppConfigurationEntry named "
                    + loginContextName + ".");
        }

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

    private static void checkAuthenticateOverKrb()
            throws IOException {
        UserGroupInformation loginUser = UserGroupInformation.getLoginUser();
        UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
        if (loginUser == null) {
            LOG.error("current user is " + currentUser + ", but loginUser is null.");
            throw new IOException("current user is " + currentUser + ", but loginUser is null.");
        }
        if (!loginUser.equals(currentUser)) {
            LOG.error("current user is " + currentUser + ", but loginUser is " + loginUser + ".");
            throw new IOException("current user is " + currentUser + ", but loginUser is " + loginUser + ".");
        }
        if (!loginUser.hasKerberosCredentials()) {
            LOG.error("current user is " + currentUser + " has no Kerberos Credentials.");
            throw new IOException("current user is " + currentUser + " has no Kerberos Credentials.");
        }
        if (!UserGroupInformation.isLoginKeytabBased()) {
            LOG.error("current user is " + currentUser + " is not Login Keytab Based.");
            throw new IOException("current user is " + currentUser + " is not Login Keytab Based.");
        }
    }

    private static boolean checkCurrentUserCorrect(String principal)
            throws IOException {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        if (ugi == null) {
            LOG.error("current user still null.");
            throw new IOException("current user still null.");
        }

        String defaultRealm = null;
        try {
            defaultRealm = KerberosUtil.getDefaultRealm();
        } catch (Exception e) {
            LOG.warn("getDefaultRealm failed.");
            throw new IOException(e);
        }

        if ((defaultRealm != null) && (defaultRealm.length() > 0)) {
            StringBuilder realm = new StringBuilder();
            StringBuilder principalWithRealm = new StringBuilder();
            realm.append("@").append(defaultRealm);
            if (!principal.endsWith(realm.toString())) {
                principalWithRealm.append(principal).append(realm);
                principal = principalWithRealm.toString();
            }
        }

        return principal.equals(ugi.getUserName());
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
}