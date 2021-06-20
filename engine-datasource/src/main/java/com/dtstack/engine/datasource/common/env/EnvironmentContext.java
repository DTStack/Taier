package com.dtstack.engine.datasource.common.env;

import com.dtstack.dtcenter.loader.client.ClientCache;
import dt.insight.plat.lang.base.Strings;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Component
@Data
public class EnvironmentContext implements InitializingBean {

    @Value("${dtuic.url}")
    private String dtUicUrl;

    /**
     * 数据源插件地址
     */
    @Value("${datasource.plugin.path:}")
    private String dataSourcePluginPath;

    @Value("${kerberos.local.path:}")
    private String kerberosLocalPath;


    @Override
    public void afterPropertiesSet() throws Exception {
        ClientCache.setUserDir(getDataSourcePluginPath());
    }

    public String getKerberosLocalPath() {
        return Strings.isNotBlank(kerberosLocalPath) ? kerberosLocalPath : System.getProperty("user.dir") + File.separator + "kerberosConf";
    }

}
