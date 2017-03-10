package com.dtstack.rdos.engine.execution.flink120.sink;

import com.dtstack.rdos.engine.execution.base.operator.CreateResultOperator;

/**
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public interface IStreamSinkGener<T> {

    T genStreamSink(CreateResultOperator resultOperator);
}
