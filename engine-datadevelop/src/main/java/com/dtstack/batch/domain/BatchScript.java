package com.dtstack.batch.domain;

import com.dtstack.engine.api.domain.TenantProjectEntity;
import lombok.Data;

/**
 * company: www.dtstack.com
 * author: toutian
 * create: 2017/11/9
 */
@Data
public class BatchScript extends TenantProjectEntity {

    /**
     * 脚本名称
     */
    private String name;

    /**
     * 脚本描述
     */
    private String scriptDesc;

    /**
     * 父文件夹id
     */
    private Long nodePid;

    /**
     * 创建者用户id
     */
    private Long createUserId;

    /**
     * 修改者用户id
     */
    private Long modifyUserId;

    /**
     * 脚本类型,0-sql,1-python,2-shell
     */
    private Integer type;

    /**
     * 脚本内容
     */
    private String scriptText;

    /**
     * 脚本环境参数
     */
    private String taskParams;

    /**
     * 脚本版本号
     */
    private Integer version;

}
