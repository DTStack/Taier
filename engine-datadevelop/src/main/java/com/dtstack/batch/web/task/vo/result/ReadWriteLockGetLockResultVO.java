package com.dtstack.batch.web.task.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("读写锁信息")
public class ReadWriteLockGetLockResultVO {

    @ApiModelProperty(value = "上一个持有锁的用户名", example = "5")
    private String lastKeepLockUserName;

    @ApiModelProperty(value = "检查结果", example = "0")
    private Integer result = 0;

    @ApiModelProperty(value = "是否持有锁", example = "false")
    private Boolean getLock = false;

    @ApiModelProperty(value = "锁名称")
    private String lockName;

    @ApiModelProperty(value = "修改的用户", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "乐观锁", example = "9")
    private Integer version;

    @ApiModelProperty(value = "项目 ID", example = "11")
    private Long projectId;

    @ApiModelProperty(value = "任务 ID", example = "3")
    private Long relationId;

    @ApiModelProperty(value = "任务类型", example = "1")
    private String type;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

}
