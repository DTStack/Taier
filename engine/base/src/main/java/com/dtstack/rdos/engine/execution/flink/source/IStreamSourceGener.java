package com.dtstack.rdos.engine.execution.flink.source;

import java.util.Properties;

/**
 * 生成input 数据源
 * Date: 2017/2/20
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public interface IStreamSourceGener<T> {

    /**
     * 获取输入源
     * @param prop
     * @param fieldNames
     * @param fieldTypes
     * @return
     */
    T genStreamSource(Properties prop, String[] fieldNames, Class[] fieldTypes);
}
