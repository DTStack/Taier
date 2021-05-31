package com.dtstack.engine.api.vo;

/**
 * @Auther: dazhi
 * @Date: 2021/5/19 10:34 上午
 * @Email:dazhi@dtstack.com
 * @Description:
 */
public class AlterSftpVO {
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
}
