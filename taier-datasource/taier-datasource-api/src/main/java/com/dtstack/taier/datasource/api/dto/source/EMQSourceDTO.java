package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * emq source dto
 *
 * @author ：wangchuan
 * date：Created in 上午10:28 2021/12/20
 * company: www.dtstack.com
 */
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EMQSourceDTO extends AbstractSourceDTO {
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
     * 是否缓存
     */
    @Builder.Default
    protected Boolean isCache = false;

    /**
     * 地址
     */
    protected String url;

    @Override
    public Integer getSourceType() {
        return DataSourceType.EMQ.getVal();
    }
}
