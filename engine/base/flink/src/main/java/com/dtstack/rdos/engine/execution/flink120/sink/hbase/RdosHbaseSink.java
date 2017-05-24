package com.dtstack.rdos.engine.execution.flink120.sink.hbase;

import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.IStreamSinkGener;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public class RdosHbaseSink extends HbaseSink implements IStreamSinkGener<RdosHbaseSink>{

    @Override
    public RdosHbaseSink genStreamSink(CreateResultOperator resultOperator) {
        return null;
    }
}
