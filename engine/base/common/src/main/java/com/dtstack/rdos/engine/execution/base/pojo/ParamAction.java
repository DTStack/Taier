package com.dtstack.rdos.engine.execution.base.pojo;

import com.dtstack.rdos.common.util.PublicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * Reason: TODO ADD REASON(可选)
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ParamAction {

    private static final Logger logger = LoggerFactory.getLogger(ParamAction.class);

	private String taskId;
	
	private String engineTaskId;
	
	private String name;
	
    private Integer taskType;

    private String engineType;
    
    private Integer computeType;

	//实时独有
	private String externalPath;

	private String sqlText;
	
	private String taskParams;

	private String exeArgs;

	private String groupName;

	//选填参数,如果请求指定集群信息的话需要填写
	private String pluginInfo;

	/**
	 * 0 是从web端发起，1是有内部节点发起，如果是1就会直接执行不会再判断node运行的task任务在进行路由选择
	 */
	private Integer requestStart = 0;
	
	
	public Integer getRequestStart() {
		return requestStart;
	}

	public void setRequestStart(Integer requestStart) {
		this.requestStart = requestStart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTaskType() {
		return taskType;
	}

	public void setTaskType(Integer taskType) {
		this.taskType = taskType;
	}

	public Integer getComputeType() {
		return computeType;
	}

	public void setComputeType(Integer computeType) {
		this.computeType = computeType;
	}

	public String getSqlText() {
		return sqlText;
	}

	public void setSqlText(String sqlText) {
		this.sqlText = sqlText;
	}

	public String getTaskParams() {
		return taskParams;
	}

	public void setTaskParams(String taskParams) {
		this.taskParams = taskParams;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	public String getEngineTaskId() {
		return engineTaskId;
	}

	public void setEngineTaskId(String engineTaskId) {
		this.engineTaskId = engineTaskId;
	}

	public String getExternalPath() {
		return externalPath;
	}

	public void setExternalPath(String externalPath) {
		this.externalPath = externalPath;
	}

	public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }


	public String getExeArgs() {
		return exeArgs;
	}


	public void setExeArgs(String exeArgs) {
		this.exeArgs = exeArgs;
	}

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

	public String getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(String pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	@Override
    public String toString() {
	    String jsonStr = "";
	    try{
            jsonStr = PublicUtil.objToString(this);
        }catch (Exception e){
	        //不应该发生
            logger.error("", e);
        }

        return jsonStr;
    }
}
