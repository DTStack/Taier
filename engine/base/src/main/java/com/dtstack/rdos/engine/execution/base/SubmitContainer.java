package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SubmitContainer {

    private static final Logger logger = LoggerFactory.getLogger(SubmitContainer.class);

    private static SubmitContainer submitContainer;
    
    private Map<String, Object> engineConf;
    
    public static SubmitContainer createSubmitContainer(Map<String,Object> properties){
    	if(submitContainer == null){
    		synchronized(SubmitContainer.class){
    			if(submitContainer == null){
    				submitContainer = new SubmitContainer(properties);
    			}
    		}
    	}
    	return SubmitContainer.submitContainer;
    }
    
    private SubmitContainer(Map<String,Object> engineConf){
    	this.engineConf = engineConf;
    	start();
    }
    
    
    public SubmitContainer getSubmitContainerInstance(){
    	return SubmitContainer.submitContainer;
    }
    

    public void start(){
        JobSubmitExecutor.getInstance().init(engineConf);
        JobSubmitExecutor.getInstance().start();

        logger.info("------start job container----");
        logger.info("engine config:{}", engineConf);
        logger.info("------------------------------");
    }

    public void shutdown(){
        JobSubmitExecutor.getInstance().shutdown();
    }


    /**********************************************/

	public void release() {
		this.shutdown();
	}
}
