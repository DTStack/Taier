package com.dtstack.engine.dao;

import com.dtstack.engine.domain.LineageColumnColumn;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 *
 * @author basion
 * @Classname TestLineageColumnColumnDao
 * @Description unit test for LineageColumnColumn
 * @Date 2021-01-25 12:00:34
 * @Created basion
 */
public interface TestLineageColumnColumnDao {

    @Insert({"INSERT INTO lineage_column_column(dt_uic_tenant_id,app_type,input_table_key,input_table_id,input_column_name,result_table_key,result_table_id,result_column_name,column_lineage_key,lineage_source)VALUES(#{lineageColumnColumn.dtUicTenantId},#{lineageColumnColumn.appType},#{lineageColumnColumn.inputTableKey},#{lineageColumnColumn.inputTableId},#{lineageColumnColumn.inputColumnName},#{lineageColumnColumn.resultTableKey},#{lineageColumnColumn.resultTableId},#{lineageColumnColumn.resultColumnName},#{lineageColumnColumn.columnLineageKey},#{lineageColumnColumn.lineageSource})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageColumnColumn.id", keyColumn = "id")
    Integer insert(@Param("lineageColumnColumn") LineageColumnColumn lineageColumnColumn);

}

