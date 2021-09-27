package com.dtstack.engine.dao;

import com.dtstack.engine.domain.LineageTableTable;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestLineageTableTableDao
 * @Description
 * @Date 2020/11/16 15:14
 * @Created chener@dtstack.com
 */
public interface TestLineageTableTableDao {

    @Insert({"INSERT INTO lineage_table_table(dt_uic_tenant_id,app_type,input_table_id,input_table_key,result_table_id,result_table_key,table_lineage_key,lineage_source)VALUES(#{lineageTableTable.dtUicTenantId},#{lineageTableTable.appType},#{lineageTableTable.inputTableId},#{lineageTableTable.inputTableKey},#{lineageTableTable.resultTableId},#{lineageTableTable.resultTableKey},#{lineageTableTable.tableLineageKey},#{lineageTableTable.lineageSource})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageTableTable.id", keyColumn = "id")
    Integer insert(@Param("lineageTableTable")LineageTableTable lineageTableTable);

}
