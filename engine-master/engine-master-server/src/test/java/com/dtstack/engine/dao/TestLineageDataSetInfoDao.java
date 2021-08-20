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

    @Insert({"INSERT INTO lineage_data_set_info(dt_uic_tenant_id,app_type,source_id,real_source_id,source_name,source_type,source_key,set_type,db_name,schema_name,table_name,table_key,is_manual)VALUES(#{lineageDataSetInfo.dtUicTenantId},#{lineageDataSetInfo.appType},#{lineageDataSetInfo.sourceId},#{lineageDataSetInfo.realSourceId},#{lineageDataSetInfo.sourceName},#{lineageDataSetInfo.sourceType},#{lineageDataSetInfo.sourceKey},#{lineageDataSetInfo.setType},#{lineageDataSetInfo.dbName},#{lineageDataSetInfo.schemaName},#{lineageDataSetInfo.tableName},#{lineageDataSetInfo.tableKey},#{lineageDataSetInfo.isManual})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageDataSetInfo.id", keyColumn = "id")
    Integer insert(@Param("lineageDataSetInfo") LineageDataSetInfo lineageDataSetInfo);

}
