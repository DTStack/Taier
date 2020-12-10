package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Cluster;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/**
 * @author chener
 * @Classname TestClusterDao
 * @Description TODO
 * @Date 2020/11/24 20:22
 * @Created chener@dtstack.com
 */
public interface TestClusterDao {

    @Insert({"INSERT INTO console_cluster(id,cluster_name,hadoop_version)VALUES(#{cluster.id},#{cluster.clusterName},#{cluster.hadoopVersion})"})
    @Options()
    Integer insert(@Param("cluster") Cluster cluster);

    @Select({"select * from console_cluster limit 1"})
    Cluster getOne();
}
