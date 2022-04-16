package com.dtstack.taier.develop.flink.sql.side;

import com.dtstack.taier.common.util.AssertUtils;
import com.dtstack.taier.develop.flink.sql.core.AbstractBaseTable;
import com.dtstack.taier.develop.flink.sql.core.SqlConstant;

import java.util.List;
import java.util.Map;

/**
 * flink sql 维表抽象类
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public abstract class AbstractSideTable extends AbstractBaseTable {

    /**
     * 维表标识
     */
    protected static final String PERIOD_FOR_SYSTEM_TIME_KEY = "PERIOD FOR SYSTEM_TIME";

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
        AssertUtils.notBlank(getAllParam().getString(SqlConstant.SideTable.CACHE), "缓存策略不能为空.");
    }
}
