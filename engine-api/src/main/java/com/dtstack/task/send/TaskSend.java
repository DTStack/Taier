package com.dtstack.task.send;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public class TaskSend extends AbstractSend {
    public TaskSend(String nodes,Integer appType) {
        super(nodes,appType);
    }

    public String taskPost(String body, String url) {
        Object result = this.post(url, body, (Map)null, 1);
        return result != null ? result.toString() : null;
    }


    public <T> T taskPost(String body, String url,Class<T> tClass) {
        return JSONObject.parseObject(this.taskPost(body,url), tClass);
    }


    public <T> List<T> taskPostList(String body, String url, Class<T> tClass) {
        return JSONObject.parseArray(this.taskPost(body,url), tClass);
    }


    public String sendTaskInfo(String body) {
        Object result = this.post("/api/task/service/batchTaskShade/info", body, (Map)null, 3);
        return result != null ? result.toString() : "";
    }
}
