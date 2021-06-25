package com.dtstack.engine.lineage.impl;

import com.alibaba.fastjson.JSON;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.dao.ComponentConfigDao;
import com.dtstack.engine.dao.ComponentDao;
import com.dtstack.engine.dao.TenantDao;
import com.dtstack.pubsvc.sdk.datasource.DataSourceAPIClient;
import com.dtstack.pubsvc.sdk.dto.param.datasource.DsListParam;
import com.dtstack.pubsvc.sdk.dto.result.datasource.DsServiceInfoDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.*;

/**
 * @author chener
 * @Classname LineageDataSourceService
 * @Description 数据源service
 * @Date 2020/10/23 14:43
 * @Created chener@dtstack.com
 */
@Service
public class LineageDataSourceService {
    private static final Logger logger = LoggerFactory.getLogger(LineageDataSourceService.class);


    @Resource
    private TenantDao tenantDao;

    @Resource
    private ComponentDao componentDao;

    @Resource
    private ComponentConfigDao componentConfigDao;

    @Autowired
    private DataSourceAPIClient dataSourceAPIClient;


    /**
     * @param sourceId: 数据源id
     * @param appType:  应用类型
     * @author zyd
     * @Description 根据sourceId和appType查询数据源信息
     * @Date 2020/10/30 4:03 下午
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    public DsServiceInfoDTO getDataSourceByIdAndAppType(Long sourceId, Integer appType) {

        //资产通过数据源中心id查询数据源
        ApiResponse<DsServiceInfoDTO> dsInfoById = dataSourceAPIClient.getDsInfoById(sourceId);
        if(dsInfoById.getCode() != 1){
            logger.error("getDsInfoById query failed,param:{}",JSON.toJSONString(sourceId));
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
        }
        DsServiceInfoDTO dsServiceInfoDTO = dsInfoById.getData();
        if(null == dsServiceInfoDTO){
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口未查到相应的数据源");
        }
        return dsServiceInfoDTO;
    }


    /**
     * @param ids:
     * @author zyd
     * @Description 根据id列表批量查询逻辑数据源信息
     * @Date 2020/10/30 2:25 下午
     * @return: com.dtstack.engine.api.domain.LineageDataSource
     **/
    public List<DsServiceInfoDTO> getDataSourcesByIdList(List<Long> ids) {

        if (CollectionUtils.isEmpty(ids)) {
            throw new RdosDefineException("数据源id列表不能为空");
        }
        DsListParam dsListParam = new DsListParam();
        dsListParam.setDataInfoIdList(ids);
        ApiResponse<List<DsServiceInfoDTO>> dsInfoListByIdList = dataSourceAPIClient.getDsInfoListByIdList(dsListParam);
        if(dsInfoListByIdList.getCode() != 1){
            logger.error("getDsInfoListByIdList query failed,param:{}",JSON.toJSONString(ids));
            throw new RdosDefineException("调用数据源中心根据id查询数据源接口失败");
        }
        if(dsInfoListByIdList.getData().size()==0){
            logger.error("getDsInfoListByIdList query result is empty,param:{}",JSON.toJSONString(ids));
            throw new RdosDefineException("调用数据源中心根据ids查询数据源接口未查到数据源");
        }
        return dsInfoListByIdList.getData();
    }








}
