package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.plugin.common.utils.DateUtil;
import com.dtstack.taier.datasource.plugin.common.utils.SftpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.security.UserGroupInformation;

import java.lang.reflect.Method;
import java.security.PrivilegedExceptionAction;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * kerberos util
 *
 * @author ：wangchuan
 * date：Created in 下午9:26 2022/2/28
 * company: www.dtstack.com
 */
@Slf4j
public class KerberosUtil {

    /**
     * kerberos login
     *
     * @param confMap  conf
     * @param supplier supplier
     * @param <T>      范型
     * @return 返回值
     */
    public static <T> T login(Map<String, Object> confMap, Supplier<T> supplier) {
        UserGroupInformation ugi = KerberosLoginUtil.loginWithUGI(confMap);
        try {
            return ugi.doAs((PrivilegedExceptionAction<T>) supplier::get);
        } catch (Exception e) {
            throw new SourceException(String.format("ugi doAs failed : %s", e.getMessage()), e);
        }
    }

    /**
     * 从 sftp 下载并替换为本地路径
     *
     * @param kerberosConfig kerberos 配置
     */
    public static void downloadAndReplace(Map<String, Object> kerberosConfig) {
        // 替换过路径的不再进行下载
        if (MapUtils.isEmpty(kerberosConfig)) {
            return;
        }
        String kerberosPath = download(kerberosConfig);
        if (StringUtils.isBlank(kerberosPath)) {
            return;
        }
        // 替换相对路径为绝对路径
        KerberosConfigUtil.changeRelativePathToAbsolutePath(kerberosConfig, kerberosPath, KerberosConstant.PRINCIPAL_FILE);
        KerberosConfigUtil.changeRelativePathToAbsolutePath(kerberosConfig, kerberosPath, KerberosConstant.KEYTAB_PATH);
        KerberosConfigUtil.changeRelativePathToAbsolutePath(kerberosConfig, kerberosPath, KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF);
    }

    /**
     * 处理 kerberos 文件, 从 sftp 下载并替换路径
     *
     * @param kerberosConf kerberos 配置
     */
    @SuppressWarnings("unchecked")
    private static String download(Map<String, Object> kerberosConf) {
        if (MapUtils.isEmpty(kerberosConf)) {
            return "";
        }
        Object sftpConfObj = MapUtils.getObject(kerberosConf, "sftpConf");
        String remoteDir = MapUtils.getString(kerberosConf, "remoteDir");
        // 兼容 engine 不上传 .lock 文件的情况, 避免认证文件重复下载
        Timestamp fileTimestamp = DateUtil.getTimestamp(MapUtils.getObject(kerberosConf, "kerberosFileTimestamp"));
        if (Objects.isNull(sftpConfObj) || StringUtils.isBlank(remoteDir)) {
            log.warn("sftp conf or remoteDir is null, conf:{} , remoteDir:{}", sftpConfObj, remoteDir);
            return "";
        }
        return SftpUtil.downloadKerberosDirFromSftp(remoteDir, (Map<String, String>) sftpConfObj, fileTimestamp);
    }

    /**
     * 获取 principal 的 domain
     *
     * @param principal principal
     * @return principal domain
     */
    public static String getDomainRealm(String principal) {
        String realmString = null;

        try {
            Class<?> classRef;
            if (System.getProperty("java.vendor").contains("IBM")) {
                classRef = Class.forName("com.ibm.security.krb5.PrincipalName");
            } else {
                classRef = Class.forName("sun.security.krb5.PrincipalName");
            }

            int tKrbNtSrvHst = classRef.getField("KRB_NT_SRV_HST").getInt(null);
            Object principalName = classRef.getConstructor(String.class, Integer.TYPE).newInstance(principal, tKrbNtSrvHst);
            realmString = (String) classRef.getMethod("getRealmString").invoke(principalName);
        } catch (Exception var5) {
            // ignore error
        }
        return null != realmString && !realmString.equals("") ? realmString : getDefaultRealm();
    }

    private static String getDefaultRealm() {
        try {
            Object kerbConf;
            Class<?> classRef;
            Method getInstanceMethod;
            Method getDefaultRealmMethod;
            if (System.getProperty("java.vendor").contains("IBM")) {
                classRef = Class.forName("com.ibm.security.krb5.internal.Config");
            } else {
                classRef = Class.forName("sun.security.krb5.Config");
            }
            getInstanceMethod = classRef.getMethod("getInstance");
            kerbConf = getInstanceMethod.invoke(classRef);
            getDefaultRealmMethod = classRef.getDeclaredMethod("getDefaultRealm"
            );
            return (String) getDefaultRealmMethod.invoke(kerbConf, new Object[0]);
        } catch (Exception e) {
            // ignore error
            log.error("get default realm error", e);
            return null;
        }
    }

}
