package com.dtstack.taier.datasource.plugin.dm;

import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 11:23 2020/4/17
 * @Description：达梦连接工厂
 */
public class DmConnFactory extends ConnFactory {
    public DmConnFactory() {
        driverName = DataBaseType.DMDB.getDriverClassName();
    }
}
