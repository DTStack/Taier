package com.dtstack.rdos.engine.execution.base.enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 输入源数据类型
 * Date: 2017/3/3
 * Company: www.dtstack.com
 *
 * @ahthor xuchao
 */
public enum ESourceType {

    KAFKA09;

    private static Logger logger = LoggerFactory.getLogger(ESourceType.class);

    public static ESourceType getSourceType(String sourceTypeStr){
        try{
            ESourceType sourceType =  ESourceType.valueOf(sourceTypeStr);
            return  sourceType;
        }catch (Exception e){
            logger.error("", e);
        }

        return null;
    }
}
