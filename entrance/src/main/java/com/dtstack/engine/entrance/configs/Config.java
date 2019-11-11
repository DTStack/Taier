package com.dtstack.engine.entrance.configs;



/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年02月23日 下午1:25:40
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public interface Config {

	public <T> T parse(String filename, Class<T> classType)throws Exception;
	
}
