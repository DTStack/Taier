package com.dtstack.engine.datasource.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.engine.datasource.dao.po.BaseModel;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Data
@TableName("dsc_type_field_ref")
public class DsTypeFieldRef extends BaseModel<DsTypeFieldRef> {

    @TableField("data_type")
    private String dataType;

    @TableField("data_version")
    private String dataVersion;

    @TableField("form_field_id")
    private Long formFieldId;

}
