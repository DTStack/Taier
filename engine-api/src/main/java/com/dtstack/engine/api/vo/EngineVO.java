package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.Engine;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class EngineVO extends Engine {

    private Long engineId;

    private List<QueueVO> queues;

    private boolean security;

    private List<ComponentVO> components;

    private JSONObject resource;

    public static EngineVO toVO(Engine engine) {
        EngineVO vo = new EngineVO();
        try {
            BeanUtils.copyProperties(engine, vo);
            vo.setEngineId(engine.getId());

            JSONObject resource = new JSONObject();
            resource.put("totalCore", engine.getTotalCore());
            resource.put("totalMemory", engine.getTotalMemory());
            resource.put("totalNode", engine.getTotalNode());
            vo.setResource(resource);

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return vo;
    }

    public static List<EngineVO> toVOs(List<Engine> engines) {
        List<EngineVO> vos = new ArrayList<>();
        for (Engine engine : engines) {
            vos.add(toVO(engine));
        }
        return vos;
    }

    public List<ComponentVO> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentVO> components) {
        this.components = components;
    }

    public JSONObject getResource() {
        return resource;
    }

    public void setResource(JSONObject resource) {
        this.resource = resource;
    }

    public boolean getSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public Long getEngineId() {
        return engineId;
    }

    public void setEngineId(Long engineId) {
        this.engineId = engineId;
    }

    public List<QueueVO> getQueues() {
        return queues;
    }

    public void setQueues(List<QueueVO> queues) {
        this.queues = queues;
    }
}

