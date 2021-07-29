package com.dtstack.batch.web.user.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("返回用户信息")
public class BatchUserBaseResultVO {

    @ApiModelProperty(value = "用户姓名", example = "ruomu")
    private String userName;

    @ApiModelProperty(value = "电话号码", example = "110")
    private String phoneNumber;

    @ApiModelProperty(value = "uic 用户 ID", example = "32")
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "zhangsan@dtstack.com")
    private String email;

    @ApiModelProperty(value = "状态", example = "0")
    private Integer status;

    @ApiModelProperty(value = "默认项目ID", example = "3")
    private Long defaultProjectId;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-28 09:22:03")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-28 09:22:03")
    private Timestamp gmtModified;
}
