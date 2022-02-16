package com.dtstack.taier.dao.domain.po;

import com.alibaba.fastjson.JSONObject;
import com.dtstack.taier.common.constant.FormNames;
import com.dtstack.taier.common.util.DataSourceUtils;
import com.dtstack.taier.dao.domain.DsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Map;

public class DsInfoBO {

    private static final Logger LOGGER = LoggerFactory.getLogger(DsInfoBO.class);

    private Long id;

    /**
     * 数据源类型唯一
     */

    private String dataType;

    /**
     * 数据源类型编码
     */
    private Integer dataTypeCode;

    /**
     * 数据源版本
     */
    private String dataVersion;

    /**
     * 数据源名称
     */
    private String dataName;


    private Long tenantId;


    /**
     * 数据源填写的表单信息
     */
    private String dataJson;

    private JSONObject data;

    /**
     * 连接状态
     */
    private Integer status;

    /**
     * 是否有meta标志
     */
    private Integer isMeta;

    /**
     * 数据库名称
     */
    private String schemaName;

    private String defaultFs;

    private Map<String,Object> hadoopConfig;

    private Map<String,Object> kerberosConfig;

    private String  userName;

    private String password;

    private String jdbc;


    public static DsInfoBO buildDsInfoBO(DsInfo dsInfo){
        DsInfoBO bo = new DsInfoBO();
        BeanUtils.copyProperties(dsInfo,bo);
        try {
            JSONObject data = DataSourceUtils.getDataSourceJson(dsInfo.getDataJson());
            bo.setData(data);
            JSONObject hadoopConfig = data.getJSONObject(FormNames.HADOOP_CONFIG);
            hadoopConfig = hadoopConfig == null?new JSONObject(1):hadoopConfig;
            String defaultFs = String.valueOf(data.getOrDefault(FormNames.DEFAULT_FS,""));
            bo.setHadoopConfig(hadoopConfig.fluentPut(FormNames.DEFAULT_FS,defaultFs));
            bo.setKerberosConfig(data.getJSONObject(FormNames.KERBEROS_CONFIG));
            bo.setDefaultFs(defaultFs);
            bo.setUserName(String.valueOf(data.getOrDefault(FormNames.USERNAME,"")));
            bo.setPassword(String.valueOf(data.getOrDefault(FormNames.PASSWORD,"")));
            bo.setJdbc(String.valueOf(data.getOrDefault(FormNames.JDBC_URL,"")));
        }catch (Exception e){
            LOGGER.error("build datasource info",e);
            throw e;
        }
        return bo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getDataTypeCode() {
        return dataTypeCode;
    }

    public void setDataTypeCode(Integer dataTypeCode) {
        this.dataTypeCode = dataTypeCode;
    }

    public String getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(String dataVersion) {
        this.dataVersion = dataVersion;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getIsMeta() {
        return isMeta;
    }

    public void setIsMeta(Integer isMeta) {
        this.isMeta = isMeta;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getDefaultFs() {
        return defaultFs;
    }

    public void setDefaultFs(String defaultFs) {
        this.defaultFs = defaultFs;
    }

    public Map<String, Object> getHadoopConfig() {
        return hadoopConfig;
    }

    public void setHadoopConfig(Map<String, Object> hadoopConfig) {
        this.hadoopConfig = hadoopConfig;
    }

    public Map<String, Object> getKerberosConfig() {
        return kerberosConfig;
    }

    public void setKerberosConfig(Map<String, Object> kerberosConfig) {
        this.kerberosConfig = kerberosConfig;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJdbc() {
        return jdbc;
    }

    public void setJdbc(String jdbc) {
        this.jdbc = jdbc;
    }
}
