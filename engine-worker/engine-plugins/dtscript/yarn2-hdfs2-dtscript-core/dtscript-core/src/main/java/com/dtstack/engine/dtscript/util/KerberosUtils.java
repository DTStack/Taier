package com.dtstack.engine.dtscript.util;

import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SFTPHandler;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class KerberosUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String DIR = "/keytab";

    private static final String USER_DIR = System.getProperty("user.dir") + File.separator + "kerberosPath";

    private static final String REMOTEDIR = "remoteDir";

    private static final String PRINCIPALFILE = "principalFile";

    private static final String KRBNAME = "krbName";

    private static final String KRB5_CONF = "java.security.krb5.conf";


    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_TIMEOUT = "timeout";
    private static final String KEY_RSA = "rsaPath";
    private static final String KEY_AUTHENTICATION = "auth";

    public static <T> T login(Configuration configuration, Supplier<T> supplier){
        if (!isOpenKerberos(configuration)) {
            return supplier.get();
        }
        String krb5ConfName = configuration.get(KRBNAME);
        String fileName = configuration.get(PRINCIPALFILE);
        String remoteDir = configuration.get(REMOTEDIR);
        String localPath = USER_DIR + remoteDir;
        File path = new File(localPath);
        if (!path.exists()){
            path.mkdirs();
        }

        SFTPHandler handler = SFTPHandler.getInstance(KerberosUtils.getSftp(configuration));
        String keytabPath = handler.loadFromSftp(fileName, remoteDir, localPath,false);
        String krb5ConfPath = "";
        if (StringUtils.isNotBlank(krb5ConfName)) {
            krb5ConfPath = handler.loadFromSftp(krb5ConfName, remoteDir, localPath, true);
        } else {
            handler.close();
        }
        return KerberosUtils.loginKerberosWithCallBack(configuration,keytabPath,KerberosUtils.getPrincipal(keytabPath),krb5ConfPath,supplier);
    }

    public static <T> T loginKerberosWithCallBack(Configuration configuration, String keytabPath, String principal, String krb5Conf, Supplier<T> supplier) {
        if (StringUtils.isNotEmpty(krb5Conf)) {
            System.setProperty(KRB5_CONF, krb5Conf);
        }
        UserGroupInformation.setConfiguration(configuration);
        try {
            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
            LOG.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            LOG.error("{}", keytabPath, e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }

    public static boolean isOpenKerberos(Configuration config){
        return "true".equals(config.get("openKerberos"));
    }

    private static Map<String, String> getSftp(Configuration config){
        Map<String, String> sftpConfig = new HashMap<>();
        sftpConfig.put(KEY_USERNAME, config.get(KEY_USERNAME));
        sftpConfig.put(KEY_PASSWORD, config.get(KEY_PASSWORD));
        sftpConfig.put(KEY_HOST, config.get(KEY_HOST));
        sftpConfig.put(KEY_PORT, config.get(KEY_PORT));
        sftpConfig.put(KEY_TIMEOUT, config.get(KEY_TIMEOUT));
        sftpConfig.put(KEY_RSA, config.get(KEY_RSA));
        sftpConfig.put(KEY_AUTHENTICATION, config.get(KEY_AUTHENTICATION));
        return sftpConfig;
    }

    public static String localPath(Configuration config){
        String fileName = config.get(PRINCIPALFILE);
        String remoteDir = config.get(REMOTEDIR);

        String localDir = USER_DIR + DIR;
        File dirs = new File(localDir);
        if (!dirs.exists()){
            dirs.mkdirs();
        }
        SFTPHandler handler = SFTPHandler.getInstance(getSftp(config));
        String localPath= handler.loadFromSftp(fileName, remoteDir, localDir);

        return localPath;
    }

    public static String getPrincipal(String filePath) {
        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(new File(filePath));
        } catch (Exception e) {
            LOG.error("getPrincipal errror {}", filePath, e);
            throw new RdosDefineException("Principal must not be null!");
        }
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal;
        if (principals.size() != 0) {
            principal = principals.get(0).getName();
        } else {
            throw new RdosDefineException("Principal must not be null!");
        }
        return principal;
    }
}