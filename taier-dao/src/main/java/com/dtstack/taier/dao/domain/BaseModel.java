package com.dtstack.taier.dao.domain;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.dtstack.taier.common.constant.MP;

import java.util.Date;

/**
 * Created by 袋鼠云-数栈产研部-应用研发中心.
 *
 * @author <a href="mailto:linfeng@dtstack.com">林丰</a>
 * @date 2021/3/4
 * @desc
 */
public class BaseModel <T extends Model<?>> extends Model<T> implements ID<Long>{
    // id\is_deleted\gmt_create\gmt_modified\creator\modifier

    @TableId(value = MP.COLUMN_ID, type = IdType.AUTO)
    private Long id;
    /**
     * 创建人ID
     */
    @TableField(value = MP.COLUMN_CREATE_BY, fill = FieldFill.INSERT)
    private Long createUserId;
    /**
     * 更新人ID
     */
    @TableField(value = MP.COLUMN_UPDATE_BY, fill = FieldFill.UPDATE)
    private Long modifyUserId;
    /**
     * 逻辑删除标志位
     */
    @TableLogic(value = "0", delval = "1")
    @TableField(MP.COLUMN_DELETED)
    private boolean isDeleted;
    /**
     * 创建时间
     */
    @TableField(value = MP.COLUMN_CREATE_AT, fill = FieldFill.INSERT)
    private Date gmtCreate;
    /**
     * 更新时间
     */
    @TableField(value = MP.COLUMN_UPDATE_AT, fill = FieldFill.UPDATE)
    private Date gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Long createUserId) {
        this.createUserId = createUserId;
    }

    public Long getModifyUserId() {
        return modifyUserId;
    }

    public void setModifyUserId(Long modifyUserId) {
        this.modifyUserId = modifyUserId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }


}
