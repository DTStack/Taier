package com.dtstack.rdos.engine.execution.flink130.sink.hbase;

import com.dtstack.rdos.engine.execution.base.operator.stream.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.IStreamSinkGener;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public class RdosHbaseSink extends HbaseSink implements IStreamSinkGener<RdosHbaseSink>{

    @Override
    public RdosHbaseSink genStreamSink(BatchCreateResultOperator resultOperator) {
        return null;
    }
}
