package com.dtstack.rdos.engine.execution.flink130.sink.stream;

import com.dtstack.rdos.engine.execution.base.operator.stream.BatchCreateResultOperator;


/**
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public interface IStreamSinkGener<T> {

    T genStreamSink(BatchCreateResultOperator resultOperator);
}
