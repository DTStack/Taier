package com.dtstack.rdos.engine.execution.flink120.sink.csv;

import com.dtstack.rdos.engine.execution.base.operator.CreateResultOperator;
import org.apache.flink.table.sinks.CsvTableSink;
import org.apache.flink.table.sinks.TableSink;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class RdosCsvSink extends CsvTableSink {

    public RdosCsvSink(CreateResultOperator resultOperator){
        super(resultOperator.getProperties().getProperty("csvPath"),
                resultOperator.getProperties().getProperty("csvDelim", "|"));
    }
}
