package com.dtstack.taier.develop.controller.develop;

import com.alibaba.fastjson.JSONArray;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.common.param.MetricResultVO;
import com.dtstack.taier.develop.dto.devlop.GetMetricValueVO;
import com.dtstack.taier.develop.dto.devlop.StreamTaskMetricDTO;
import com.dtstack.taier.develop.mapstruct.console.StreamJobMetricTransfer;
import com.dtstack.taier.develop.service.develop.impl.StreamJobMetricService;
import com.dtstack.taier.develop.vo.develop.query.GetTaskMetricsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhiChen
 * @date 2022/4/27 18:40
 */
@Api(value = "任务指标接口", tags = {"任务指标接口"})
@RestController
@RequestMapping(value = "/streamJobMetric")
public class DevelopStreamJobMetricController {

    @Autowired
    StreamJobMetricService streamJobMetricService;
    @ApiOperation(value = "获取任务指标")
    @PostMapping(value = "getTaskMetrics")
    public R<JSONArray> getTaskMetrics(@RequestBody  StreamTaskMetricDTO dto) {
        return new APITemplate<JSONArray>() {
            @Override
            protected JSONArray process()   {
                return streamJobMetricService.getTaskMetrics(dto);
            }
        }.execute();
    }

    @ApiOperation(value = "根据任务类型获取支持的任务指标 key")
    @GetMapping(value = "values")
    public R<List<String>> values(StreamTaskMetricDTO vo) {
        return new APITemplate<List<String>>() {
            @Override
            protected List<String> process()   {
                return streamJobMetricService.getMetricsByTaskType(vo.getTaskId());
            }
        }.execute();
    }
    @ApiOperation(value = "查询指定指标信息")
    @PostMapping(value = "queryTaskMetrics")
    public R<List<MetricResultVO>> queryTaskMetrics(@RequestBody GetMetricValueVO vo) {
        return new APITemplate<List<MetricResultVO>>() {
            @Override
            protected List<MetricResultVO> process()   {
                return streamJobMetricService.queryTaskMetrics(vo.getDtuicTenantId(), vo.getTaskId(), vo.getEnd(), vo.getTimespan(), vo.getChartName());
            }
        }.execute();
    }
}
