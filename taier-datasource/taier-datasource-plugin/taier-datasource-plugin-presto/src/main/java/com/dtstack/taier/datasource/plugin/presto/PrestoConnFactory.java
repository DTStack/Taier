package com.dtstack.taier.datasource.plugin.presto;

import com.dtstack.taier.datasource.plugin.common.exception.ErrorCode;
import com.dtstack.taier.datasource.plugin.rdbms.ConnFactory;
import com.dtstack.taier.datasource.api.exception.SourceException;
import com.dtstack.taier.datasource.api.source.DataBaseType;

/**
 * presto 数据源连接工厂类
 *
 * @author ：wangchuan
 * date：Created in 上午9:50 2021/3/23
 * company: www.dtstack.com
 */
public class PrestoConnFactory extends ConnFactory {

    public PrestoConnFactory() {
        driverName = DataBaseType.Presto.getDriverClassName();
        this.errorPattern = new PrestoErrorPattern();
        testSql = DataBaseType.Presto.getTestSql();
    }

    @Override
    protected String getCallProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }

    protected boolean supportTransaction() {
        return false;
    }

    protected boolean supportProcedure(String sql) {
        return false;
    }

    protected String getDropProc(String procName) {
        throw new SourceException(ErrorCode.NOT_SUPPORT.getDesc());
    }
}
