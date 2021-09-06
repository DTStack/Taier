package com.dtstack.batch.web.datasource.vo.query;

import com.dtstack.engine.common.param.DtInsightAuthParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.sql.Timestamp;

@Data
@ApiModel("数据源用户信息")
public class BatchDataSourceUserVO extends DtInsightAuthParam {
    @ApiModelProperty(value = "用户名称", example = "admin", required = true)
    private String userName;

    @ApiModelProperty(value = "电话号码", example = "18888888888", required = true)
    private String phoneNumber;

    @ApiModelProperty(value = "dtuic用户id", hidden = true)
    private Long dtuicUserId;

    @ApiModelProperty(value = "邮箱", example = "test@dtstack.com", required = true)
    private String email;

    @ApiModelProperty(value = "状态", example = "1", required = true)
    private Integer status;

    @ApiModelProperty(value = "默认项目id", hidden = true)
    private Long defaultProjectId;

    @ApiModelProperty(value = "整库同步id", example = "1", required = true)
    private Long id = 0L;

    @ApiModelProperty(value = "创建时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtCreate;

    @ApiModelProperty(value = "修改时间", example = "2020-08-14 14:41:55", required = true)
    private Timestamp gmtModified;

    @ApiModelProperty(value = "是否删除", example = "1", required = true)
    private Integer isDeleted = 0;
}
