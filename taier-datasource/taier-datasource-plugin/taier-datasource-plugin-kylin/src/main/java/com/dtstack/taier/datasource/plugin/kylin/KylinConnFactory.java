package com.dtstack.taier.datasource.plugin.kylin;

import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 19:15 2020/1/7
 * @Description：Kylin 连接工厂
 */
public class KylinConnFactory extends ConnFactory {
    public KylinConnFactory() {
        this.driverName = DataBaseType.Kylin.getDriverClassName();
        this.testSql = DataBaseType.Kylin.getTestSql();
        this.errorPattern = new KylinErrorPattern();
    }
}
