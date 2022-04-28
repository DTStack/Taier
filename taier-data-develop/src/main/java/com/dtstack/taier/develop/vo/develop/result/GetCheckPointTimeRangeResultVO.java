package com.dtstack.taier.develop.vo.develop.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 *
 * @see TaskCheckpointTransfer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GetCheckPointTimeRangeResultVO {
    @ApiModelProperty(value = "开始时间", example = "1612340806000")
    private Long startTime;

    @ApiModelProperty(value = "结束时间", example = "1612340806000")
    private Long endTime;
}
