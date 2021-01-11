package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.vo.template.TaskTemplateResultVO;
import com.dtstack.engine.api.vo.template.TaskTemplateVO;
import com.dtstack.engine.master.impl.TaskParamTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Auther: dazhi
 * @Date: 2020/9/29 4:28 下午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@Api(tags = {"任务参数"})
@RestController
@RequestMapping("/node")
public class TaskParamController {

    @Autowired
    private TaskParamTemplateService taskParamTemplateService;

    @ApiOperation("获取指定任务类型的任务参数 \n 用户替换console的接口:/api/console/service/taskParam/getEngineParamTmplByComputeType")
    @PostMapping("/taskParam/getEngineParamTmplByComputeType")
    public TaskTemplateResultVO getEngineParamTmplByComputeType(@RequestBody TaskTemplateVO param) {
        return taskParamTemplateService.getEngineParamTmplByComputeType(param.getEngineType(), param.getComputeType(), param.getTaskType());
    }

}
