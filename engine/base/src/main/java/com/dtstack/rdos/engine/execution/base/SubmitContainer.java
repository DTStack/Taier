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

    private ClientType clientType;

//    /**设置上传jar文件临时目录,如果未设置默认是/tmp/flinkjar*/
//    private String jarFileTmpPath;

    //计算资源
//    private int slots;

//    private String zkNamespace;
//
//    private String host;
//
//    private String jobManagerPort;

    private static SubmitContainer submitContainer;
    
    private Properties properties = new Properties();
    
    public static SubmitContainer createSubmitContainer(ClientType clientType,Map<String,Object> properties){
    	if(submitContainer!=null){
    		synchronized(SubmitContainer.class){
    			if(submitContainer!=null){
    				submitContainer = new SubmitContainer(clientType,properties);
    			}
    		}
    	}
    	return SubmitContainer.submitContainer;
    }
    
    private SubmitContainer(ClientType clientType,Map<String,Object> properties){
    	this.clientType = clientType;
    	this.properties.putAll(properties);
    	start();
    }
    
    
    public SubmitContainer getSubmitContainerInstance(){
    	return SubmitContainer.submitContainer;
    }
    

    public void start(){
        JobSubmitExecutor.getInstance().init(clientType,properties);
        JobSubmitExecutor.getInstance().start();

        logger.info("------start job container----");
        logger.info("client_type:{}", clientType);
        logger.info("------------------------------");
    }


    /**********************************************/
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }
    

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}
