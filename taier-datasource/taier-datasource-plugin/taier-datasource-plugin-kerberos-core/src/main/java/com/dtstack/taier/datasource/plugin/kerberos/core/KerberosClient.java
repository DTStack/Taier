package com.dtstack.taier.datasource.plugin.kerberos.core;

import com.dtstack.taier.datasource.plugin.common.DtClassConsistent;
import com.dtstack.taier.datasource.plugin.common.utils.Xml2JsonUtil;
import com.dtstack.taier.datasource.plugin.common.utils.ZipUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosConfigUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosLoginUtil;
import com.dtstack.taier.datasource.plugin.kerberos.core.util.KerberosUtil;
import com.dtstack.taier.datasource.api.client.IKerberos;
import com.dtstack.taier.datasource.api.dto.source.AbstractSourceDTO;
import com.dtstack.taier.datasource.api.dto.source.ISourceDTO;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.plugin.common.constant.KerberosConstant;
import com.dtstack.taier.datasource.api.utils.AssertUtils;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.security.UserGroupInformation;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * kerberos 客户端
 *
 * @author ：wangchuan
 * date：Created in 上午10:38 2021/8/11
 * company: www.dtstack.com
 */
@Slf4j
public class KerberosClient implements IKerberos {

    @Override
    public Map<String, Object> parseKerberosFromUpload(String zipLocation, String localKerberosPath) {
        ZipUtil.unzipFile(zipLocation, localKerberosPath);
        return parseKerberosFromLocalDir(localKerberosPath);
    }

    @Override
    public void changeToRelativePath(Map<String, Object> kerberosConfig) {
        if (MapUtils.isEmpty(kerberosConfig)) {
            return;
        }
        KerberosConfigUtil.changeToRelativePath(kerberosConfig, KerberosConstant.PRINCIPAL_FILE);
        KerberosConfigUtil.changeToRelativePath(kerberosConfig, KerberosConstant.KEYTAB_PATH);
        KerberosConfigUtil.changeToRelativePath(kerberosConfig, KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF);
    }

    @Override
    public String getPrincipals(ISourceDTO sourceDTO, String url) {
        return KerberosConfigUtil.getPrincipalFromUrl(url);
    }

    @Override
    public List<String> getPrincipals(Map<String, Object> kerberosConfig) {
        // 兼容 sourceDTO 中传 kerberos 的情况
        KerberosUtil.downloadAndReplace(kerberosConfig);
        String principalFile = MapUtils.getString(kerberosConfig, KerberosConstant.PRINCIPAL_FILE);
        if (StringUtils.isBlank(principalFile)) {
            throw new SourceException("get Principal message，Keytab setting not exits");
        }
        return KerberosConfigUtil.getPrincipals(principalFile);
    }

    @Override
    public Map<String, Object> parseKerberosFromLocalDir(String kerberosDir) {
        AssertUtils.notBlank(kerberosDir, "local kerberos dir can't be null.");
        File kerberosFile = new File(kerberosDir);
        AssertUtils.isTrue(kerberosFile.exists(), String.format("local kerberos dir: [%s] is not exists.", kerberosDir));
        AssertUtils.isTrue(kerberosFile.isDirectory(), String.format("local kerberos path: [%s] is not directory.", kerberosDir));
        File[] kerberosFiles = kerberosFile.listFiles();
        AssertUtils.isTrue(Objects.nonNull(kerberosFiles) && kerberosFiles.length > 0, String.format("the kerberos dir [%s] is empty.", kerberosDir));
        List<File> kerberosFileList = Arrays.stream(kerberosFiles).filter(file -> !file.getName().startsWith(".")).collect(Collectors.toList());

        String principalFilePath = kerberosFileList.stream()
                .filter(kerFile -> kerFile.getName().endsWith(".keytab"))
                .map(File::getAbsolutePath)
                .findAny()
                .orElseThrow(() -> new SourceException(String.format("the kerberos dir [%s] does not contain files ending in .keytab", kerberosDir)));

        String krb5ConfPath = kerberosFileList.stream()
                .filter(kerFile -> kerFile.getName().equals("krb5.conf"))
                .map(File::getAbsolutePath)
                .findAny()
                .orElseThrow(() -> new SourceException(String.format("the kerberos dir [%s] does not contain files equal krb5.conf", kerberosDir)));

        Map<String, Object> confMap = Maps.newHashMap();
        confMap.put(KerberosConstant.PRINCIPAL_FILE, principalFilePath);
        confMap.put(KerberosConstant.KEY_JAVA_SECURITY_KRB5_CONF, krb5ConfPath);
        confMap.put(KerberosConstant.LOCAL_KERBEROS_DIR, kerberosDir);

        // 获取 XML 文件并解析为 MAP
        List<File> xmlFileList = kerberosFileList.stream().filter(file -> file.getName().endsWith(DtClassConsistent.PublicConsistent.XML_SUFFIX))
                .collect(Collectors.toList());
        xmlFileList.forEach(file -> confMap.putAll(Xml2JsonUtil.xml2map(file)));
        return confMap;
    }

    @Override
    public Boolean authTest(ISourceDTO sourceDTO, Map<String, Object> kerberosConfig) {
        UserGroupInformation ugi = KerberosLoginUtil.loginWithUGI(kerberosConfig);
        AssertUtils.notNull(ugi, "ugi can't be null");
        return true;
    }
}