package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.api.vo.template.TaskTemplateVO;
import com.dtstack.sdk.core.common.ApiResponse;
import com.dtstack.sdk.core.common.DtInsightServer;
import com.dtstack.sdk.core.feign.Headers;
import com.dtstack.sdk.core.feign.RequestLine;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:21 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface TaskParamService extends DtInsightServer {

    /**
     *  获取指定任务类型的任务参数
     *  用户替换console的接口:/api/console/service/taskParam/getEngineParamTmplByComputeType
     *
     * @param param
     * @return
     */
    @RequestLine("POST /node/taskParam/getEngineParamTmplByComputeType")
    @Headers(value={"Content-Type: application/json"})
    ApiResponse<TaskTemplateResultVO> getEngineParamTmplByComputeType(TaskTemplateVO param);
}
