package com.dtstack.engine.master.controller;

import com.dtstack.engine.master.callback.ApiResult;
import com.dtstack.engine.master.impl.ActionService;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/node/action")
@SuppressWarnings("unchecked")
public class ActionController {

    @Autowired
    ActionService actionService;

    @RequestMapping("/listJobStatusByJobIds")
    @ResponseBody
    public String getListJobStatusByJobIds(@RequestBody Map<String, Object> paramMap) {
        List<String> jobIds = (List<String>)MapUtils.getObject(paramMap, "jobIds");
        return ApiResult.getApiResultString(() -> actionService.listJobStatusByJobIds(jobIds),
                "/listJobStatusByJobIds", paramMap);
    }

    @RequestMapping("/start")
    @ResponseBody
    public String start(@RequestBody Map<String, Object> paramMap) {
        return ApiResult.getApiResultString(() -> actionService.start(paramMap),
                "/start", paramMap);
    }

    @RequestMapping("/stop")
    @ResponseBody
    public String stop(@RequestBody Map<String, Object> paramMap) {
        return ApiResult.getApiResultString(() -> {actionService.stop(paramMap); return null;},
                "/stop", paramMap);
    }

    @RequestMapping("/status")
    @ResponseBody
    public String status(@RequestBody Map<String, Object> paramMap) {
        String jobId = MapUtils.getString(paramMap, "jobId");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.status(jobId, computeType),
                "/status", paramMap);
    }

    @RequestMapping("/statusByJobIds")
    @ResponseBody
    public String statusByJobIds(@RequestBody Map<String, Object> paramMap) {
        List<String> jobIds = (List<String>)MapUtils.getObject(paramMap, "jobIds");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.statusByJobIds(jobIds, computeType),
                "/statusByJobIds", paramMap);
    }

    @RequestMapping("/startTime")
    @ResponseBody
    public String startTime(@RequestBody Map<String, Object> paramMap) {
        String jobId = MapUtils.getString(paramMap, "jobId");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.startTime(jobId, computeType),
                "/startTime", paramMap);
    }

    @RequestMapping("/log")
    @ResponseBody
    public String log(@RequestBody Map<String, Object> paramMap) {
        String jobId = MapUtils.getString(paramMap, "jobId");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.log(jobId, computeType),
                "/log", paramMap);
    }

    @RequestMapping("/retryLog")
    @ResponseBody
    public String retryLog(@RequestBody Map<String, Object> paramMap) {
        String jobId = MapUtils.getString(paramMap, "jobId");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.retryLog(jobId, computeType),
                "/retryLog", paramMap);
    }

    @RequestMapping("/retryLogDetail")
    @ResponseBody
    public String retryLogDetail(@RequestBody Map<String, Object> paramMap) {
        String jobId = MapUtils.getString(paramMap, "jobId");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        Integer retryNum = MapUtils.getInteger(paramMap, "retryNum");
        return ApiResult.getApiResultString(() -> actionService.retryLogDetail(jobId, computeType, retryNum),
                "/retryLogDetail", paramMap);
    }

    @RequestMapping("/entitys")
    @ResponseBody
    public String entitys(@RequestBody Map<String, Object> paramMap) {
        List<String> jobIds = (List<String>)MapUtils.getObject(paramMap, "jobIds");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.entitys(jobIds, computeType),
                "/entitys", paramMap);
    }

    @RequestMapping("/containerInfos")
    @ResponseBody
    public String containerInfos(@RequestBody Map<String, Object> paramMap) {
        return ApiResult.getApiResultString(() -> actionService.containerInfos(paramMap),
                "/containerInfos", paramMap);
    }

    @RequestMapping("/resetTaskStatus")
    @ResponseBody
    public String resetTaskStatus(@RequestBody Map<String, Object> paramMap) {
        String jobId = MapUtils.getString(paramMap, "jobId");
        Integer computeType = MapUtils.getInteger(paramMap, "computeType");
        return ApiResult.getApiResultString(() -> actionService.resetTaskStatus(jobId, computeType),
                "/resetTaskStatus", paramMap);
    }

    @RequestMapping("/listJobStatus")
    @ResponseBody
    public String listJobStatus(@RequestBody Map<String, Object> paramMap) {
        Long time = MapUtils.getLong(paramMap, "time");
        return ApiResult.getApiResultString(() -> actionService.listJobStatus(time),
                "/listJobStatus", paramMap);
    }

}
