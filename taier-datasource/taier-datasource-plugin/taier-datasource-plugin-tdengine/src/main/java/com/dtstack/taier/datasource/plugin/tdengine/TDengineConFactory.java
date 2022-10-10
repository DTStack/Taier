package com.dtstack.taier.datasource.plugin.tdengine;

import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.source.DataBaseType;

/**
 * TDengine连接工厂类
 *
 * @author luming
 * date 2022/3/31
 */
public class TDengineConFactory extends ConnFactory {
    public TDengineConFactory() {
        this.driverName = DataBaseType.TDengine.getDriverClassName();
    }
}
