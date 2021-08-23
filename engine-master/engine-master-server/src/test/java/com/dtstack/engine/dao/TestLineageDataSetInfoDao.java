package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageDataSetInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
/**
 * @author chener
 * @Classname TestLineageDataSetInfoDao
 * @Description TODO
 * @Date 2021/1/25 13:48
 * @Created chener@dtstack.com
 */
public interface TestLineageDataSetInfoDao {

    @Insert({"INSERT INTO lineage_data_set_info(dt_uic_tenant_id,app_type,source_name,source_type,set_type,db_name,schema_name,table_name,table_key,is_manual,data_info_id)VALUES(#{lineageDataSetInfo.dtUicTenantId},#{lineageDataSetInfo.appType},#{lineageDataSetInfo.sourceName},#{lineageDataSetInfo.sourceType},#{lineageDataSetInfo.setType},#{lineageDataSetInfo.dbName},#{lineageDataSetInfo.schemaName},#{lineageDataSetInfo.tableName},#{lineageDataSetInfo.tableKey},#{lineageDataSetInfo.isManual},#{lineageDataSetInfo.dataInfoId})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageDataSetInfo.id", keyColumn = "id")
    Integer insert(@Param("lineageDataSetInfo") LineageDataSetInfo lineageDataSetInfo);

}
