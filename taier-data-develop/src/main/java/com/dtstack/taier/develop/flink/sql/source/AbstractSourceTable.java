package com.dtstack.taier.develop.flink.sql.source;


import com.dtstack.taier.common.util.MapUtil;
import com.dtstack.taier.develop.enums.develop.FlinkVersion;
import com.dtstack.taier.develop.flink.sql.core.AbstractBaseTable;
import com.dtstack.taier.develop.flink.sql.core.SqlConstant;

import java.util.List;
import java.util.Map;

/**
 * flink sql 源表抽象类
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public abstract class AbstractSourceTable extends AbstractBaseTable {

    /**
     * 并行度信息
     */
    String PARALLELISM_112 = "scan.parallelism";

    @Override
    protected void addTableStructureParam(List<String> tableStructure) {
    }

    @Override
    protected void addBaseParam(Map<String, Object> tableParam) {
        // 区分版本
        if (FlinkVersion.FLINK_112.equals(getVersion())) {
            MapUtil.putIfValueNotNull(tableParam, PARALLELISM_112, getAllParam().getString(SqlConstant.PARALLELISM));
        } else {
            MapUtil.putIfValueNotNull(tableParam, SqlConstant.PARALLELISM, getAllParam().getString(SqlConstant.PARALLELISM));
        }
    }

    @Override
    public void checkParam() {
    }
}
