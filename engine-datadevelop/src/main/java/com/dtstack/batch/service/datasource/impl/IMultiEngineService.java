package com.dtstack.batch.service.datasource.impl;

import com.dtstack.batch.service.multiengine.EngineInfo;
import com.dtstack.dtcenter.common.enums.EJobType;
import com.dtstack.dtcenter.common.enums.EScriptType;
import com.dtstack.dtcenter.loader.source.DataSourceType;

import java.util.List;

/**
 * Reason:
 * Date: 2019/5/30
 * Company: www.dtstack.com
 * @author xuchao
 */

public interface IMultiEngineService {

    /**
     * 获取支持的引擎信息
     * @param dtuicTenantId
     * @return
     */
    List<Integer> getTenantSupportMultiEngine(Long dtuicTenantId);

    /**
     * 从console获取Hadoop的meta数据源
     * @param dtuicTenantId
     * @return
     */
    DataSourceType getTenantSupportHadoopMetaDataSource(Long dtuicTenantId);

    List<EJobType> getTenantSupportJobType(Long dtuicTenantId, Long projectId);

    EngineInfo getEnginePluginInfo(Long dtuicTenantId, Integer type, Long  ProjectId);

    List<EScriptType> getTenantSupportScriptType(Long dtuicTenantId, Long projectId);
}
