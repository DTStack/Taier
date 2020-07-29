package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.KerberosConfig;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@ApiModel
public class KerberosConfigVO extends KerberosConfig {
    private String keytabPath;

    private Map hdfsConfig;

    public static KerberosConfigVO toVO(KerberosConfig config) {
        KerberosConfigVO kerberosConfigVO = new KerberosConfigVO();
        BeanUtils.copyProperties(config, kerberosConfigVO);
        return kerberosConfigVO;
    }

    public String getKeytabPath() {
        return keytabPath;
    }

    public void setKeytabPath(String keytabPath) {
        this.keytabPath = keytabPath;
    }

    public Map getHdfsConfig() {
        return hdfsConfig;
    }

    public void setHdfsConfig(Map hdfsConfig) {
        this.hdfsConfig = hdfsConfig;
    }
}

