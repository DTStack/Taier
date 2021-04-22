package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.KerberosConfig;
import java.sql.Timestamp;
import io.swagger.annotations.ApiModel;
import org.springframework.beans.BeanUtils;

import java.util.Map;

@ApiModel
public class KerberosConfigVO extends KerberosConfig {
    private String keytabPath;

    private Map hdfsConfig;

    private Timestamp kerberosFileTimestamp;



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

    public Timestamp getKerberosFileTimestamp() {
        return kerberosFileTimestamp;
    }

    public void setKerberosFileTimestamp(Timestamp kerberosFileTimestamp) {
        this.kerberosFileTimestamp = kerberosFileTimestamp;
    }
}

