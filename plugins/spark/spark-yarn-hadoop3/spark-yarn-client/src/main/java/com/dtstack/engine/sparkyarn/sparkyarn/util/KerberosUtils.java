package com.dtstack.engine.sparkyarn.sparkyarn.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.sparkyarn.sparkyarn.SparkYarnConfig;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosUtils {

    private static final Logger LOG = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String DIR = "/keytab/";

    private static final String USER_DIR = System.getProperty("user.dir");


    public static void login(SparkYarnConfig config) throws IOException {


        String keytabPath = localPath(config);
        String principal = getPrincipal(keytabPath);

        HadoopConf customerConf = new HadoopConf();
        customerConf.initHadoopConf(config.getHadoopConf());
        Configuration hadoopConf = customerConf.getConfiguration();
        UserGroupInformation.setConfiguration(hadoopConf);
        UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
        LOG.info("Login successful for user " + principal + " using keytab file " + keytabPath);
    }

    public static String localPath(SparkYarnConfig config){
        String fileName = config.getPrincipalFile();

        String remoteDir = config.getRemoteDir();
        String localDir = USER_DIR + DIR ;

        File path = new File(localDir);
        if (!path.exists()){
            path.mkdirs();
        }

        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String localPath = handler.loadFromSftp(fileName, remoteDir, localDir);

        return localPath;
    }

    public static String getPrincipal(String filePath){
        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal = "";
        if (principals.size() != 0){
            principal = principals.get(0).getName();
        } else {
            LOG.error("Principal must not be null!");
        }
        return principal;
    }
}