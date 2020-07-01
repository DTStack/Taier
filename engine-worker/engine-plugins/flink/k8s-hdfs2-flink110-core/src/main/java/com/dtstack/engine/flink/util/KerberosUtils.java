package com.dtstack.engine.flink.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.flink.FlinkConfig;
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

    public static void login(FlinkConfig config) throws Exception {

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
        LOG.info("Login successful for user " + principal + " using keytab file " + keytabPath);
    }
}