package com.dtstack.batch.web.testproduct.vo.query;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("发布包信息")
public class BatchPackageItemInfoVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "发布包 ID", example = "54", required = true)
    private Long packageId;

    @ApiModelProperty(value = "选项 ID", example = "1")
    private Long itemId;

    @ApiModelProperty(value = "资源类型：0-任务，1-表，2-资源，3-函数", example = "1", required = true)
    private Integer itemType;

    @ApiModelProperty(value = "", example = "1", required = true)
    private Integer itemInnerType;

    @ApiModelProperty(value = "是否修改环境变量", example = "1", required = true)
    private String publishParam;

    @ApiModelProperty(value = "0 未发布 1发布失败 2发布完成", example = "1")
    private Integer status;

    @ApiModelProperty(value = "日志", example = "")
    private String log;

    @ApiModelProperty(value = "0 一键发布  1导入导出", example = "1", required = true)
    private Integer type;

    @ApiModelProperty(value = "冗余字段", example = "")
    private String itemName;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台 ID", hidden = true)
    private Integer appType;

    @ApiModelProperty(value = "创建时间", hidden = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", hidden = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", hidden = true)
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "数据")
    private JSONObject data;

    @ApiModelProperty(value = "发布参数", example = "{\"updateEnvParam\": false}")
    private JSONObject publishParamJson;

}
