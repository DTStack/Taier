package com.dtstack.engine.config;


/**
 *
 * @author maqi
 */
public interface IPluginConfigParser<T,R> {

    R parse(T t) throws Exception;
}
