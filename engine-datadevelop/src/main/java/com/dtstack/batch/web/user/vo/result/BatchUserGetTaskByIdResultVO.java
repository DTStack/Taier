package com.dtstack.batch.web.user.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("根据任务id详情返回信息")
public class BatchUserGetTaskByIdResultVO {

    @ApiModelProperty(value = "用户 ID", example = "0")
    private Long id = 0L;

    @ApiModelProperty(value = "用户名称", example = "admin")
    private String userName;

    @ApiModelProperty(value = "手机号", example = "110")
    private String phoneNumber;

    @ApiModelProperty(value = "UIC用户 ID", example = "1L")
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "1208686186@qq.com")
    private String email;

    @ApiModelProperty(value = "用户状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "默认项目 ID", example = "1L")
    private Long defaultProjectId;
}
