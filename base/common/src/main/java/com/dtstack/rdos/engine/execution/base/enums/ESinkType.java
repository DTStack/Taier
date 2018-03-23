package com.dtstack.rdos.engine.execution.base.enums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reason:
 * Date: 2017/3/8
 * Company: www.dtstack.com
 * @ahthor xuchao
 */
public enum ESinkType {
    MYSQL, CSV, ELASTIC5, KAFKA09;

    private static final Logger logger = LoggerFactory.getLogger(ESinkType.class);

    public static ESinkType getSinkType(String type){
        type = type.toUpperCase();
        try{
            ESinkType sinkType = ESinkType.valueOf(type);
            return sinkType;
        }catch (Exception e){
            logger.error("", e);
        }

        return null;
    }
}
