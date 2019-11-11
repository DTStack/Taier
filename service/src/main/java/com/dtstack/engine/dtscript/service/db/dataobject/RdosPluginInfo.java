package com.dtstack.engine.dtscript.service.db.dataobject;

/**
 * RDOS 插件信息
 * Date: 2018/2/6
 * Company: www.dtstack.com
 * @author xuchao
 */

public class RdosPluginInfo extends DataObject{

    private String pluginKey;

    private String pluginInfo;

    private int type;

    public String getPluginKey() {
        return pluginKey;
    }

    public void setPluginKey(String pluginKey) {
        this.pluginKey = pluginKey;
    }

    public String getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(String pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
