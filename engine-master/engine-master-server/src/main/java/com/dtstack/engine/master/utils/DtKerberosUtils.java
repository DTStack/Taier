package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.kerberos.KerberosConfigVerify;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.authentication.util.KerberosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.Config;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import java.util.Map;

public class DtKerberosUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DtKerberosUtils.class);

    private static final String _HOST = "_HOST";

    public synchronized static void loginKerberos(Configuration conf) throws Exception {
        UserGroupInformation.setConfiguration(conf);

        String principal = getServerPrincipal(HadoopConfTool.getPrincipal(conf), "0.0.0.0");
        String keyTabPath = HadoopConfTool.getKeyTabFile(conf);

        UserGroupInformation.loginUserFromKeytab(principal, keyTabPath);
    }

    public synchronized static Configuration loginKerberosHbase(Map<String, Object> confMap) {
        return loginKerberos(confMap, HadoopConfTool.KEY_HBASE_MASTER_KERBEROS_PRINCIPAL, HadoopConfTool.KEY_HBASE_MASTER_KEYTAB_FILE, HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);
    }


    public synchronized static void loginKerberosHdfs(Configuration conf) throws Exception {
        UserGroupInformation.setConfiguration(conf);

        String principal = HadoopConfTool.getHdfsPrincipal(conf);
        String keyTabPath = HadoopConfTool.getHdfsKeytab(conf);

        LOG.info("login kerberos, princiapl={}, path={}", principal, keyTabPath);
        UserGroupInformation.loginUserFromKeytab(principal, keyTabPath);
    }

    public synchronized static Configuration loginKerberosHdfs(Map<String, Object> confMap) {
        return loginKerberos(confMap, HadoopConfTool.DFS_NAMENODE_KERBEROS_PRINCIPAL, HadoopConfTool.DFS_NAMENODE_KEYTAB_FILE, HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);
    }

    public synchronized static Configuration loginKerberos(Map<String, Object> confMap) {
        return loginKerberos(confMap, HadoopConfTool.HIVE_SERVER2_AUTHENTICATION_KERBEROS_PRINCIPAL, HadoopConfTool.HIVE_SERVER2_AUTHENTICATION_KERBEROS_KEYTAB, HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);
    }

    public synchronized static Configuration loginKerberos(Map<String, Object> confMap, String principal, String keytab, String krb5Conf) {
        confMap = KerberosConfigVerify.replaceHost(confMap);
        principal = MapUtils.getString(confMap, principal);
        keytab = MapUtils.getString(confMap, keytab);
        krb5Conf = MapUtils.getString(confMap, krb5Conf);

        Configuration config = getConfig(confMap);
        if (MapUtils.isNotEmpty(confMap) && StringUtils.isNotEmpty(principal) && StringUtils.isNotEmpty(keytab)) {
            try {
                Config.refresh();
                if (StringUtils.isNotEmpty(krb5Conf)) {
                    System.setProperty(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, krb5Conf);
                }
                config.set("hadoop.security.authentication", "Kerberos");
                UserGroupInformation.setConfiguration(config);
                LOG.info("login kerberos, princiapl={}, path={}, krb5Conf={}", principal, keytab, krb5Conf);
                UserGroupInformation.loginUserFromKeytab(principal, keytab);
            } catch (Exception e) {
                LOG.error("Login fail with config:{} \n {}", confMap, e);
                throw new RdosDefineException("Kerberos Login fail");
            }
        }
        return config;
    }

    public static Configuration getConfig(Map<String, Object> configMap) {
        Configuration conf = new Configuration();
        for (Map.Entry<String, Object> entry : configMap.entrySet()) {
            if (entry.getValue() != null && !(entry.getValue() instanceof Map)) {
                conf.set(entry.getKey(), entry.getValue().toString());
            }
        }

        return conf;
    }

    public synchronized static void loginKerberos(Configuration conf, String principal, String keyTabPath) throws Exception {
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation.loginUserFromKeytab(principal, keyTabPath);
    }

    public static boolean needLoginKerberos(Configuration conf) throws IOException {
        boolean authorization = Boolean.parseBoolean(conf.get(HadoopConfTool.IS_HADOOP_AUTHORIZATION));

        return authorization;

//        if (!authorization){
//            return false;
//        }

//        String principal = HadoopConfTool.getPrincipal(conf);
//        UserGroupInformation currentUser = UserGroupInformation.getCurrentUser();
//        if ((currentUser != null) && (currentUser.hasKerberosCredentials())) {
//            return !checkCurrentUserCorrect(principal);
//        }
//
//        return true;
    }

    private static boolean checkCurrentUserCorrect(String principal)
            throws IOException {
        UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        if (ugi == null) {
            return false;
        }

        String defaultRealm;
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

    static String getLocalHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getCanonicalHostName();
    }
}