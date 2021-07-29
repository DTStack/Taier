package com.dtstack.batch.web.function.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("函数用户信息")
public class BatchFunctionUserVO {
    @ApiModelProperty(value = "用户 ID")
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

    @ApiModelProperty(value = "创建时间", example = "2020-12-30 11:42:14")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-30 11:42:14")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}