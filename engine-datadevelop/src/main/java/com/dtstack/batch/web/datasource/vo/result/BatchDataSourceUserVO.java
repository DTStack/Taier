package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("数据源用户信息")
public class BatchDataSourceUserVO {
    @ApiModelProperty(value = "用户名称", example = "admin")
    private String userName;

    @ApiModelProperty(value = "电话号码", example = "18888888888")
    private String phoneNumber;

    @ApiModelProperty(value = "dtuic用户id")
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "test@dtstack.com")
    private String email;

    @ApiModelProperty(value = "状态", example = "1")
    private Integer status;

    @ApiModelProperty(value = "默认项目id")
    private Long defaultProjectId;

    @ApiModelProperty(value = "整库同步id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1")
    private Integer isDeleted = 0;
}