package com.dtstack.rdos.engine.execution.flink.sink;

import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;


/**
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public interface IStreamSinkGener<T> {

    T genStreamSink(CreateResultOperator resultOperator);
}
