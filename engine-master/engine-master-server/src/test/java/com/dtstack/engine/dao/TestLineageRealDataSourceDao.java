package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.LineageRealDataSource;
import com.dtstack.engine.master.anno.DatabaseInsertOperation;
import com.dtstack.engine.master.utils.Template;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @author chener
 * @Classname TestLineageRealDataSourceDao
 * @Description TODO
 * @Date 2020/11/27 10:59
 * @Created chener@dtstack.com
 */
public interface TestLineageRealDataSourceDao {

    @Insert({"INSERT INTO lineage_real_data_source(source_name,source_key,source_type,data_json,kerberos_conf,open_kerberos)VALUES(#{lineageRealDataSource.sourceName},#{lineageRealDataSource.sourceKey},#{lineageRealDataSource.sourceType},#{lineageRealDataSource.dataJson},#{lineageRealDataSource.kerberosConf},#{lineageRealDataSource.openKerberos})"})
    @Options(useGeneratedKeys=true, keyProperty = "lineageRealDataSource.id", keyColumn = "id")
    Integer insert(@Param("lineageRealDataSource") LineageRealDataSource lineageRealDataSource);

}
