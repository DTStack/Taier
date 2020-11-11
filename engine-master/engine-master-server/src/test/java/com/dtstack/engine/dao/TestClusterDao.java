package com.dtstack.engine.dao;

import com.dtstack.engine.api.domain.Cluster;
import com.dtstack.engine.api.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @Author tengzhen
 * @Description:
 * @Date: Created in 8:53 下午 2020/11/9
 */
public interface TestClusterDao {


    @Insert({" INSERT INTO console_cluster (cluster_name,hadoop_version)" +
            "        values(#{cluster.clusterName},#{cluster.hadoopVersion})"})
    @Options(useGeneratedKeys=true, keyProperty = "cluster.id", keyColumn = "id")
    void insert(@Param("cluster") Cluster cluster);

}
