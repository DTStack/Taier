package com.dtstack.engine.datasource.vo.datasource.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@ApiModel("数据表单模版视图类")
public class DsFormTemplateVo implements Serializable {

    @ApiModelProperty(value = "数据源类型", notes = "具体查看com.dtstack.pubsvc.common.enums.datasource.DsType枚举类")
    private String dataType;

    @ApiModelProperty("数据源版本 可为空")
    private String dataVersion;

    @ApiModelProperty("模版表单属性详情列表")
    private List<DsFormFieldVo> fromFieldVoList;


}
