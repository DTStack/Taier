package com.dtstack.batch.web.user.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("用户信息")
public class BatchUserBaseVO {

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "用户名称", example = "admin", required = true)
    private String userName;

    @ApiModelProperty(value = "手机号", example = "110", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "UIC用户 ID", example = "1L", required = true)
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "1208686186@qq.com", required = true)
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
