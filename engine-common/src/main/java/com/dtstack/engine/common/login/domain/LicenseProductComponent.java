package com.dtstack.engine.common.login.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author: 小北(xiaobei @ dtstack.com)
 * @program: DAGScheduleX
 * @description:
 * @create: 2021-12-16 00:00
 **/
public class LicenseProductComponent implements Serializable {
    private String componentName;

    private String componentCode;

    private List<LicenseProductComponentWidget> widgets;

    public String getComponentCode() {
        return componentCode;
    }

    public void setComponentCode(String componentCode) {
        this.componentCode = componentCode;
    }

    public List<LicenseProductComponentWidget> getWidgets() {
        return widgets;
    }

    public void setWidgets(List<LicenseProductComponentWidget> widgets) {
        this.widgets = widgets;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }
}
