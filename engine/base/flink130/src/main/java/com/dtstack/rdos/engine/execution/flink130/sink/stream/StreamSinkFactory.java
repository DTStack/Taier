package com.dtstack.rdos.engine.execution.flink130.sink.stream;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ESinkType;
import com.dtstack.rdos.engine.execution.base.operator.stream.BatchCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.csv.RdosCsvSink;
import com.dtstack.rdos.engine.execution.flink130.sink.db.mysql.MysqlSink;
import com.dtstack.rdos.engine.execution.flink130.sink.kafka.RdosKafka09Sink;

import org.apache.flink.table.sinks.TableSink;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class StreamSinkFactory {

    public static TableSink getTableSink(BatchCreateResultOperator resultOperator){

        String resultType = resultOperator.getType();
        ESinkType sinkType = ESinkType.getSinkType(resultType);

        switch (sinkType){
            case MYSQL:
                return new MysqlSink().genStreamSink(resultOperator);

            case CSV://FIXME 未测试
                return new RdosCsvSink().genStreamSink(resultOperator);

            case KAFKA09://FIXME 未测试
                return new RdosKafka09Sink().genStreamSink(resultOperator);
        }

        throw new RdosException("not support sink type:" + resultType + "!!!");
    }


}
