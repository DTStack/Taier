package com.dtstack.taier.datasource.plugin.oracle;

/**
 * sql 常量
 *
 * @author ：wangchuan
 * date：Created in 下午7:47 2022/3/3
 * company: www.dtstack.com
 */
public interface SqlConstant {

    /**
     * 获取 oracle PDB 列表前 设置 session
     */
    String ALTER_PDB_SESSION = "ALTER SESSION SET CONTAINER=%s";

}
