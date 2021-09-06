package com.dtstack.batch.web.task.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("读写锁信息")
public class BatchReadWriteLockBaseVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "上一个持有锁的用户名", example = "1")
    private String lastKeepLockUserName;

    @ApiModelProperty(value = "检查结果", example = "1")
    private Integer result = 0;

    @ApiModelProperty(value = "是否持有锁", hidden = true)
    private Boolean isGetLock = false;

    @ApiModelProperty(value = "锁名称", hidden = true)
    private String lockName;

    @ApiModelProperty(value = "修改的用户", hidden = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "乐观锁", example = "1")
    private Integer version;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "任务 ID", example = "1", required = true)
    private Long relationId;

    @ApiModelProperty(value = "任务类型", example = "1", required = true)
    private String type;

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

}
