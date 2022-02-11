package com.dtstack.taiga.develop.vo.datasource;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 *
 *
 * @author 全阅
 * @Description:
 * @Date: 2021/3/9 11:54
 */
@Data
@ApiModel("数据源基本信息")
public class DsDetailVO {

    @ApiModelProperty("数据源id")
    private Long dataInfoId;

    @ApiModelProperty(value = "数据源类型", example = "Mysql")
    private String dataType;

    @ApiModelProperty("数据源报表")
    private String dataVersion;

//    @ApiModelProperty("数据源类型")
//    private String dataTypeName;

    @ApiModelProperty("数据源名称")
    private String dataName;

    @ApiModelProperty("数据源描述")
    private String dataDesc;

    @ApiModelProperty(value = "数据源信息", notes = "数据源填写的表单信息, 保存为json, key键要与表单的name相同")
    private String dataJson;

    @ApiModelProperty(value = "0普通，1默认数据源，2从控制台添加的")
    private String isMeta;

}
