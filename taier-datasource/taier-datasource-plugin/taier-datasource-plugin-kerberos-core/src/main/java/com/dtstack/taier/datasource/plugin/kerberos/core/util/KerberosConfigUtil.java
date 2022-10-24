package com.dtstack.taier.datasource.plugin.kerberos.core.util;

import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.utils.PathUtils;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 10:36 2020/8/27
 * @Description：Kerberos 配置工具
 */
@Slf4j
public class KerberosConfigUtil {

    /**
     * 更改为相对路径
     *
     * @param kerberosConfig kerberos 配置
     * @param checkKey       需要更改的 key
     */
    public static void changeToRelativePath(Map<String, Object> kerberosConfig, String checkKey) {
        String path = MapUtils.getString(kerberosConfig, checkKey);
        if (StringUtils.isBlank(path) || !path.contains("/")) {
            return;
        }
        String[] splitPathArr = path.split("/");
        kerberosConfig.put(checkKey, splitPathArr[splitPathArr.length - 1]);
    }

    /**
     * 改动相对路径为绝对路径
     *
     * @param conf              kerberos 配置
     * @param localKerberosPath 本地文件夹路径
     * @param checkKey          替换的 key
     */
    public static void changeRelativePathToAbsolutePath(Map<String, Object> conf, String localKerberosPath, String checkKey) {
        String relativePath = MapUtils.getString(conf, checkKey);
        if (StringUtils.isBlank(relativePath)) {
            return;
        }

        // 如果目录超过三级，说明不是相对路径，已经被改为绝对路径了
        if (relativePath.split("/").length > 3) {
            return;
        }

        String absolutePath = PathUtils.removeMultiSeparatorChar(localKerberosPath + File.separator + relativePath);
        log.info("changeRelativePathToAbsolutePath checkKey:{} relativePath:{}, localKerberosConfPath:{}, absolutePath:{}", checkKey, relativePath, localKerberosPath, absolutePath);
        conf.put(checkKey, absolutePath);
    }

    /**
     * 从 Keytab 中获取 Principal 信息
     *
     * @param keytabPath
     * @return
     */
    public static List<String> getPrincipals(String keytabPath) {
        File file = new File(keytabPath);
        Keytab keytab;

        try {
            keytab = Keytab.loadKeytab(file);
        } catch (IOException e) {
            throw new SourceException(String.format("Failed to parse keytab file,%s", e.getMessage()), e);
        }

        if (CollectionUtils.isEmpty(keytab.getPrincipals())) {
            throw new SourceException("Principal in keytab is empty");
        }

        return keytab.getPrincipals().stream().map(PrincipalName::getName).collect(Collectors.toList());
    }

    /**
     * 将 Map 转换为 Configuration
     *
     * @param configMap
     * @return
     */
    public static Configuration getConfig(Map<String, Object> configMap) {
        Configuration conf = new Configuration(false);
        Iterator var2 = configMap.entrySet().iterator();

        while (var2.hasNext()) {
            Map.Entry<String, Object> entry = (Map.Entry) var2.next();
            if (entry.getValue() != null && !(entry.getValue() instanceof Map)) {
                conf.set(entry.getKey(), entry.getValue().toString());
            }
        }

        return conf;
    }

    /**
     * 从 JDBC_URL 中获取 Principal 信息
     *
     * @param url
     * @return
     */
    public static String getPrincipalFromUrl(String url) {
        if (StringUtils.isBlank(url)) {
            throw new SourceException("jdbcUrl is empty");
        }

        log.info("get url principal : {}", url);
        Matcher matcher = DtClassConsistent.PatternConsistent.JDBC_PATTERN.matcher(url);
        if (matcher.find()) {
            String params = matcher.group("param");
            String[] split = params.split(";");
            for (String param : split) {
                String[] keyValue = param.split("=");
                if (KerberosConstant.PRINCIPAL.equals(keyValue[0])) {
                    return keyValue.length > 1 ? keyValue[1] : StringUtils.EMPTY;
                }
            }
        }
        throw new SourceException("jdbcUrl not contain Principal information : " + url);
    }
}
