package com.dtstack.engine.base.config;

import java.io.FileNotFoundException;

/**
 *
 * @author maqi
 */
public interface IPluginConfigParser<T,R> {

    R parse(T t) throws Exception;
}
