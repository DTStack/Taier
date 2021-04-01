package com.dtstack.engine.master.controller;

import com.dtstack.engine.api.pojo.ParamAction;
import com.dtstack.engine.api.pojo.ParamActionExt;
import com.dtstack.engine.api.pojo.ParamTaskAction;
import com.dtstack.engine.api.vo.action.ActionJobEntityVO;
import com.dtstack.engine.api.vo.action.ActionJobStatusVO;
import com.dtstack.engine.api.vo.action.ActionLogVO;
import com.dtstack.engine.api.vo.action.ActionRetryLogVO;
import com.dtstack.engine.master.impl.ActionService;
import com.dtstack.engine.master.router.DtRequestParam;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/node/action")
@Api(value = "/node/action", tags = {"任务动作接口"})
public class ActionController {

    @Autowired
    private ActionService actionService;

    @RequestMapping(value="/listJobStatusByJobIds", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态、执行时间等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobIds",value="查询的所有job的jobId值",required=true, dataType = "String", allowMultiple = true)
    })
    public List<ActionJobStatusVO> listJobStatusByJobIds(@DtRequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.listJobStatusByJobIds(jobIds);
    }

    @RequestMapping(value="/start", method = {RequestMethod.POST})
    @ApiOperation(value = "开始任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name="paramActionExt",value="请求开始的任务的相关信息及集群信息",required=true, paramType="body", dataType = "ParamActionExt")
    })
    public Boolean start(@RequestBody ParamActionExt paramActionExt) {
        return actionService.start(paramActionExt);
    }

    @RequestMapping(value="/startJob", method = {RequestMethod.POST})
    @ApiOperation(value = "立即执行任务")
    public Boolean startJob(@RequestBody ParamTaskAction paramTaskAction) {
        return actionService.startJob(paramTaskAction.getBatchTask(),paramTaskAction.getJobId(),paramTaskAction.getFlowJobId());
    }

    @RequestMapping(value="/paramActionExt", method = {RequestMethod.POST})
    @ApiOperation(value = "提交前预处理接口")
    public ParamActionExt paramActionExt(@RequestBody ParamTaskAction paramTaskAction) throws Exception {
        return actionService.paramActionExt(paramTaskAction.getBatchTask(),paramTaskAction.getJobId(),paramTaskAction.getFlowJobId());
    }

    @RequestMapping(value="/stop", method = {RequestMethod.POST})
    @ApiOperation(value = "停止任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobIds",value="请求停止任务的相关信息，jobIds",required=true, paramType="body")
    })
    public Boolean stop(@DtRequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.stop(jobIds);
    }

    @RequestMapping(value="/forceStop", method = {RequestMethod.POST})
    @ApiOperation(value = "停止任务")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobIds",value="请求停止任务的相关信息，jobIds",required=true, paramType="body")
    })
    public Boolean stop(@DtRequestParam(value = "jobIds") List<String> jobIds, @DtRequestParam("isForce") Integer isForce) throws Exception {
        return actionService.stop(jobIds, isForce);
    }

    @RequestMapping(value="/status", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="查询的job的jobId值",required=true, dataType = "String"),
    })
    public Integer status(@DtRequestParam("jobId") String jobId) throws Exception{
        return actionService.status(jobId);
    }

    @RequestMapping(value="/statusByJobIds", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobIds",value="查询的所有job的jobId值",required=true, dataType = "String", allowMultiple = true),
    })
    public Map<String, Integer> statusByJobIds(@DtRequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.statusByJobIds(jobIds);
    }

    @RequestMapping(value="/startTime", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job开始运行的时间", notes = "返回值为毫秒级时间戳")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="查询的job的jobId值",required=true, dataType = "String")
    })
    public Long startTime(@DtRequestParam("jobId") String jobId) throws Exception {
        return actionService.startTime(jobId);
    }

    @RequestMapping(value="/log", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="查询的job的jobId值",required=true, dataType = "String"),
            @ApiImplicitParam(name="computeType",value="查询的job的computeType值",required=true, dataType = "int")
    })
    public ActionLogVO log(@DtRequestParam("jobId") String jobId, @DtRequestParam("computeType") Integer computeType) throws Exception {
        return actionService.log(jobId, computeType);
    }

    @RequestMapping(value="/logFromEs", method = {RequestMethod.POST})
    @ApiOperation(value = "K8s调度下，查询单个Job的log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="查询的job的jobId值",required=true, dataType = "String")
    })
    public String logFromEs(@DtRequestParam("jobId") String jobId) throws Exception {
        return actionService.logFromEs(jobId);
    }

    @RequestMapping(value="/retryLog", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的重试log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="查询的job的jobId值",required=true, dataType = "String"),
    })
    public List<ActionRetryLogVO> retryLog(@DtRequestParam("jobId") String jobId) throws Exception {
        return actionService.retryLog(jobId);
    }

    @RequestMapping(value="/retryLogDetail", method = {RequestMethod.POST})
    @ApiOperation(value = "查询单个Job的详细重试log日志")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="查询的job的jobId值",required=true, dataType = "String"),
            @ApiImplicitParam(name="retryNum",value="查询的job的retryNum值",required=true, dataType = "int")
    })
    public ActionRetryLogVO retryLogDetail(@DtRequestParam("jobId") String jobId,@DtRequestParam("retryNum") Integer retryNum) throws Exception {
        return actionService.retryLogDetail(jobId, retryNum);
    }

    @RequestMapping(value="/entitys", method = {RequestMethod.POST})
    @ApiOperation(value = "查询多个Job的状态、相关日志等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobIds",value="查询的所有job的jobId值",required=true, dataType = "String", allowMultiple = true),
    })
    public List<ActionJobEntityVO> entitys(@DtRequestParam(value = "jobIds") List<String> jobIds) throws Exception {
        return actionService.entitys(jobIds);
    }

    @RequestMapping(value="/containerInfos", method = {RequestMethod.POST})
    @ApiOperation(value = "查询容器信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="paramAction",value="jobId、计算类型等信息",required=true, paramType="body", dataType = "ParamAction")
    })
    public List<String> containerInfos(@RequestBody ParamAction paramAction) throws Exception {
        return actionService.containerInfos(paramAction);
    }

    @RequestMapping(value="/resetTaskStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "重置任务状态为未提交")
    @ApiImplicitParams({
            @ApiImplicitParam(name="jobId",value="重置的job的jobId值",required=true, dataType = "String")
    })
    public String resetTaskStatus(@DtRequestParam("jobId") String jobId) {
        return actionService.resetTaskStatus(jobId);
    }

    @RequestMapping(value="/listJobStatus", method = {RequestMethod.POST})
    @ApiOperation(value = "查询某个时间开始的Job的状态、执行时间等信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="time",value="查询的job的调整的时间点",required=true, dataType = "long")
    })
    public List<ActionJobStatusVO> listJobStatus(@DtRequestParam("time") Long time) {
        return actionService.listJobStatus(time);
    }

    @RequestMapping(value="/generateUniqueSign", method = {RequestMethod.POST, RequestMethod.GET})
    public String generateUniqueSign() {
        return actionService.generateUniqueSign();
    }
}
