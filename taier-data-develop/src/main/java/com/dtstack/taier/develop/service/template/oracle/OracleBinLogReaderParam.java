package com.dtstack.taier.develop.service.template.oracle;


import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020/1/10
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class OracleBinLogReaderParam extends DaPluginParam {
    /**
     * 0从任务运行时开始
     * 1按时间选择
     * 2按文件选择
     */
    private Integer collectType;
    /**
     * 文件名称
     */
    private String journalName;
    /**
     * 分组分表
     */
    private Map<String, Object> distributeTable;

    /**
     * 监听操作类型
     * DAoperators数组
     */
    private List<Integer> cat;

    /**
     * 嵌套JSON平铺
     */
    private Boolean pavingData;

    /**
     * 开始的SCN
     */
    private String startSCN;

    private String schema;

    private Boolean allTable;

    private List<String> table;

    // oracle 可拔插数据库（PDB）
    private String pdbName;

    public Integer getCollectType() {
        return collectType;
    }

    public void setCollectType(Integer collectType) {
        this.collectType = collectType;
    }

    public String getJournalName() {
        return journalName;
    }

    public void setJournalName(String journalName) {
        this.journalName = journalName;
    }

    public Map<String, Object> getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(Map<String, Object> distributeTable) {
        this.distributeTable = distributeTable;
    }

    public List<Integer> getCat() {
        return cat;
    }

    public void setCat(List<Integer> cat) {
        this.cat = cat;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }

    public String getStartSCN() {
        return startSCN;
    }

    public void setStartSCN(String startSCN) {
        this.startSCN = startSCN;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Boolean getAllTable() {
        return allTable;
    }

    public void setAllTable(Boolean allTable) {
        this.allTable = allTable;
    }

    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

    public String getPdbName() {
        return pdbName;
    }

    public void setPdbName(String pdbName) {
        this.pdbName = pdbName;
    }
}
