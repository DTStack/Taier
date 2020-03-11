package com.dtstack.engine.common.pojo;

import com.dtstack.engine.common.constrant.ConfigConstant;
import com.dtstack.engine.common.util.PublicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 
 *
 * Date: 2017年03月03日 下午1:25:18
 * Company: www.dtstack.com
 * @author sishu.yss
 *
 */
public class ParamAction {

    private static final Logger logger = LoggerFactory.getLogger(ParamAction.class);

	private String taskId;
	
	private String engineTaskId;

	private String applicationId;
	
	private String name;
	
    private Integer taskType;

    private String engineType;
    
    private Integer computeType;

	//实时独有
	private String externalPath;

	private String sqlText;
	
	private String taskParams;

	private String exeArgs;

	private String groupName = ConfigConstant.DEFAULT_GROUP_NAME;

	//选填参数,如果请求指定集群信息的话需要填写
	private Map<String, Object> pluginInfo;

	/**
	 * 0 是从web端发起，1是有内部节点发起，如果是1就会直接执行不会再判断node运行的task任务在进行路由选择
	 */
	private Integer requestStart = 0;

	private Integer sourceType;

	private long priority;

	private long generateTime = System.currentTimeMillis();

	private Integer maxRetryNum;

	private long stopJobId;

	private long lackingCount;


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

	public Map<String, Object> getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(Map<String, Object> pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public Integer getSourceType() {
		return sourceType;
	}

	public void setSourceType(Integer sourceType) {
		this.sourceType = sourceType;
	}

	public long getGenerateTime() {
		return generateTime;
	}

	public void setGenerateTime(long generateTime) {
		this.generateTime = generateTime;
	}

	public long getPriority() {
		return priority;
	}

	public void setPriority(long priority) {
		this.priority = priority;
	}

	public String getApplicationId() {
		return applicationId;
	}

	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}

	public Integer getMaxRetryNum() {
		return maxRetryNum;
	}

	public void setMaxRetryNum(Integer maxRetryNum) {
		this.maxRetryNum = maxRetryNum;
	}

	public long getStopJobId() {
		return stopJobId;
	}

	public void setStopJobId(long stopJobId) {
		this.stopJobId = stopJobId;
	}

	public long getLackingCount() {
		return lackingCount;
	}

	public void setLackingCount(long lackingCount) {
		this.lackingCount = lackingCount;
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
