package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageDataSource;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestLineageDataSourceDao
 * @Description TODO
 * @Date 2020/11/27 11:09
 * @Created chener@dtstack.com
 */
public interface TestLineageDataSourceDao {

    @Insert({"INSERT INTO lineage_data_source(real_source_id,source_key,source_name,app_type,source_type,data_json,kerberos_conf,open_kerberos,app_source_id,inner_source,component_id)VALUES(#{lineageDataSource.realSourceId},#{lineageDataSource.sourceKey},#{lineageDataSource.sourceName},#{lineageDataSource.appType},#{lineageDataSource.sourceType},#{lineageDataSource.dataJson},#{lineageDataSource.kerberosConf},#{lineageDataSource.openKerberos},#{lineageDataSource.appSourceId},#{lineageDataSource.innerSource},#{lineageDataSource.componentId})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageDataSource.id", keyColumn = "id")
    Integer insert(@Param("lineageDataSource") LineageDataSource lineageDataSource);

}
