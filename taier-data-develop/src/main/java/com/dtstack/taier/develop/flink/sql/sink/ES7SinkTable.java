package com.dtstack.taier.develop.flink.sql.sink;


import com.dtstack.taier.develop.flink.sql.core.ISqlParamEnum;
import com.dtstack.taier.develop.flink.sql.sink.param.ES7SinkParamEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

import static com.dtstack.taier.develop.enums.develop.FlinkVersion.FLINK_112;


/**
 * mysql 结果表
 *
 * @author ：qianyi
 * company: www.dtstack.com
 */
public class ES7SinkTable extends AbstractSinkTable {

    @Override
    protected void addSelfParam(Map<String, Object> tableParam) {
        super.addSelfParam(tableParam);
    }

    @Override
    public ISqlParamEnum[] getSqlParamEnums() {
        return ES7SinkParamEnum.values();
    }

    @Override
    protected void addTableStructureParam(List<String> tableStructure) {
        if (FLINK_112.equals(version)) {
            String id = (String) getAllParam().get(ES7SinkParamEnum.id.getFront());
            if (StringUtils.isNotEmpty(id)) {
                tableStructure.add(String.format(PRIMARY_KEY_TEMPLATE, id));
            }
        } else {
            super.addTableStructureParam(tableStructure);
        }
    }

    @Override
    protected String getTypeBeforeVersion112() {
        return "elasticsearch7";
    }

    @Override
    protected String getTypeVersion112() {
        return "elasticsearch7-x";
    }
}
