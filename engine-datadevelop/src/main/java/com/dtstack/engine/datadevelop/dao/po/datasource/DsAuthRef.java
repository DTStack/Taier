package com.dtstack.pubsvc.dao.po.datasource;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.dtstack.pubsvc.dao.po.BaseModel;
import lombok.Data;

/**
 * @author 全阅
 * @Description: 数据源与授权产品关联
 * @Date: 2021/3/10
 */
@Data
@TableName("dsc_auth_ref")
public class DsAuthRef extends BaseModel<DsAuthRef> {

    /**
     * 数据源实例主键id {@link DsInfo}
     */
    @TableField("data_info_id")
    private Long dataInfoId;

    /**
     * 产品唯一编码 {@link DsAppList}
     */
    @TableField("app_type")
    private Integer appType;

}
