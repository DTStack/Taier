package com.dtstack.rdos.engine.execution.flink140.source.stream;

import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 自定义watermark---用于eventtime
 * Date: 2017/12/28
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class CustomerWaterMarkerForString extends BoundedOutOfOrdernessTimestampExtractor<Row> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerWaterMarkerForString.class);

    private static final long serialVersionUID = 1L;

    private int pos;

    private SimpleDateFormat sdf;

    private long lastTime = 0;

    public CustomerWaterMarkerForString(Time maxOutOfOrderness, int pos, String sdfStr) {
        super(maxOutOfOrderness);
        this.pos = pos;
        this.sdf = new SimpleDateFormat(sdfStr);
    }

    @Override
    public long extractTimestamp(Row row) {
        String dateStr = (String) row.getField(2);
        try {
            lastTime = sdf.parse(dateStr).getTime();
            return lastTime;
        } catch (ParseException e) {
            logger.error("", e);
        }
        return lastTime;
    }
}
