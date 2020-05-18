package com.dtstack.engine.master.utils;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.schedule.common.kerberos.KerberosConfigVerify;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


public class DtKerberosUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DtKerberosUtils.class);

    private static final String _HOST = "_HOST";

   /* public synchronized static void loginKerberos(Configuration conf) throws Exception {
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

        String principal = conf.get(HadoopConfTool.PRINCIPAL);
        String keyTabPath = conf.get(HadoopConfTool.PRINCIPAL_FILE);

        LOG.info("login kerberos, princiapl={}, path={}", principal, keyTabPath);
        UserGroupInformation.loginUserFromKeytab(principal, keyTabPath);
    }

    @Deprecated
    public synchronized static Configuration loginKerberosHdfs(Map<String, Object> confMap) {
        return loginKerberos(confMap, HadoopConfTool.DFS_NAMENODE_KERBEROS_PRINCIPAL, HadoopConfTool.DFS_NAMENODE_KEYTAB_FILE, HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);
    }

    public synchronized static Configuration loginKerberos(Map<String, Object> confMap) {
        return loginKerberos(confMap, HadoopConfTool.PRINCIPAL, HadoopConfTool.PRINCIPAL_FILE, HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF);
    }

    public synchronized static Configuration loginKerberos(Map<String, Object> confMap, String principal, String keytab, String krb5Conf) {
        confMap = KerberosConfigVerify.replaceHost(confMap);
        principal = MapUtils.getString(confMap, principal);
        keytab = MapUtils.getString(confMap, keytab);
        krb5Conf = MapUtils.getString(confMap, krb5Conf);
        if (StringUtils.isNotEmpty(keytab) && !keytab.contains("/")) {
            keytab = MapUtils.getString(confMap, HadoopConfTool.KEYTAB_PATH);
        }
        Preconditions.checkState(StringUtils.isNotEmpty(keytab), "keytab can not be empty");

        LOG.info("login kerberos, principal={}, path={}, krb5Conf={}", principal, keytab, krb5Conf);
        Configuration config = getConfig(confMap);
        if (StringUtils.isEmpty(principal) && StringUtils.isNotEmpty(keytab)) {
            principal = getPrincipal(keytab);
        }
        if (MapUtils.isNotEmpty(confMap) && StringUtils.isNotEmpty(principal) && StringUtils.isNotEmpty(keytab)) {
            try {
                Config.refresh();
                if (StringUtils.isNotEmpty(krb5Conf)) {
                    System.setProperty(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, krb5Conf);
                }
                config.set("hadoop.security.authentication", "Kerberos");
                UserGroupInformation.setConfiguration(config);
                LOG.info("login kerberos, currentUser={}", UserGroupInformation.getCurrentUser(), principal, keytab, krb5Conf);
                UserGroupInformation.loginUserFromKeytab(principal, keytab);
            } catch (Exception e) {
                LOG.error("Login fail with config:{} \n {}", confMap, e);
                throw new RdosDefineException("Kerberos Login fail");
            }
        }
        return config;
    }
*/
    public static String getPrincipal(String keytabPath) {
        File file = new File(keytabPath);
        Preconditions.checkState(file.exists() && file.isFile(), String.format("can't read keytab file (%s),it's  not exist or not a file", keytabPath));

        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(file);
        } catch (IOException e) {
            LOG.error("Keytab loadKeytab error {}", e);
            throw new RdosDefineException("解析keytab文件失败");
        }
        List<PrincipalName> names = keytab.getPrincipals();
        if (CollectionUtils.isNotEmpty(names)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("all principal from keytab={}:", file.getName());
                names.stream().forEach(name -> LOG.debug("principal={}", name.toString()));
            }
            PrincipalName principalName = names.get(0);
            if (Objects.nonNull(principalName)) {
                return principalName.getName();
            }
        }
        throw new RdosDefineException("当前keytab文件不包含principal信息");
    }

    /*public static Configuration getConfig(Map<String, Object> configMap) {
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
*/
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


    /**
     * 将上传的xml转换为map
     * @param resource
     * @param localKerberosConf
     * @return
     * @throws Exception
     */
    public static Map<String, Object> parseKerberosFromUpload(Pair<String, String> resource, String localKerberosConf) throws Exception {
        try {
            File oldKerberosFile = new File(localKerberosConf);
            if(oldKerberosFile.exists()){
                FileUtils.deleteDirectory(oldKerberosFile);
            }
        } catch (Exception e) {
            LOG.error("delete old kerberos dataJson file {} error",localKerberosConf,e);
        }
        Map<String, Map<String, String>> confMapMap = KerberosConfigVerify.parseKerberosFromUpload(resource.getRight(), localKerberosConf);
        if (MapUtils.isNotEmpty(confMapMap)) {
            Map<String, Object> map = new HashMap<>();
            confMapMap.values().stream().forEach(conf -> map.putAll(conf));
            checkPrincipalFile(localKerberosConf,map);
            map.put("dfs.namenode.kerberos.principal.pattern","*");
            return map;
        } else {
            throw new RdosDefineException("kerberos配置缺失");
        }
    }


    /**
     * 检查principalFile参数是否存在，且对应的keytab文件是否可用
     * 并解析principal参数
     *
     * @param localKerberosConf
     * @param map
     */
    private static void checkPrincipalFile(String localKerberosConf, Map<String, Object> map) {
        String principalFile = (String)map.get(HadoopConfTool.PRINCIPAL_FILE);
        if(StringUtils.isBlank(principalFile)) {
            //从localKerberosConf 获取对应的keyTab path
            principalFile = getPrincipalFilePath(localKerberosConf);
        }
        if (StringUtils.isNotEmpty(principalFile)) {
            map.put(HadoopConfTool.PRINCIPAL_FILE,principalFile);
            JSONObject obj = (JSONObject) JSONObject.toJSON(map);
            Map<String, String> replacedMap = KerberosConfigVerify.replaceFilePath(obj, localKerberosConf);
            String principal = DtKerberosUtils.getPrincipal(replacedMap.get(HadoopConfTool.PRINCIPAL_FILE));
            map.putIfAbsent(HadoopConfTool.PRINCIPAL, principal);
        } else {
            throw new RdosDefineException("principalFile参数未添加");
        }
    }

    private static String getPrincipalFilePath(String dir) {
        File file = null;
        File dirFile = new File(dir);
        if (dirFile.exists() && dirFile.isDirectory()) {
            File[] files = dirFile.listFiles();
            if (Objects.nonNull(files)){
                file = Arrays.stream(files).filter(f -> f.getName().endsWith(".keytab")).findFirst().orElseThrow(() -> new RdosDefineException("压缩包中不包含keytab文件"));
                if (Objects.nonNull(file)) {
                    return file.getPath();
                }
            }
        }
        return null;
    }

}