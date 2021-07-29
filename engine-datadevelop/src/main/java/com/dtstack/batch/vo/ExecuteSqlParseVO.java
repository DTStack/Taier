package com.dtstack.batch.vo;

import lombok.Data;

import java.util.List;


/**
 * @author yunliu
 * @date 2020-04-27 09:24
 * @description
 */
@Data
public class ExecuteSqlParseVO {


    private String msg;

    private Integer status;


    /**
     * 发送到引擎生成的jobid
     */
    private String  jobId;

    private Integer engineType;

    private String sqlText;

    /**
     * sql结果id对应的集合
     */
    private List<SqlResultVO> sqlIdList;

}
