package com.dtstack.rdos.engine.execution.flink130.sink.batch;


import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateResultOperator;

/**
 * batch 生成sink接口
 * Date: 2017/3/10
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public interface IBatchSinkGener<T> {

    T genBatchSink(BatchCreateResultOperator resultOperator);
}
