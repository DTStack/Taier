package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    /**设置上传jar文件临时目录,如果未设置默认是/tmp/flinkjar*/
    private String jarFileTmpPath;

    //计算资源
    private int slots;

    private String zkNamespace;

    private String host;

    private String jobManagerPort;

    private static SubmitContainer submitContainer;
    
    public static SubmitContainer createSubmitContainer(ClientType clientType,String zkNamespace,String executionEngineUrl,String jarFileTmpPath,int slots){
    	if(submitContainer!=null){
    		synchronized(SubmitContainer.class){
    			if(submitContainer!=null){
    				String[] uports = executionEngineUrl.split(":");
    				submitContainer = new SubmitContainer(clientType,zkNamespace,uports[0],uports[1],jarFileTmpPath,slots);
    			}
    		}
    	}
    	return SubmitContainer.submitContainer;
    }
    
    private SubmitContainer(ClientType clientType,String zkNamespace,String host,String jobManagerPort,String jarFileTmpPath,int slots){
    	this.clientType = clientType;
    	this.zkNamespace = zkNamespace;
    	this.host= host;
    	this.jobManagerPort = jobManagerPort;
    	this.jarFileTmpPath = jarFileTmpPath;
    	this.slots = slots;
    	start();
    }
    
    
    public SubmitContainer getSubmitContainerInstance(){
    	return SubmitContainer.submitContainer;
    }
    

    public void start(){

        Properties properties = new Properties();
        JobSubmitExecutor.getInstance().init(clientType, jarFileTmpPath, slots, properties);
        JobSubmitExecutor.getInstance().start();

        logger.info("------start job container----");
        logger.info("client_type:{}", clientType);
        logger.info("zkNamespace:{}", zkNamespace);
        logger.info("jobMgrHost:{}", host);
        logger.info("jobManagerPort:{}", jobManagerPort);
        logger.info("jarFileTmpPath:{}", jarFileTmpPath);
        logger.info("slots:{}", slots);
        logger.info("------------------------------");
    }


    /**********************************************/
    public ClientType getClientType() {
        return clientType;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public String getZkNamespace() {
        return zkNamespace;
    }

    public void setZkNamespace(String zkNamespace) {
        this.zkNamespace = zkNamespace;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getJobManagerPort() {
        return jobManagerPort;
    }

    public void setJobManagerPort(String jobManagerPort) {
        this.jobManagerPort = jobManagerPort;
    }

    public String getJarFileTmpPath() {
        return jarFileTmpPath;
    }

    public void setJarFileTmpPath(String jarFileTmpPath) {
        this.jarFileTmpPath = jarFileTmpPath;
    }

	public int getSlots() {
		return slots;
	}

	public void setSlots(int slots) {
		this.slots = slots;
	}
}
