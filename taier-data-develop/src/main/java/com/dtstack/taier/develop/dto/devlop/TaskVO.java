package com.dtstack.taier.develop.dto.devlop;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.dao.domain.Task;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

/**
 * @author zhiChen
 * @date 2022/3/3 19:39
 */
public class TaskVO extends Task {

    private Map<String, Object> sourceMap;

    private Map<String, Object> targetMap;

    private Map<String, Object> settingMap;


    /**
     * flinksql 源表
     */
    private List<JSONObject> source;

    /**
     * flinksql 结果表
     */
    private List<JSONObject> sink;

    /**
     * 维表
     */
    private List<JSONObject> side;

    /**
     * 用户 ID
     */
    private Long userId;

    private Integer whereToEnter = 0;

    private Boolean updateSource = true;

    /**
     * 运行时间
     */
    private Timestamp execStartTime;

    private Integer syncContent;

    /**
     * 任务版本
     */
    private List<TaskVersionVO> taskVersions;

    /**
     * 离线的参数替换字段
     */
    private List<Map> taskVariables;

    private Long parentId;
    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Map<String, Object> getSourceMap() {
        return sourceMap;
    }

    public void setSourceMap(Map<String, Object> sourceMap) {
        this.sourceMap = sourceMap;
    }

    public Map<String, Object> getTargetMap() {
        return targetMap;
    }

    public void setTargetMap(Map<String, Object> targetMap) {
        this.targetMap = targetMap;
    }

    public Map<String, Object> getSettingMap() {
        return settingMap;
    }

    public void setSettingMap(Map<String, Object> settingMap) {
        this.settingMap = settingMap;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getWhereToEnter() {
        return whereToEnter;
    }

    public void setWhereToEnter(Integer whereToEnter) {
        this.whereToEnter = whereToEnter;
    }

    public Boolean getUpdateSource() {
        return updateSource;
    }

    public void setUpdateSource(Boolean updateSource) {
        this.updateSource = updateSource;
    }

    public Timestamp getExecStartTime() {
        return execStartTime;
    }

    public void setExecStartTime(Timestamp execStartTime) {
        this.execStartTime = execStartTime;
    }

    public Integer getSyncContent() {
        return syncContent;
    }

    public void setSyncContent(Integer syncContent) {
        this.syncContent = syncContent;
    }

    public List<Map> getTaskVariables() {
        return taskVariables;
    }

    public void setTaskVariables(List<Map> taskVariables) {
        this.taskVariables = taskVariables;
    }

    public void setSource(List<JSONObject> source) {
        this.source  = source;
    }

    public List<JSONObject> getSource() {
        return source;
    }

    public void setSink(List<JSONObject> sink) {
        this.sink  = sink;
    }

    public List<JSONObject> getSink() {
        return sink;
    }

    public void setSide(List<JSONObject> side) {
        this.side  = side;
    }

    public List<JSONObject> getSide() {
        return side;
    }
}
