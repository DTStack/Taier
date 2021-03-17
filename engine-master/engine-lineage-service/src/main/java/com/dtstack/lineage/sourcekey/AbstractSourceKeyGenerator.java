package com.dtstack.lineage.sourcekey;

/**
 * @Author: ZYD
 * Date: 2021/2/3 17:48
 * Description: sourceKey生成器
 * @since 1.0.0
 */
public abstract class AbstractSourceKeyGenerator {

    public abstract String generateSourceKey(String jdbcUrl,Integer sourceType);
}
