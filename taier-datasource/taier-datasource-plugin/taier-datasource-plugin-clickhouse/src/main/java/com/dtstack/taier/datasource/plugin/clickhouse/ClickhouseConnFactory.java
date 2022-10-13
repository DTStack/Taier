package com.dtstack.taier.datasource.plugin.clickhouse;

import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:58 2020/1/7
 * @Description：ClickHouse 连接工厂
 */
public class ClickhouseConnFactory extends ConnFactory {
    public ClickhouseConnFactory() {
        this.driverName = DataBaseType.Clickhouse.getDriverClassName();
        this.errorPattern = new ClickhouseErrorPattern();
    }
}
