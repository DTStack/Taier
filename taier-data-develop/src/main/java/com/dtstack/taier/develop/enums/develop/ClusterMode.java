package com.dtstack.taier.develop.enums.develop;

public enum ClusterMode {

    /**
     * 通过yarn调度
     */
    YARN("yarn"),

    /**
     * 通过k8s调度
     */
    K8S("kubernetes");

    private String val;

    ClusterMode(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
