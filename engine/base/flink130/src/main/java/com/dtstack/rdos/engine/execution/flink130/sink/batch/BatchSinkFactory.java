package com.dtstack.rdos.engine.execution.flink130.sink.batch;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ESinkType;
import com.dtstack.rdos.engine.execution.base.operator.batch.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.batch.elasticsearch.Elastic5BatchTableSink;
import org.apache.flink.table.sinks.TableSink;

/**
 * 获得table sink
 * Date: 2017/7/25
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class BatchSinkFactory {

    public static TableSink getTableSink(BatchCreateResultOperator resultOperator){

        String resultType = resultOperator.getType();
        ESinkType sinkType = ESinkType.getSinkType(resultType);

        switch (sinkType){
            case ELASTIC5:
                return new Elastic5BatchTableSink().genBatchSink(resultOperator);
        }

        throw new RdosException("not support sink type:" + resultType + "!!!");
    }
}
