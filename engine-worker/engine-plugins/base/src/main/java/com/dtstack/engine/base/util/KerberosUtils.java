package com.dtstack.engine.base.util;

import com.dtstack.engine.base.BaseConfig;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.common.util.SFTPHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public class KerberosUtils {
    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String USER_DIR = System.getProperty("user.dir") + File.separator + "kerberosPath";

    private static final String KRB5_CONF = "java.security.krb5.conf";

    private static final String KERBEROS_AUTH = "hadoop.security.authentication";
    private static final String KERBEROS_AUTH_TYPE = "kerberos";

    /**
     * @param config        任务外层配置
     * @param supplier
     * @param configuration 集群如yarn配置信息
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T login(BaseConfig config, Supplier<T> supplier, Map<String, Object> configuration) throws Exception {

        if (Objects.isNull(config) || config.isOpenKerberos()) {
            return supplier.get();
        }

        String fileName = config.getPrincipalFile();

        String remoteDir = config.getRemoteDir();
        String localPath = USER_DIR + remoteDir;

        File path = new File(localPath);
        if (!path.exists()) {
            path.mkdirs();
        }


        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        String keytabPath = handler.loadFromSftp(fileName, remoteDir, localPath, false);

        String krb5ConfName = config.getKrbName();
        String krb5ConfPath = "";
        if (org.apache.commons.lang3.StringUtils.isNotBlank(krb5ConfName)) {
            krb5ConfPath = handler.loadFromSftp(krb5ConfName, remoteDir, localPath, true);
        } else {
            handler.close();
        }
        logger.info("kerberos login remoteDir {} localPath {} krb5ConfPath {}", remoteDir, localPath, keytabPath);
        return KerberosUtils.loginKerberosWithCallBack(configuration, keytabPath,
                KerberosUtils.getPrincipal(keytabPath), krb5ConfPath, supplier);
    }

    public static <T> T loginKerberosWithCallBack(Map<String, Object> allConfig, String keytabPath, String principal, String krb5Conf, Supplier<T> supplier) {
        if (StringUtils.isNotEmpty(krb5Conf)) {
            System.setProperty(KRB5_CONF, krb5Conf);
        }
        Configuration configuration = new Configuration();
        for (String key : allConfig.keySet()) {
            configuration.set(key, String.valueOf(allConfig.get(key)));
        }
        configuration.set(KERBEROS_AUTH, KERBEROS_AUTH_TYPE);
        UserGroupInformation.setConfiguration(configuration);
        try {
            UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI(principal, keytabPath);
            logger.info("userGroupInformation current user = {} ugi user  = {} ", UserGroupInformation.getCurrentUser(), ugi.getUserName());
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            logger.error("{}", keytabPath, e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }

    public static String getPrincipal(String filePath) {
        Keytab keytab = null;
        try {
            keytab = Keytab.loadKeytab(new File(filePath));
        } catch (IOException e) {
            logger.error("Principal {} parse error e!", filePath);
            throw new RdosDefineException("keytab文件解析异常", e);
        }
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal = "";
        if (principals.size() != 0) {
            principal = principals.get(0).getName();
        } else {
            logger.error("Principal must not be null!");
        }
        return principal;
    }

    public static String localPath(BaseConfig config) {
        String fileName = config.getPrincipalFile();

        String remoteDir = config.getRemoteDir();
        String localDir = USER_DIR + remoteDir;

        File path = new File(localDir);
        if (!path.exists()) {
            path.mkdirs();
        }

        SFTPHandler handler = SFTPHandler.getInstance(config.getSftpConf());
        return handler.loadFromSftp(fileName, remoteDir, localDir);
    }
}
