package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.LineageTableTable;
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

    @Insert({" INSERT INTO lineage_table_table (dt_uic_tenant_id,app_type,input_table_id,input_table_key,result_table_id,result_table_key,table_lineage_key,lineage_source)" +
            " values " +
            "        (#{tableTable.dtUicTenantId},#{tableTable.appType},#{tableTable.inputTableId},#{tableTable.inputTableKey},#{tableTable.resultTableId},#{tableTable.resultTableKey},#{tableTable.tableLineageKey},#{tableTable.lineageSource})" +
            "        on duplicate key update gmt_modified=values(gmt_modified)"})
    @Options(useGeneratedKeys=true, keyProperty = "tableTable.id", keyColumn = "id")
    void insert(@Param("tableTable") LineageTableTable tableTable);
}
