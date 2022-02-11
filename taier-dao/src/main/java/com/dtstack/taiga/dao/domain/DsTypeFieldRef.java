package com.dtstack.taiga.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 *
 * @description:
 * @author: liuxx
 * @date: 2021/3/11
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
