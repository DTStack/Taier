package com.dtstack.taier.develop.controller.develop;

import com.alibaba.fastjson.JSONArray;
import com.dtstack.taier.common.lang.coc.APITemplate;
import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.develop.dto.devlop.StreamTaskMetricDTO;
import com.dtstack.taier.develop.mapstruct.console.StreamJobMetricTransfer;
import com.dtstack.taier.develop.service.develop.impl.StreamJobMetricService;
import com.dtstack.taier.develop.vo.develop.query.GetTaskMetricsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public R<JSONArray> getTaskMetrics(@RequestBody @Validated GetTaskMetricsVO vo) {
        return new APITemplate<JSONArray>() {
            @Override
            protected JSONArray process()   {
                StreamTaskMetricDTO metricDTO = StreamJobMetricTransfer.INSTANCE.getTaskMetrics(vo);
                return streamJobMetricService.getTaskMetrics(metricDTO);
            }
        }.execute();
    }
}
