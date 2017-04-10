package com.dtstack.rdos.engine.execution.flink120.sink.csv;

import com.dtstack.rdos.engine.execution.base.operator.stream.CreateResultOperator;
import com.dtstack.rdos.engine.execution.flink120.sink.IStreamSinkGener;

import org.apache.flink.table.sinks.CsvTableSink;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class RdosCsvSink implements IStreamSinkGener<CsvTableSink>{

    public RdosCsvSink(){
    }

    @Override
    public CsvTableSink genStreamSink(CreateResultOperator resultOperator) {
        String csvPath = resultOperator.getProperties().getProperty("csvPath");
        String csvDelim = resultOperator.getProperties().getProperty("csvDelim", "|");
        return  new CsvTableSink(csvPath, csvDelim);
    }
}
