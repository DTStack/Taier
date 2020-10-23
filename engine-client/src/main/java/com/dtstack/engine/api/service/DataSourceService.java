package com.dtstack.engine.api.service;

import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;

/**
 * @author chener
 * @Classname DataSourceService
 * @Description 数据源接口
 * @Date 2020/10/22 20:43
 * @Created chener@dtstack.com
 */
public interface DataSourceService extends DtInsightServer {

    /**
     * 增加或更新数据源
     * @param engineSourceId 引擎数据源id
     * @param dataJson 数据源配置json
     * @param kerberosConf kerberos配置，没有开启为null
     * @param dataSourceName 数据源名称
     * @param dataSourceType 数据源类型
     * @return 数据源id
     */
    ApiResponse<Long> addOrUpdateDataSource(Integer appType,Long engineSourceId,String dataJson,String kerberosConf,String dataSourceName,Integer dataSourceType);

    /**
     * 删除数据源
     * @return
     */
    ApiResponse deleteDataSource(Integer appType,Long engineSourceId);
}
