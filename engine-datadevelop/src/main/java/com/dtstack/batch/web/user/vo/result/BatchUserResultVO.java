package com.dtstack.batch.web.user.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("用户信息")
public class BatchUserResultVO {

    @ApiModelProperty(value = "用户姓名", example = "dtstack")
    private String userName;

    @ApiModelProperty(value = "电话号码", example = "135xxxxx892")
    private String phoneNumber;

    @ApiModelProperty(value = "uic 用户 ID", example = "32")
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "xxx@163.com")
    private String email;

    @ApiModelProperty(value = "状态", example = "0")
    private Integer status;

    @ApiModelProperty(value = "默认项目ID", example = "3")
    private Long defaultProjectId;

    @ApiModelProperty(value = "rootUser", example = "1")
    private Integer rootUser;

    @ApiModelProperty(value = "uic 租户 ID", example = "13")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台类别", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "租户ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目ID", example = "1")
    private Long projectId;

    @ApiModelProperty(value = "是否删除", example = "dtstack")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

}
