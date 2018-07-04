package com.dtstack.rdos.engine.execution.base;

/**
 * Reason:
 * Date: 2018/7/3
 * Company: www.dtstack.com
 * @author xuchao
 */

public class AddJarInfo {

    private String jarPath;

    private String mainClass;

    public String getJarPath() {
        return jarPath;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }
}
