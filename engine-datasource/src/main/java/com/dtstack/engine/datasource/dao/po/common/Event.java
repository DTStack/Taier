package com.dtstack.engine.datasource.dao.po.common;

import com.baomidou.mybatisplus.annotation.*;
import com.dtstack.engine.datasource.dao.BaseMapperField;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2021/5/10
 */
@Getter
@Setter
@TableName("cm_event")
public class Event implements Serializable {
    private static final long serialVersionUID = -2345198871908446105L;
    @TableId(value = BaseMapperField.COLUMN_ID, type = IdType.AUTO)
    private Long id;
    /**
     * 1离线开发 2数据质量 3数据服务 4智能标签 5数据地图 6控制台 7实时开发 8算法开发 9数据资产 10指标平台
     *
     * @see com.dtstack.dtcenter.common.enums.AppType
     */
    @TableField("app_type")
    private Integer appType;
    /**
     * 事件编码
     * ("DM_RELEASE", "发布模型"),
     * ("DM_OFFLINE", "下线模型"),
     * ("DM_DELETE", "删除模型");
     */
    @TableField("event_code")
    private String eventCode;
    /**
     * 回调地址（最长256个字符）
     */
    @TableField("callback_url")
    private String callbackUrl;
    /**
     * 是否激活
     */
    @TableField("is_active")
    private boolean active;
    /**
     * 备注（最长64个字符）
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = BaseMapperField.COLUMN_CREATE_AT, fill = FieldFill.INSERT)
    private Date gmtCreate;
    /**
     * 更新时间
     */
    @TableField(value = BaseMapperField.COLUMN_UPDATE_AT, fill = FieldFill.UPDATE)
    private Date gmtModified;
}
