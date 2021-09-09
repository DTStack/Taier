package com.dtstack.engine.common.pojo;

/**
 * @author xinge
 */
public class DtScriptAgentLabel {

    private Boolean autoDeploy;

    private Boolean autoUpdate;

    private String id;

    private String name;
    /**
     * 主机标签，多个,分割
     */
    private String label;

    private String os;

    private String version;

    /**
     * 主机ip
     */
    private String localIp;


    public Boolean getAutoDeploy() {
        return autoDeploy;
    }

    public void setAutoDeploy(Boolean autoDeploy) {
        this.autoDeploy = autoDeploy;
    }

    public Boolean getAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(Boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocalIp() {
        return localIp;
    }

    public void setLocalIp(String localIp) {
        this.localIp = localIp;
    }

    @Override
    public String toString() {
        return "DtScriptAgentLabel{" +
                "autoDeploy=" + autoDeploy +
                ", autoUpdate=" + autoUpdate +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", label='" + label + '\'' +
                ", os='" + os + '\'' +
                ", version='" + version + '\'' +
                ", localIp='" + localIp + '\'' +
                '}';
    }
}
