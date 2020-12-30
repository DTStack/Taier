package com.dtstack.engine.api.service;

import com.dtstack.engine.api.dto.ClusterAlertResultDTO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.RequestLine;

import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2020/12/18 11:41 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface AlertApiClient extends DtInsightServer {

    /**
     * 查询通道列表
     *
     * @return
     */
    @RequestLine("POST /node/alert/list/show")
    ApiResponse<List<ClusterAlertResultDTO>> listShow();
}
