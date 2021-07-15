package com.dtstack.batch.web.script.vo.query;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("读写锁信息")
public class BatchReadWriteLockBaseVO {

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "上一个持有锁的用户名", example = "admin")
    private String lastKeepLockUserName;

    @ApiModelProperty(value = "检查结果", example = "0")
    private Integer result = 0;

    @ApiModelProperty(value = "是否持有锁", example = "false")
    private Boolean getLock = false;

    @ApiModelProperty(value = "锁名称", example = "锁")
    private String lockName;

    @ApiModelProperty(value = "修改的用户", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "乐观锁", example = "0")
    private Integer version;

    @ApiModelProperty(value = "任务Id", example = "0")
    private Long relationId;

    @ApiModelProperty(value = "任务类型", example = "sql")
    private String type;

    @ApiModelProperty(value = "脚本ID", example = "0")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;
}
