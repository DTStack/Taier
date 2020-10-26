package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/26 10:11
 * @Description:对应于: {@link org.apache.calcite.sql.SqlCall}
 * 非叶子结点。是一个sql operator的调用。
 *
 * 通常来讲，每一个非叶子结点都应当是一个call（select，insert这种由于其需要的方法太多，
 * 将其单独作为了Node，而非继承Call）。call和operator基本上描述了一个node
 *
 * call是对一系列操作数的调用
 */
public abstract class BaseCall extends Node {
    public BaseCall(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }
}
