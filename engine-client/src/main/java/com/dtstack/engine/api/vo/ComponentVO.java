package com.dtstack.engine.api.vo;

import com.dtstack.engine.api.domain.Component;
import io.swagger.annotations.ApiModel;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@ApiModel
public class ComponentVO extends Component {

    private String clusterName;

    private String componentConfig;

    private String componentTemplate;

    public String getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(String componentConfig) {
        this.componentConfig = componentConfig;
    }

    public String getComponentTemplate() {
        return componentTemplate;
    }

    public void setComponentTemplate(String componentTemplate) {
        this.componentTemplate = componentTemplate;
    }

    private String principals;

    private String principal;

    private String mergeKrb5Content;

    public String getMergeKrb5Content() {
        return mergeKrb5Content;
    }

    public void setMergeKrb5Content(String mergeKrb5Content) {
        this.mergeKrb5Content = mergeKrb5Content;
    }

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

    public static List<ComponentVO> toVOS(List<Component> components) {
        List<ComponentVO> vos = new ArrayList<>();
        if (CollectionUtils.isEmpty(components)) {
            return vos;
        }
        for (Component component : components) {
            ComponentVO vo = new ComponentVO();
            BeanUtils.copyProperties(component, vo);
            vos.add(vo);
        }
        return vos;
    }


    public static ComponentVO toVO(Component component) {
        ComponentVO vo = new ComponentVO();
        BeanUtils.copyProperties(component, vo);
        return vo;
    }
}

