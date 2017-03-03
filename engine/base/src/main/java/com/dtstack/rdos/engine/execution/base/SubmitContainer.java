package com.dtstack.rdos.engine.execution.base;

import com.dtstack.rdos.engine.execution.base.enumeration.ClientType;

/**
 * Reason:
 * Date: 2017/2/22
 * Company: www.dtstack.com
 * @ahthor xuchao
 */

public class SubmitContainer {

    private ClientType clientType;

    private String zkNamespace;

    private String host;

    private String jobManagerPort;

    /**
     * 设置上传jar文件临时目录,如果未设置默认是/tmp/flinkjar
     */
    private String jarFileTmpPath;
    
    //计算资源
    private int slots;
    
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
