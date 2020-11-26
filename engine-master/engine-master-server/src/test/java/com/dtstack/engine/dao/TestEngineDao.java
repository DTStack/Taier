package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Engine;
import com.dtstack.sdk.core.feign.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;

/**
 * @author chener
 * @Classname TestEngineDao
 * @Description TODO
 * @Date 2020/11/24 19:58
 * @Created chener@dtstack.com
 */
public interface TestEngineDao {

    @Insert({"INSERT INTO console_engine(id,cluster_id,engine_name,engine_type,total_node,total_memory,total_core,sync_type)VALUES(#{id},#{clusterId},#{engineName},#{engineType},#{totalNode},#{totalMemory},#{totalCore},#{syncType})"})
    @Options()
    void insert(@Param("engine") Engine engine);
}
