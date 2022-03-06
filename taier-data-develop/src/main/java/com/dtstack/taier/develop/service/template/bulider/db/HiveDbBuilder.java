package com.dtstack.taier.develop.service.template.bulider.db;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.dtcenter.loader.client.ClientCache;
import com.dtstack.dtcenter.loader.dto.source.ISourceDTO;
import com.dtstack.dtcenter.loader.source.DataSourceType;
import com.dtstack.taier.common.env.EnvironmentContext;
import com.dtstack.taier.common.exception.ErrorCode;
import com.dtstack.taier.common.exception.RdosDefineException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;


@Component
public class HiveDbBuilder extends AbsRdbmsDbBuilder {
    @Autowired
    EnvironmentContext environmentContext;
    @Override
    public DataSourceType getDataSourceType() {
        return DataSourceType.HIVE;
    }


    private String generalCreateSql(List<JSONObject> writerColumns, List<String> partList, String
            tableName, boolean lifecycle, String tableComment) {
        StringBuilder createSql = new StringBuilder();
        createSql.append("CREATE TABLE ").append("`" + tableName + "`").append(" (");
        Iterator<JSONObject> it = writerColumns.iterator();
        while (true) {
            JSONObject writerColumn = it.next();
            createSql.append("`" + writerColumn.getString("key") + "`").append(" ").append(writerColumn.getString("type"))
                    .append(" COMMENT '" + (StringUtils.isNotEmpty(writerColumn.getString("comment")) ? writerColumn.getString("comment") : "") + "'");
            if (!it.hasNext()) {
                break;
            }
            createSql.append(",");
        }
        createSql.append(")").append(" ");
        createSql.append(String.format("comment '%s'", (StringUtils.isNotEmpty(tableComment) ? tableComment : "")));
        createSql.append(" partitioned by (");
        if (CollectionUtils.isNotEmpty(partList)) {
            for (int i = 0; i < partList.size(); i++) {
                String part = partList.get(i);
                createSql.append(part).append(" STRING");
                if (i < partList.size() - 1) {
                    createSql.append(",");
                }
            }
        }else {
            createSql.append("pt STRING");
        }
        createSql.append(String.format(") stored as %s \n ", this.environmentContext.getCreateTableType()));
        if (lifecycle) {
            createSql.append("lifecycle 10;");
        }
        return createSql.toString();
    }
}
