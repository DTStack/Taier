package com.dtstack.engine.yarn;

import com.dtstack.engine.base.BaseConfig;

import java.util.Map;

/**
 * Reason:
 * Date: 2018/5/14
 * Company: www.dtstack.com
 * @author xuchao
 */

public class Config extends BaseConfig {

    private String md5sum;

    private String hadoopUserName;

    private Map<String, Object> hadoopConf;

    private Map<String, Object> yarnConf;
    /**
     * 组件名称
     */
    private String componentName;


    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
    }

    public Map<String, Object> getHadoopConf() {
        return hadoopConf;
    }

    public void setHadoopConf(Map<String, Object> hadoopConf) {
        this.hadoopConf = hadoopConf;
    }

    public Map<String, Object> getYarnConf() {
        return yarnConf;
    }

    public void setYarnConf(Map<String, Object> yarnConf) {
        this.yarnConf = yarnConf;
    }

    public String getMd5sum() {
        return md5sum;
    }

    public void setMd5sum(String md5sum) {
        this.md5sum = md5sum;
    }

    public String getHadoopUserName() {
        return hadoopUserName;
    }

    public void setHadoopUserName(String hadoopUserName) {
        this.hadoopUserName = hadoopUserName;
    }
}
