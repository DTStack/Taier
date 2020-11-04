package com.dtstack.lineage.adapter;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import com.dtstack.engine.api.domain.LineageTableTable;
import com.dtstack.engine.api.enums.LineageOriginType;
import com.dtstack.engine.api.pojo.lineage.TableLineage;

import java.util.Map;
import java.util.Objects;

/**
 * @author chener
 * @Classname TableLineageAdapter
 * @Description
 * @Date 2020/11/4 11:51
 * @Created chener@dtstack.com
 */
public class TableLineageAdapter {
    public static TableLineage sqlTableLineage2ApiTableLineage(com.dtstack.engine.sql.TableLineage sqlTableLineage){
        if (Objects.isNull(sqlTableLineage)){
            return null;
        }
        TableLineage tableLineage = new TableLineage();
        tableLineage.setFromDb(sqlTableLineage.getFromDb());
        tableLineage.setFromSchema(sqlTableLineage.getFromDb());
        tableLineage.setFromTable(sqlTableLineage.getFromTable());
        tableLineage.setToDb(sqlTableLineage.getToDb());
        tableLineage.setToSchema(sqlTableLineage.getToDb());
        tableLineage.setToTable(sqlTableLineage.getToTable());
        return tableLineage;
    }

    public static LineageTableTable sqlTableLineage2DbTableLineage(com.dtstack.engine.sql.TableLineage sqlTableLineage, Map<String, LineageDataSetInfo> tableMap, LineageOriginType originType,String uniqueKey){
        String keyPref = "%s.%s";
        LineageTableTable tableTable = new LineageTableTable();
        LineageDataSetInfo inputTable = tableMap.get(String.format(keyPref,sqlTableLineage.getFromDb(),sqlTableLineage.getFromTable()));
        LineageDataSetInfo resultTable = tableMap.get(String.format(keyPref,sqlTableLineage.getToDb(),sqlTableLineage.getToTable()));
        tableTable.setInputTableId(inputTable.getId());
        tableTable.setInputTableKey(inputTable.getTableKey());
        tableTable.setResultTableId(resultTable.getId());
        tableTable.setResultTableKey(resultTable.getTableKey());
        tableTable.setLineageSource(originType.getType());
        tableTable.setUniqueKey(uniqueKey);
        return tableTable;
    }
}
