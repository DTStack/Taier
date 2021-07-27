package com.dtstack.engine.api.service;

import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.api.vo.template.TaskTemplateVO;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:21 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public interface TaskParamApiClient {

    /**
     *  获取指定任务类型的任务参数
     *  用户替换console的接口:/api/console/service/taskParam/getEngineParamTmplByComputeType
     *
     * @param param
     * @return
     */
    TaskTemplateResultVO getEngineParamTmplByComputeType(TaskTemplateVO param);
}
