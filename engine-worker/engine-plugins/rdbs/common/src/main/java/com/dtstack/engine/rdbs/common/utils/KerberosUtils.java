package com.dtstack.engine.rdbs.common.utils;

import com.dtstack.engine.base.util.HadoopConfTool;
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
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * @Auther: jiangjunjie
 * @Date: 2020-03-13
 * @Description:
 */
public class KerberosUtils {
    private static final Logger logger = LoggerFactory.getLogger(KerberosUtils.class);

    private static final String USER_DIR = System.getProperty("user.dir") + File.separator + "kerberosPath";

    public static <T> T login(Properties properties, Supplier<T> supplier) throws Exception {
        int open = Objects.isNull(properties.get("openKerberos")) ? 0 : (int)properties.get("openKerberos");
        boolean openKerberos = 1 == open;

        if (!openKerberos) {
            return supplier.get();
        }

        Map config = (Map)properties.get("config");

        String fileName = (String)config.get("principalFile");

        String remoteDir = (String)config.get("remoteDir");
        String localPath = USER_DIR + remoteDir;

        File path = new File(localPath);
        if (!path.exists()){
            path.mkdirs();
        }

        SFTPHandler handler = SFTPHandler.getInstance((Map)properties.get("sftpConf"));
        String keytabPath = handler.loadFromSftp(fileName, remoteDir, localPath,false);

        String krb5ConfName = (String)config.get("krbName");
        String krb5ConfPath = "";
        if (StringUtils.isNotBlank(krb5ConfName)) {
            krb5ConfPath = handler.loadFromSftp(krb5ConfName, remoteDir, localPath, true);
        } else {
            handler.close();
        }

        Keytab keytab = Keytab.loadKeytab(new File(keytabPath));
        List<PrincipalName> principals = keytab.getPrincipals();
        String principal;
        if (principals.size() != 0){
            principal = principals.get(0).getName();
        } else {
            throw new IOException("Principal must not be null!");
        }
       return KerberosUtils.loginKerberosWithCallBack((Map<String, Object>) config.get("yarnConf"),keytabPath,principal,krb5ConfPath,supplier);
    }

    public static <T> T loginKerberosWithCallBack(Map<String, Object> allConfig, String keytabPath, String principal, String krb5Conf, Supplier<T> supplier) {
        if (StringUtils.isNotEmpty(krb5Conf)) {
            System.setProperty(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, krb5Conf);
        }
        Configuration configuration = new Configuration();
        for (String key : allConfig.keySet()) {
            configuration.set(key,String.valueOf(allConfig.get(key)));
        }
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

}
