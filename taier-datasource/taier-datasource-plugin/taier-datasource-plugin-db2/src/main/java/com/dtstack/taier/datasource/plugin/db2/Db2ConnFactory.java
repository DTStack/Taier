package com.dtstack.taier.datasource.plugin.db2;

import com.dtstack.taier.datasource.api.source.DataBaseType;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 16:09 2020/1/7
 * @Description：Db2 工厂类
 */
public class Db2ConnFactory extends ConnFactory {
    public Db2ConnFactory() {
        this.driverName = DataBaseType.DB2.getDriverClassName();
        this.errorPattern = new Db2ErrorPattern();
    }

    @Override
    protected String getCreateProcHeader(String procName) {
        return String.format("CREATE PROCEDURE %s() \n", procName);
    }

    @Override
    public String getCallProc(String procName) {
        return String.format("call %s()", procName);
    }

    @Override
    public String getDropProc(String procName) {
        return String.format("DROP PROCEDURE %s", procName);
    }
}
