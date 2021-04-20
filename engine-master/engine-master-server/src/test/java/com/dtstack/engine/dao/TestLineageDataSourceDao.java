package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageDataSource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestLineageDataSourceDao
 * @Description TODO
 * @Date 2020/11/27 11:09
 * @Created chener@dtstack.com
 */
public interface TestLineageDataSourceDao {

    @Insert({"INSERT INTO lineage_data_source(dt_uic_tenant_id,real_source_id,source_id,project_id,schema_name,source_key,source_name,app_type,source_type,data_json,kerberos_conf,open_kerberos,app_source_id,inner_source,component_id)VALUES(#{lineageDataSource.dtUicTenantId},#{lineageDataSource.realSourceId},#{lineageDataSource.sourceId},#{lineageDataSource.projectId},#{lineageDataSource.schemaName},#{lineageDataSource.sourceKey},#{lineageDataSource.sourceName},#{lineageDataSource.appType},#{lineageDataSource.sourceType},#{lineageDataSource.dataJson},#{lineageDataSource.kerberosConf},#{lineageDataSource.openKerberos},#{lineageDataSource.appSourceId},#{lineageDataSource.innerSource},#{lineageDataSource.componentId})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageDataSource.id", keyColumn = "id")
    Integer insert(@Param("lineageDataSource")LineageDataSource lineageDataSource);

    @Select({"select * from lineage_data_source limit 1"})
    LineageDataSource getOne();
}
