package com.dtstack.rdos.engine.execution.flink130.source.batch;

import com.dtstack.rdos.commom.exception.RdosException;
import com.dtstack.rdos.engine.execution.base.enumeration.ESourceType;
import com.dtstack.rdos.engine.execution.flink130.source.stream.IBatchSourceGener;
import com.dtstack.rdos.engine.execution.flink130.source.stream.kafka.FlinkKafka09SourceGenr;

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
    public static IBatchSourceGener getBatchSourceGener(String sourceTypeStr){

        ESourceType sourceType = ESourceType.getSourceType(sourceTypeStr);

        switch (sourceType){
            case ELASTIC5:
                return new Elas();
        }

        throw new RdosException("not support for flink stream source type: " + sourceTypeStr);
    }

}
