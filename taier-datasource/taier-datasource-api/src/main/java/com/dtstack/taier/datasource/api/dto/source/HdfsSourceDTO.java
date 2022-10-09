package com.dtstack.taier.datasource.api.dto.source;

import com.dtstack.taier.datasource.api.source.DataSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Map;

/**
 * @company: www.dtstack.com
 * @Author ：Nanqi
 * @Date ：Created in 17:51 2020/5/22
 * @Description：HDFS 数据源信息
 */
@Data
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class HdfsSourceDTO extends RdbmsSourceDTO {
    /**
     * Hadoop defaultFS
     */
    @Builder.Default
    private String defaultFS = "";

    /**
     * Hadoop/ Hbase 配置信息
     */
    private String config;

    /************************************临时处理 yarn 日志下载*********************************************/
    /**
     * appid 字符串
     */
    private String appIdStr;

    /**
     * 读取长度限制
     */
    @Builder.Default
    private Integer readLimit = 4095;

    /**
     * 日志下载角色
     */
    private String user;

    /**
     * 日志类型
     */
    private String logType = null;

    /**
     * yarn 配置
     */
    private Map<String, Object> yarnConf;

    /**
     * taskmanager id
     */
    private String containerId;


    @Override
    public Integer getSourceType() {
        return DataSourceType.HDFS.getVal();
    }
}
