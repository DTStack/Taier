package com.dtstack.taier.datasource.api.dto.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:06 2020/5/22
 * @Description：关系型数据库 DTO
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RdbmsSourceDTO extends AbstractSourceDTO {
    /**
     * 用户名
     */
    protected String username;

    /**
     * 密码
     */
    protected String password;

    /**
     * 数据源类型
     */
    protected Integer sourceType;

    /**
     * 地址
     */
    protected String url;

    /**
     * 库
     */
    private String schema;

    /**
     * JDBC 自定义参数, json 格式
     */
    private String properties;
}
