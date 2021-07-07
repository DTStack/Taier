package com.dtstack.batch.web.task.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("表基本信息")
public class BatchModelTableVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "是否root用户", hidden = true)
    private Boolean isRoot;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "token", hidden = true)
    private String dtToken;

    @ApiModelProperty(value = "是否admin用户", hidden = true)
    private Boolean isAdmin;

    @ApiModelProperty(value = "是否删除标识 0 正常 1 删除", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "当前页", example = "1", required = true)
    private Integer currentPage;

    @ApiModelProperty(value = "页面展示条数", example = "10", required = true)
    private Integer pageSize;

    @ApiModelProperty(value = "1 层级 2 主题域 3 刷新频率 4 增量定义", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "层级编号", example = "2", required = true)
    private Integer level;

    @ApiModelProperty(value = "表名", example = "dev")
    private String name;

    @ApiModelProperty(value = "说明", example = "表备注")
    private String modelDesc;

    @ApiModelProperty(value = "前缀标识", example = "dev")
    private String prefix;

    @ApiModelProperty(value = "生命周期  单位：天", example = "99")
    private Integer lifeDay;

    @ApiModelProperty(value = "是否层级依赖 0:不依赖, 1:依赖", example = "0")
    private Integer depend;

    @ApiModelProperty(value = "最近修改人id", example = "1", hidden = true)
    private Long modifyUserId;

    @ApiModelProperty(value = "创建者用户id", example = "1", hidden = true)
    private Long createUserId;

    @ApiModelProperty(value = "平台类型", example = "", hidden = true)
    private Integer appType;

    @ApiModelProperty(value = "主键id", example = "1")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "多个主键id", required = true)
    private List<Long> ids;

    @ApiModelProperty(value = "数据表id", example = "3", required = true)
    private Long tableId;

}
