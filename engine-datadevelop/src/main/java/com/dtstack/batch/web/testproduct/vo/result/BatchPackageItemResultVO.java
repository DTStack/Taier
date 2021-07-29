package com.dtstack.batch.web.testproduct.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("发布包信息")
public class BatchPackageItemResultVO {

    @ApiModelProperty(value = "创建人", example = "admin")
    private String createUser;

    @ApiModelProperty(value = "修改人", example = "root")
    private String modifyUser;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp modifyTime;

    @ApiModelProperty(value = "ID", example = "1")
    private Long id;

    @ApiModelProperty(value = "租户 ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "13")
    private Long projectId;

    @ApiModelProperty(value = "发布包 ID", example = "54")
    private Long packageId;

    @ApiModelProperty(value = "选项 ID", example = "1")
    private Long itemId;

    @ApiModelProperty(value = "资源类型：0-任务，1-表，2-资源，3-函数", example = "1")
    private Integer itemType;

    @ApiModelProperty(value = "", example = "1")
    private Integer itemInnerType;

    @ApiModelProperty(value = "是否修改环境变量", example = "1")
    private String publishParam;

    @ApiModelProperty(value = "0 未发布 1发布失败 2发布完成", example = "1")
    private Integer status;

    @ApiModelProperty(value = "日志", example = "")
    private String log;

    @ApiModelProperty(value = "0 一键发布  1导入导出", example = "1")
    private Integer type;

    @ApiModelProperty(value = "冗余字段", example = "")
    private String itemName;

    @ApiModelProperty(value = "uic 租户 ID", example = "111")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "平台 ID", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

}
