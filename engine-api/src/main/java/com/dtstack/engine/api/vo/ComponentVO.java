package com.dtstack.engine.api.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class ComponentVO extends Component {

    public static List<ComponentVO> toVOS(List<Component> components) {
        List<ComponentVO> vos = new ArrayList<>();
        for (Component component : components) {
            vos.add(toVO(component));
        }
        return vos;
    }

    public static ComponentVO toVO(Component component){
        ComponentVO vo = new ComponentVO();
        BeanUtils.copyProperties(component, vo);
        //前端默认不展示kerberosConfig
        JSONObject jsonObject = JSONObject.parseObject(component.getComponentConfig());
        jsonObject.remove("typeName");
        vo.setComponentConfig(jsonObject.toJSONString());
        return vo;
    }
}

