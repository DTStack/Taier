package com.dtstack.engine.api.vo;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.engine.api.domain.Component;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class ComponentVO extends Component {

    private String clusterName;

    private String principals;

    private String principal;

    public String getPrincipals() {
        return principals;
    }

    public void setPrincipals(String principals) {
        this.principals = principals;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public static List<ComponentVO> toVOS(List<Component> components, boolean removeTypeName) {
        List<ComponentVO> vos = new ArrayList<>();
        if(CollectionUtils.isEmpty(components)){
            return vos;
        }
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
            jsonObject.remove("md5zip");
        }
        vo.setComponentConfig(jsonObject.toJSONString());
        return vo;
    }
}

