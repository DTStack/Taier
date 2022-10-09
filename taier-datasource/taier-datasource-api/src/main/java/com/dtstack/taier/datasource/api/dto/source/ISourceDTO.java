package com.dtstack.taier.datasource.api.dto.source;

import java.sql.Connection;

/**
 * 数据源信息接口
 *
 * @author ：nanqi
 * date：Created in 下午7:47 2020/5/22
 * company: www.dtstack.com
 */
public interface ISourceDTO {

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    String getUsername();

    /**
     * 获取密码
     *
     * @return 密码
     */
    String getPassword();

    /**
     * 获取数据源类型
     *
     * @return 数据源类型
     */
    Integer getSourceType();

    /**
     * 获取连接
     *
     * @return jdbc connection
     */
    Connection getConnection();

    /**
     * 设置 jdbc connection
     */
    void setConnection(Connection connection);
}
