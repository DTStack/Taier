package com.dtstack.engine.dtscript.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dtstack.engine.common.util.SFTPHandler;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String DIR = "/keytab";

    private static final String USER_DIR = System.getProperty("user.dir");

    private static final String REMOTEDIR = "remoteDir";

    private static final String PRINCIPALFILE = "principalFile";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_HOST = "host";
    private static final String KEY_PORT = "port";
    private static final String KEY_TIMEOUT = "timeout";
    private static final String KEY_RSA = "rsaPath";
    private static final String KEY_AUTHENTICATION = "auth";

    public static void login(Configuration config) throws IOException {

        String keytabPath = localPath(config);
        String principal = getPrincipal(keytabPath);

        UserGroupInformation.setConfiguration(config);
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
        LOG.info("Login successful for user " + principal + " using keytab file " + keytabPath);
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

    public static String getPrincipal(String filePath) throws IOException {
        Keytab keytab = Keytab.loadKeytab(new File(filePath));
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal;
        if (principals.size() != 0){
            principal = principals.get(0).getName();
        } else {
            throw new IOException("Principal must not be null!");
        }
        return principal;
    }
}