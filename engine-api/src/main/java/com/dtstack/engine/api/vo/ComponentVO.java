package com.dtstack.engine.api.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ComponentVO extends Component {

    private String clusterName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public static List<ComponentVO> toVOS(List<Component> components, boolean removeTypeName) {
        List<ComponentVO> vos = new ArrayList<>();
        for (Component component : components) {
            vos.add(toVO(component,removeTypeName));
        }
        return vos;
    }

    public static ComponentVO toVO(Component component,boolean removeTypeName){
        ComponentVO vo = new ComponentVO();
        BeanUtils.copyProperties(component, vo);
        //前端默认不展示kerberosConfig
        JSONObject jsonObject = JSONObject.parseObject(component.getComponentConfig());
        if(removeTypeName){
            jsonObject.remove("typeName");
        }
        jsonObject.remove("md5zip");
        vo.setComponentConfig(jsonObject.toJSONString());
        return vo;
    }
}

