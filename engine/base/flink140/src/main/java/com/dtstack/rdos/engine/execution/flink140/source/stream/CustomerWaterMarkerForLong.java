package com.dtstack.rdos.engine.execution.flink140.source.stream;

import com.dtstack.rdos.common.util.MathUtil;
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

public class CustomerWaterMarkerForLong extends BoundedOutOfOrdernessTimestampExtractor<Row> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerWaterMarkerForLong.class);

    private static final long serialVersionUID = 1L;

    private int pos;

    private long lastTime = 0;

    public CustomerWaterMarkerForLong(Time maxOutOfOrderness, int pos) {
        super(maxOutOfOrderness);
        this.pos = pos;
    }

    @Override
    public long extractTimestamp(Row row) {

        try{
            Long eveTime = MathUtil.getLongVal(row.getField(2));
            lastTime = eveTime;
            return eveTime;
        }catch (Exception e){
            logger.error("", e);
        }

        return lastTime;
    }
}
