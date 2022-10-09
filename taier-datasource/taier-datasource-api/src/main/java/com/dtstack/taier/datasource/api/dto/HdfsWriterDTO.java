package com.dtstack.taier.datasource.api.dto;

import lombok.Data;

import java.util.List;

/**
 * 写入hdfs的参数列表
 *
 * @author ：wangchuan
 * date：Created in 下午2:38 2020/8/11
 * company: www.dtstack.com
 */
@Data
public class HdfsWriterDTO {

    private String hdfsDirPath;

    private String fromLineDelimiter;

    private String toLineDelimiter;

    private String fromFileName;

    private String oriCharSet;

    private Integer startLine;

    private Boolean topLineIsTitle;

    private List<ColumnMetaDTO> columnsList;

    private List<HDFSImportColumn> keyList;

    private String fileFormat;

    /**
     * 是否设置默认值
     */
    private Boolean setDefault;

}
