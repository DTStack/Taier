package com.dtstack.engine.common.console;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @program: DAGScheduleX
 * @description:
 * @create: 2021-12-16 00:10
 **/
public class SecurityResult<T> {

    /**
     * 操作人
     */
    private String operator;

    /**
     * 操作人在当前app下的userId
     */
    private Long operatorId;

    /**
     * 操作人在当前app下的租户id
     */
    private Long tenantId;

    /**
     * 暂时未使用.操作人在当前app下的项目id
     */
    private Long projectId;

    /**
     * 是否不记录日志
     */
    private boolean ignoreLog;

    /**
     * 构造{@link com.dtstack.engine.common.enums.ActionType}需要用到的参数列表
     */
    private Map<String,Object> securityDataMap = new HashMap<>(4);

    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public SecurityResult addSecurityData(String key,Object value){
        securityDataMap.put(key,value);
        return this;
    }

    public Object getSecurityData(String key){
        return securityDataMap.get(key);
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public boolean isIgnoreLog() {
        return ignoreLog;
    }

    public void setIgnoreLog(boolean ignoreLog) {
        this.ignoreLog = ignoreLog;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
