package com.dtstack.taier.datasource.api.dto;

import lombok.Builder;
import lombok.Data;

/**
 * job接口相关入参
 *
 * @author luming
 * @date 2022/2/25
 */
@Builder
@Data
public class JobParam {

    private String jobId;

    private Boolean retry;

    private String cubeName;

    /**
     * Supported build type: ‘BUILD’, ‘MERGE’, ‘REFRESH’
     */
    private BuildType buildType;

    private Long startTime;

    private Long endTime;

    private JobParam.RequestConfig requestConfig;

    private String sql;

    /**
     * http连接属性设置
     */
    @Builder
    @Data
    public static class RequestConfig {
        private Integer socketTimeout;
        private Integer connectTimeout;
        private Integer connectionRequestTimeout;
    }

    public enum BuildType {
        BUILD,
        MERGE,
        REFRESH;
    }
}
