package com.dtstack.rdos.engine.execution.flink130.sink.stream;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ESinkType;
import com.dtstack.rdos.engine.execution.base.operator.stream.StreamCreateResultOperator;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.csv.RdosCsvSink;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.db.mysql.MysqlSink;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.elasticsearch.RdosElasticsearchSink;
import com.dtstack.rdos.engine.execution.flink130.sink.stream.kafka.RdosKafka09Sink;

import org.apache.flink.table.sinks.TableSink;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class StreamSinkFactory {

    public static TableSink getTableSink(StreamCreateResultOperator resultOperator){

        String resultType = resultOperator.getType();
        ESinkType sinkType = ESinkType.getSinkType(resultType);

        switch (sinkType){
            case MYSQL:
                return new MysqlSink().genStreamSink(resultOperator);

            case CSV://FIXME 未测试
                return new RdosCsvSink().genStreamSink(resultOperator);

            case KAFKA09://FIXME 未测试
                return new RdosKafka09Sink().genStreamSink(resultOperator);

            case ELASTIC5:
                return new RdosElasticsearchSink().genStreamSink(resultOperator);
        }

        throw new RdosException("not support sink type:" + resultType + "!!!");
    }


}
