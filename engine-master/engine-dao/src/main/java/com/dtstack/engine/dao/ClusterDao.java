package com.dtstack.engine.dao;

import com.dtstack.engine.domain.Cluster;
import com.dtstack.engine.dto.ClusterDTO;
import com.dtstack.engine.common.pager.PageQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ClusterDao {

    Integer generalCount(@Param("model") ClusterDTO clusterDTO);

    List<Cluster> generalQuery(PageQuery<ClusterDTO> pageQuery);

    Integer insert(Cluster cluster);

    Integer insertWithId(Cluster cluster);

    Cluster getByClusterName(@Param("clusterName") String clusterName);

    Cluster getOne(@Param("id") Long clusterId);

    List<Cluster> listAll();

    Integer updateHadoopVersion(@Param("id") Long clusterId, @Param("hadoopVersion") String hadoopVersion);

    void deleteCluster(Long clusterId);

    void updateGmtModified(Long clusterId);
}
