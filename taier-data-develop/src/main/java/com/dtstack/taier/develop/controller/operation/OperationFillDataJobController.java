package com.dtstack.taier.develop.controller.operation;

import com.dtstack.taier.common.lang.web.R;
import com.dtstack.taier.dao.pager.PageResult;
import com.dtstack.taier.develop.mapstruct.fill.FillDataJobMapstructTransfer;
import com.dtstack.taier.develop.service.schedule.JobService;
import com.dtstack.taier.develop.vo.fill.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:12 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/fill")
@Api(value = "/fill", tags = {"运维中心---补数据相关接口"})
public class OperationFillDataJobController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationFillDataJobController.class);

    @Autowired
    private JobService jobService;


    @RequestMapping(value = "/fillData", method = {RequestMethod.POST})
    @ApiOperation(value = "补数据接口:支持批量补数据和工程补数据")
    public R<Long> fillData(@RequestBody @Valid ScheduleFillJobParticipateVO scheduleFillJobParticipateVO) {
        return R.ok(jobService.fillData(FillDataJobMapstructTransfer.INSTANCE.scheduleFillJobParticipateVoToScheduleFillJobParticipateDTO(scheduleFillJobParticipateVO)));
    }

    @RequestMapping(value = "/queryFillDataList", method = {RequestMethod.POST})
    public R<PageResult<List<ReturnFillDataListVO>>> fillDataList(@RequestBody @Valid QueryFillDataListVO vo) {
        return R.ok(jobService.fillDataList(FillDataJobMapstructTransfer.INSTANCE.fillDataListVOToFillDataListDTO(vo)));
    }

    @RequestMapping(value = "/queryFillDataJobList", method = {RequestMethod.POST})
    public R<PageResult<ReturnFillDataJobListVO>> fillDataJobList(@RequestBody @Valid QueryFillDataJobListVO vo) {
        return R.ok(jobService.fillDataJobList(FillDataJobMapstructTransfer.INSTANCE.fillDataJobListVOToFillDataJobReturnListVO(vo)));
    }

}
