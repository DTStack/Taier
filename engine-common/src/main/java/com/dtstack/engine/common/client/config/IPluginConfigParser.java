package com.dtstack.engine.common.client.config;


/**
 *
 * @author maqi
 */
public interface IPluginConfigParser<T,R> {

    R parse(T t) throws Exception;
}
