package com.dtstack.engine.sparkyarn.sparkyarn.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.dtstack.engine.base.util.HadoopConfTool;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SFTPHandler;
import com.dtstack.engine.sparkyarn.sparkyarn.SparkYarnConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KerberosUtils {
    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String USER_DIR = System.getProperty("user.dir");

    private static final String localhost = getLocalHostName();


    public static <T> T login(SparkYarnConfig config, Supplier<T> supplier) throws IOException {

        if (!config.isOpenKerberos()) {
            return supplier.get();
        }

        String fileName = config.getPrincipalFile();

        String remoteDir = config.getRemoteDir();
        String localPath = USER_DIR + remoteDir;

        File path = new File(localPath);
        if (!path.exists()){
            path.mkdirs();
        }

        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String keytabPath = handler.loadFromSftp(fileName, remoteDir, localPath,false);

        String krb5ConfName = config.getKrbName();
        String krb5ConfPath = "";
        if (StringUtils.isNotBlank(krb5ConfName)) {
            krb5ConfPath = handler.loadFromSftp(krb5ConfName, remoteDir, localPath, true);
        } else {
            handler.close();
        }
        return KerberosUtils.loginKerberosWithCallBack(config.getYarnConf(),keytabPath,KerberosUtils.getPrincipal(keytabPath),krb5ConfPath,supplier);
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

    public static <T> T loginKerberosWithCallBack(Map<String, Object> allConfig, String keytabPath, String principal, String krb5Conf, Supplier<T> supplier) {
        if (StringUtils.isNotEmpty(krb5Conf)) {
            System.setProperty(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, krb5Conf);
        }
        HadoopConf hadoopConf = new HadoopConf();
        hadoopConf.initYarnConf(allConfig);
        UserGroupInformation.setConfiguration(hadoopConf.getYarnConfiguration());
        try {
            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
            logger.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            logger.error("{}", keytabPath, e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }

    public static String localPath(SparkYarnConfig config){
        String fileName = config.getPrincipalFile();

        String remoteDir = config.getRemoteDir();
        String localDir = USER_DIR + remoteDir ;

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
            logger.error("Principal must not be null!");
        }
        return principal;
    }
}
