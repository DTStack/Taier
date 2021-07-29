package com.dtstack.batch.web.script.vo.query;

import com.dtstack.batch.web.user.vo.query.BatchUserBaseVO;
import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("脚本添加更新信息")
public class BatchScriptAddOrUpdateVO extends DtInsightAuthParam {
    @ApiModelProperty(hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(hidden = true)
    private Long tenantId;

    @ApiModelProperty(hidden = true)
    private Long projectId;

    @ApiModelProperty(hidden = true)
    private Long userId;

    @ApiModelProperty(value = "创建用户", example = "{}")
    private BatchUserBaseVO createUser;

    @ApiModelProperty(value = "修改用户", example = "{}")
    private BatchUserBaseVO modifyUser;

    @ApiModelProperty(value = "读写锁", example = "{}")
    private BatchReadWriteLockBaseVO readWriteLockVO;

    @ApiModelProperty(value = "锁版本", example = "0")
    private Integer lockVersion = 0;

    @ApiModelProperty(value = "强制更新", example = "false")
    private Boolean forceUpdate = false;

    @ApiModelProperty(value = "脚本名称", example = "脚本名称")
    private String name;

    @ApiModelProperty(value = "脚本描述", example = "脚本描述")
    private String scriptDesc;

    @ApiModelProperty(value = "父文件夹ID", example = "1")
    private Long nodePid;

    @ApiModelProperty(value = "创建者用户ID", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "脚本类型,0-sql,1-python,2-shell", example = "1")
    private Integer type;

    @ApiModelProperty(value = "脚本内容", example = "1")
    private String scriptText;

    @ApiModelProperty(value = "脚本环境参数", example = "##")
    private String taskParams;

    @ApiModelProperty(value = "脚本版本号", example = "1")
    private Integer version;

    @ApiModelProperty(value = "app类型", example = "RDOS")
    private Integer appType;

    @ApiModelProperty(value = "修改者用户ID", example = "1")
    private Long modifyUserId;

    @ApiModelProperty(value = "脚本ID", example = "0")
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-11-04 14:48:05")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

}

