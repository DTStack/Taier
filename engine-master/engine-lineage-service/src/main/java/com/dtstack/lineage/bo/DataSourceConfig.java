package com.dtstack.lineage.bo;

/**
 * @author chener
 * @Classname DataSourceConfig
 * @Description 数据源配置信息
 * @Date 2020/10/23 15:00
 * @Created chener@dtstack.com
 */
public abstract class DataSourceConfig {

    private boolean openKerberos = false;

    private String kerberosConfig;

    public boolean isOpenKerberos() {
        return openKerberos;
    }

    public void setOpenKerberos(boolean openKerberos) {
        this.openKerberos = openKerberos;
    }

    public String getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(String kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    /**
     * 获取配置json
     * @return
     */
    public abstract String getConfigJson();

    /**
     * 物理数据源定位码
     * @return
     */
    public abstract String generateRealSourceKey();
}
