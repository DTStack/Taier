package com.dtstack.batch.controller.operation;

import com.dtstack.batch.mapstruct.fill.FillDataJobMapstructTransfer;
import com.dtstack.batch.service.schedule.JobService;
import com.dtstack.batch.vo.fill.*;
import com.dtstack.engine.common.exception.RdosDefineException;
import com.dtstack.engine.pager.PageResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * @Auther: dazhi
 * @Date: 2021/12/7 3:12 PM
 * @Email:dazhi@dtstack.com
 * @Description:
 */
@RestController
@RequestMapping("/node/fill")
public class OperationFillDataJobController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperationFillDataJobController.class);

    @Autowired
    private JobService jobService;


    @RequestMapping(value = "/fillData", method = {RequestMethod.POST})
    @ApiOperation(value = "补数据接口:支持批量补数据和工程补数据")
    public Long fillData(@RequestBody @Valid ScheduleFillJobParticipateVO scheduleFillJobParticipateVO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            LOGGER.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            throw new RdosDefineException(bindingResult.getFieldError().getDefaultMessage());
        }
        return jobService.fillData(FillDataJobMapstructTransfer.INSTANCE.scheduleFillJobParticipateVoToScheduleFillJobParticipateDTO(scheduleFillJobParticipateVO));
    }

    @RequestMapping(value = "/fillDataList", method = {RequestMethod.POST})
    public PageResult<List<FillDataReturnListVO>> fillDataList(@RequestBody @Valid FillDataListVO vo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            LOGGER.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            throw new RdosDefineException(bindingResult.getFieldError().getDefaultMessage());
        }
        return jobService.fillDataList(FillDataJobMapstructTransfer.INSTANCE.fillDataListVOToFillDataListDTO(vo));
    }

    @RequestMapping(value = "/fillDataJobList", method = {RequestMethod.POST})
    public PageResult<FillDataJobReturnListVO> fillDataJobList(@RequestBody @Valid FillDataJobListVO vo, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            LOGGER.error(Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage());
            throw new RdosDefineException(bindingResult.getFieldError().getDefaultMessage());
        }
        return jobService.fillDataJobList(FillDataJobMapstructTransfer.INSTANCE.fillDataJobListVOToFillDataJobReturnListVO(vo));
    }

}
