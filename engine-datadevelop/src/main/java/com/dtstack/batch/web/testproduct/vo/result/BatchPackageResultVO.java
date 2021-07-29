package com.dtstack.batch.web.testproduct.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@ApiModel("发布包信息")
public class BatchPackageResultVO {

    @ApiModelProperty(value = "")
    private String applyUser;

    @ApiModelProperty(value = "发布人 ID", example = "3")
    private String publishUser;

    @ApiModelProperty(value = "发布包信息")
    private List<BatchPackageItemResultVO> items;

    @ApiModelProperty(value = "是否绑定", example = "false")
    private Boolean isBinding;

    @ApiModelProperty(value = "发布包名", example = "ad23sdd4a-1adw2d5g")
    private String name;

    @ApiModelProperty(value = "备注", example = "打包测试")
    private String comment;

    @ApiModelProperty(value = "创建人 ID", example = "1")
    private Long createUserId;

    @ApiModelProperty(value = "发布人 ID", example = "1")
    private Long publishUserId;

    @ApiModelProperty(value = "发布状态 ：0-待发布，1-成功，2-失败", example = "0")
    private Integer status;

    @ApiModelProperty(value = "日志")
    private String log;

    @ApiModelProperty(value = "包类型 导出0 导入1", example = "0")
    private Integer packageType;

    @ApiModelProperty(value = "导入的压缩包的path", example = "/tmp/zip/")
    private String path;

    @ApiModelProperty(value = "判断zip的导入时间是否过期", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp pathTime;

    @ApiModelProperty(value = "uic 租户 ID", example = "111")
    private Long dtuicTenantId;

    @ApiModelProperty(value = "租户 ID", example = "3")
    private Long tenantId;

    @ApiModelProperty(value = "项目 ID", example = "13")
    private Long projectId;

    @ApiModelProperty(value = "平台 ID", example = "1")
    private Integer appType;

    @ApiModelProperty(value = "创建时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "0")
    private Integer isDeleted = 0;

    @ApiModelProperty(value = "ID", example = "32")
    private Long id = 0L;

}
