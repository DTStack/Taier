package com.dtstack.engine.base.config;


/**
 *
 * @author maqi
 */
public interface IPluginConfigParser<T,R> {

    R parse(T t) throws Exception;
}
