package com.dtstack.engine.dao;

import com.dtstack.engine.domain.KerberosConfig;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestKerberosConfigDao
 * @Description TODO
 * @Date 2020/11/25 20:29
 * @Created chener@dtstack.com
 */
public interface TestKerberosConfigDao {

    @Insert({"INSERT INTO console_kerberos(cluster_id,name,open_kerberos,remote_path,principal,component_type,krb_name)VALUES(#{kerberosConfig.clusterId},#{kerberosConfig.name},#{kerberosConfig.openKerberos},#{kerberosConfig.remotePath},#{kerberosConfig.principal},#{kerberosConfig.componentType},#{kerberosConfig.krbName})"})
    @Options(useGeneratedKeys=true, keyProperty = "kerberosConfig.id", keyColumn = "id")
    Integer insert(@Param("kerberosConfig") KerberosConfig kerberosConfig);

    @Select({"select * from console_kerberos_config limit 1"})
    KerberosConfig getOne();
}
