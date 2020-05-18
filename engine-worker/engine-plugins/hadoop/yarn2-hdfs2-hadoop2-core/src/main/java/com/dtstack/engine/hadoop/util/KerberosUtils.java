package com.dtstack.engine.hadoop.util;

import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.hadoop.Config;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @Auther: jiangjunjie
 * @Date: 2020-03-13
 * @Description:
 */
public class KerberosUtils {
    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String DIR = "/keytab";

    private static final String USER_DIR = System.getProperty("user.dir");

    private static final String localhost = getLocalHostName();

    public static void login(Config config) throws IOException {

        String fileName = config.getPrincipalFile();

        String remoteDir = config.getRemoteDir();
        String localPath = USER_DIR + DIR;

        File path = new File(localPath);
        if (!path.exists()){
            path.mkdirs();
        }

        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String keytabPath = handler.loadFromSftp(fileName, remoteDir, localPath);
        Keytab keytab = Keytab.loadKeytab(new File(keytabPath));
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal;
        if (principals.size() != 0){
            principal = principals.get(0).getName();
        } else {
            throw new IOException("Principal must not be null!");
        }

        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(config.getHadoopConf());
        Configuration hadoopConf = customerConf.getConfiguration();
        UserGroupInformation.setConfiguration(hadoopConf);
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
        logger.info("Login successful for user " + principal + " using keytab file " + keytabPath);
    }

    static String getLocalHostName(){
        String localhost = "_HOST";
        try {
            localhost = InetAddress.getLocalHost().getCanonicalHostName();
            logger.info("Localhost name is " + localhost);
        } catch (UnknownHostException e) {
            logger.error("Get localhostname error: " + e);
        }
        return localhost;
    }
}
