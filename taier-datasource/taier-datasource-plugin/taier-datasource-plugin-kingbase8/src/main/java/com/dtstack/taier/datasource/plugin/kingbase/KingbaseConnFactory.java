package com.dtstack.taier.datasource.plugin.kingbase;

import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;

/**
 * company: www.dtstack.com
 *
 * @author ：Nanqi
 * Date ：Created in 17:11 2020/09/01
 * Description：kingbase 连接工厂
 */
public class KingbaseConnFactory extends ConnFactory {
    public KingbaseConnFactory() {
        driverName = DataBaseType.KINGBASE8.getDriverClassName();
        errorPattern = new KingbaseErrorPattern();
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("create  procedure \"%s\" as \n", procName);
    }
}
