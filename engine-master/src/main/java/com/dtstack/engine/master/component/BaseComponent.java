package com.dtstack.engine.master.component;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.common.enums.EComponentType;
import com.dtstack.dtcenter.common.hadoop.HadoopConfTool;
import com.dtstack.engine.common.exception.RdosDefineException;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class BaseComponent implements ComponentImpl {

    public static Logger LOG = LoggerFactory.getLogger(BaseComponent.class);

    protected Map<String, Object> allConfig;

    protected boolean openKerberos;

    protected EComponentType componentType;

    public BaseComponent(Map<String, Object> allConfig) {
        this.allConfig = allConfig;
        openKerberos = MapUtils.getBooleanValue(allConfig, "openKerberos", false);
    }

    @Override
    public String getJsonString() {
        return MapUtils.isEmpty(allConfig) ? "{}" : JSONObject.toJSONString(allConfig);
    }

    @Override
    public void checkConfig(){
    }

    /**
     * 检查开启Kerberos后的配置
     */
    @Deprecated
    private void checkKerberosConfig(){
        for (String key : getKerberosKey()) {
            if (allConfig.get(key) == null){
                throw new RdosDefineException(String.format("集群开启Kerberos认证时，%s插件必须填写[%s]配置",componentType.getName(),key));
            }
        }
    }

    protected List<String> getKerberosKey(){
        return Collections.EMPTY_LIST;
    }

    /**
     * 添加额外的配置
     */
    public void addExtraConfig(Map<String, Object> extraConfig){
        allConfig.putAll(extraConfig);
    }

    /**
     * 添加额外的配置
     */
    public void addExtraConfig(String key, Object value){
        allConfig.put(key, value);
    }

    public void setComponentType(EComponentType componentType) {
        this.componentType = componentType;
    }

    public boolean getOpenKerberos() {
        return openKerberos;
    }

    public void loginKerberos(Configuration configuration, String principal, String keytabPath, String krb5Conf) {
        try {
            if (StringUtils.isNotBlank(keytabPath) && StringUtils.isNotBlank(principal)) {
                File keytabFile = new File(keytabPath);
                if (!keytabFile.exists() || !keytabFile.isFile()) {
                    throw new RdosDefineException("keytab文件不存在");
                }
                if (StringUtils.isNotEmpty(principal)) {
                    String localhost = InetAddress.getLocalHost().getCanonicalHostName();
                    principal = principal.replace("_HOST", localhost);

                    if (StringUtils.isNotEmpty(krb5Conf)) {
                        System.setProperty(HadoopConfTool.KEY_JAVA_SECURITY_KRB5_CONF, krb5Conf);
                    }

                    UserGroupInformation.setConfiguration(configuration);
                    //logger
                    UserGroupInformation.loginUserFromKeytab(principal, keytabPath);
                    LOG.info("userGroupInformation current user = {}", UserGroupInformation.getCurrentUser());
                }
            }
        } catch (IOException e) {
            LOG.error("{}", e);
            throw new RdosDefineException("kerberos校验失败, Message:" + e.getMessage());
        }
    }
}

