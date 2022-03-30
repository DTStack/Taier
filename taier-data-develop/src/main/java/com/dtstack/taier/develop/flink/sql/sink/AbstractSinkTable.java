package com.dtstack.taier.develop.flink.sql.sink;


import com.dtstack.taier.develop.flink.sql.core.AbstractBaseTable;

import java.util.List;
import java.util.Map;

/**
 * flink sql 结果表抽象类
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public abstract class AbstractSinkTable extends AbstractBaseTable {

    @Override
    protected void addTableStructureParam(List<String> tableStructure) {
        addPrimaryKey(tableStructure);
    }

    @Override
    protected void addBaseParam(Map<String, Object> tableParam) {
    }

    @Override
    public void checkParam() {
        super.checkParam();
    }
}
