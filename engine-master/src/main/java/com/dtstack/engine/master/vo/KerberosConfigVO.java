package com.dtstack.engine.master.vo;

import com.dtstack.engine.domain.KerberosConfig;
import org.springframework.beans.BeanUtils;

import java.util.Map;

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

