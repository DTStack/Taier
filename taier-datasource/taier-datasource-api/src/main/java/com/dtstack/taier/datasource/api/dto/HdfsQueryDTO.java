package com.dtstack.taier.datasource.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * hdfs接口查询参数
 *
 * @author luming
 * @date 2022年04月24日
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HdfsQueryDTO {
    /**
     * 文件类型，支持orc,text,parquet
     */
    private String fileType;
    /**
     * hdfs文件/文件夹路径，绝对路径
     */
    private String hdfsPath;
    /**
     * 限制读取行数，实际数据行数超过此行数则抛出异常
     */
    private Integer limit;
    /**
     * text类型文件需提供分隔符
     */
    private String separator;
    /**
     * 当path为文件夹时，是否递归查询
     * default : false
     */
    private Boolean isRecursion;
    /**
     * 指定查询数据内列索引，可组合使用
     * required : true
     */
    private Integer colIndex;
    /**
     * 指定查询数据内行索引，可组合使用
     * required : false
     */
    private Integer rowIndex;
}
