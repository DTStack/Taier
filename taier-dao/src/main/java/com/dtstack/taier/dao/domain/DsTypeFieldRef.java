package com.dtstack.taier.dao.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @description:
 * @author: liuxx
 * @date: 2021/3/11
 */
@TableName("dsc_type_field_ref")
public class DsTypeFieldRef extends BaseModel<DsTypeFieldRef> {

    @TableField("data_type")
    private String dataType;

    @TableField("data_version")
    private String dataVersion;

    @TableField("form_field_id")
    private Long formFieldId;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Long getFormFieldId() {
        return formFieldId;
    }

    public void setFormFieldId(Long formFieldId) {
        this.formFieldId = formFieldId;
    }
}
