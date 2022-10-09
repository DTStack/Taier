package com.dtstack.taier.datasource.api.dto;

import lombok.Data;

import java.text.SimpleDateFormat;

/**
 * hdfs导入类
 *
 * @author ：wangchuan
 * date：Created in 上午10:33 2020/8/11
 * company: www.dtstack.com
 */
@Data
public class HDFSImportColumn {

    private String key;

    private String format;

    private SimpleDateFormat dateFormat;

}
