package com.dtstack.batch.web.datasource.vo.result;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author <a href="mailto:jiangyue@dtstack.com">江月 At 袋鼠云</a>.
 * @description
 * @date 2021/6/17 11:25 上午
 */
@Data
@ApiModel("获取所有可引入数据源接口的返回参数，已经引入的不会查询出来")
public class BatchDataSourceAllowImportResultVO {

    @ApiModelProperty(value = "数据源中心的id", example = "1")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源名称", example = "myData")
    private String dataName;

    @ApiModelProperty(value = "数据源类型", example = "1")
    private Integer type;

    @ApiModelProperty(value = "数据源版本")
    private String dataVersion;

    @ApiModelProperty(value = "是否开启Kerberos")
    private Boolean openKerberos;

    @ApiModelProperty(value = "数据源描述", example = "desc")
    private String dataDesc;

    @ApiModelProperty(value = "数据源连接url信息（json格式）")
    private String linkJson;

    @ApiModelProperty(value = "连接状态 0连接丢失，1连接可用", example = "1")
    private Integer status;

    @ApiModelProperty(value = "修改时间", example = "2020-12-29T11:39:13.000+00:00")
    private Date gmtModified;

}
