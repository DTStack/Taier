package com.dtstack.engine.common.util;

import com.dtstack.engine.common.exception.RdosDefineException;

import java.util.Objects;

/**
 * @author chener
 * @Classname ValidateUtil
 * @Description 参数校验工具
 * @Date 2020/11/19 13:59
 * @Created chener@dtstack.com
 */
public class ValidateUtil {

    public static void validateNotNull(Object object,String info){
        if (Objects.isNull(object)){
            throw new RdosDefineException(info);
        }
    }
}
