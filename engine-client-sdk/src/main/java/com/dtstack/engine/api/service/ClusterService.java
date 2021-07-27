package com.dtstack.engine.api.service;

import com.dtstack.engine.api.dto.ClusterDTO;
import com.dtstack.engine.api.pager.PageResult;
import com.dtstack.engine.api.vo.ClusterVO;



import java.util.List;

public interface ClusterService {

    /**
     * 对外接口
     */
    String clusterInfo(Long tenantId);

}
