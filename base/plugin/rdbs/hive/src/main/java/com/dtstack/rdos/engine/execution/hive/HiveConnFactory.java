package com.dtstack.rdos.engine.execution.hive;


import com.dtstack.rdos.engine.execution.rdbs.constant.ConfigConstant;
import com.dtstack.rdos.engine.execution.rdbs.executor.ConnFactory;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class HiveConnFactory extends ConnFactory {

    protected String hiveSubType;

    private static final String HIVE_SUB_TYPE = "hiveSubType";

    private static final String SUB_TYPE_INCEPTOR = "INCEPTOR";

    public HiveConnFactory() {
        driverName = "org.apache.hive.jdbc.HiveDriver";
        testSql = "show tables";
    }

    @Override
    public void init(Properties props) throws ClassNotFoundException {
        super.init(props);
        hiveSubType = props.getProperty(HIVE_SUB_TYPE);
    }


    @Override
    public boolean supportProcedure() {
        return false;
    }

    @Override
    public List<String> buildSqlList(String sql) {
        if(hiveSubType != null && hiveSubType.equalsIgnoreCase(SUB_TYPE_INCEPTOR)) {
            sql = "BEGIN\n" + sql + "\nEND;\n";
            List<String> sqlList = new ArrayList<>();
            sqlList.add(sql);
            return sqlList;
        } else {
            return Arrays.asList(sql.split(";"));
        }
    }

    @Override
    public String getCreateProcedureHeader(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCallProc(String procName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getDropProc(String procName) {
        throw new UnsupportedOperationException();
    }
}
