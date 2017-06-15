package com.dtstack.rdos.engine.execution.flink.source;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ESourceType;
import com.dtstack.rdos.engine.execution.flink.source.kafka.FlinkKafka09SourceGenr;

/**
 * Reason:
 * Date: 2017/3/10
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */

public class SourceFactory {

    /**
     * 根据指定的类型构造数据源
     * 当前只支持kafka09
     * @param sourceTypeStr
     * @return
     */
    public static IStreamSourceGener getStreamSourceGener(String sourceTypeStr){

        ESourceType sourceType = ESourceType.getSourceType(sourceTypeStr);

        switch (sourceType){
            case KAFKA09:
                return new FlinkKafka09SourceGenr();
        }

        throw new RdosException("not support for flink stream source type: " + sourceTypeStr);
    }

}
