package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;

/**
 * 返回ftp 正则匹配的结果
 */
@Data
public class FtpRegexVO {

    /**
     * 查询出的 前20条
     */
    private List<String> fileNameList;

    /**
     * 匹配的条数  这里取巧  最多返回101 如果是101前端就展示超过100条
     */
    private Integer number;
}
