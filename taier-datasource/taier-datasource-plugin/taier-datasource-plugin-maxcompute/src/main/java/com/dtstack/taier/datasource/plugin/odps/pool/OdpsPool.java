package com.dtstack.taier.datasource.plugin.odps.pool;

import com.aliyun.odps.Odps;
import com.dtstack.taier.datasource.plugin.common.Pool;

/**
 * @company: www.dtstack.com
 * @Author ：wangchuan
 * @Date ：Created in 下午3:14 2020/8/3
 * @Description：
 */
public class OdpsPool extends Pool<Odps> {

    private OdpsPoolConfig config;

    public OdpsPool(OdpsPoolConfig config) {
        super(config, new OdpsPoolFactory(config));
        this.config = config;
    }

    public OdpsPoolConfig getConfig() {
        return config;
    }

    public void setConfig(OdpsPoolConfig config) {
        this.config = config;
    }

}
