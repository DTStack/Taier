package com.dtstack.rdos.engine.execution.flink.sink.hbase;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.table.sinks.TableSink;
import org.apache.flink.table.sinks.UpsertStreamTableSink;
import org.apache.flink.types.Row;

/**
 * Created by sishu.yss on 2017/5/23.
 */
public  abstract class HbaseSink implements UpsertStreamTableSink<Row> {

	@Override
	public TableSink<Tuple2<Boolean, Row>> configure(String[] arg0,
			TypeInformation<?>[] arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFieldNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeInformation<?>[] getFieldTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void emitDataStream(DataStream<Tuple2<Boolean, Row>> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public TupleTypeInfo<Tuple2<Boolean, Row>> getOutputType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TypeInformation<Row> getRecordType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setIsAppendOnly(Boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setKeyFields(String[] arg0) {
		// TODO Auto-generated method stub
		
	}

}
