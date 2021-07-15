package com.dtstack.batch.web.table.vo.query;

import com.dtstack.sdk.core.common.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("数据导入信息")
public class BatchDataImportVO extends DtInsightAuthParam {

    @ApiModelProperty(value = "项目 ID", hidden = true)
    private Long projectId;

    @ApiModelProperty(value = "租户 ID", hidden = true)
    private Long tenantId;

    @ApiModelProperty(value = "uic 租户 ID", hidden = true)
    private Long dtuicTenantId;

    @ApiModelProperty(value = "用户 ID", hidden = true)
    private Long userId;

    @ApiModelProperty(value = "分隔符", example = ",", required = true)
    private String separator;

    @ApiModelProperty(value = "字符集", example = "utf8", required = true)
    private String oriCharset;

    @ApiModelProperty(value = "开始行", example = "1", required = true)
    private Integer startLine;

    @ApiModelProperty(value = "首行是否是标题", example = "true", required = true)
    private Boolean topLineIsTitle;

    @ApiModelProperty(value = "匹配类型", example = "1", required = true)
    private Integer matchType;

    @ApiModelProperty(value = "表id", example = "1", required = true)
    private Long tableId;

    @ApiModelProperty(value = "", example = "")
    private String partitions;

    @ApiModelProperty(value = "", example = "")
    private String keyRef;

    @ApiModelProperty(value = "是否覆盖", example = "1", required = true)
    private Integer overwriteFlag;

    @ApiModelProperty(value = "上传文件的临时文件夹", example = "/111/11", hidden = true)
    private String tmpPath;

    @ApiModelProperty(value = "上传文件的源文件名称", example = "源文件的名称", hidden = true)
    private String originalFilename;

}
