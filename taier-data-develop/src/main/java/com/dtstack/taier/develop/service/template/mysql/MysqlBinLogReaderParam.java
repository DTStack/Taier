package com.dtstack.taier.develop.service.template.mysql;



import com.dtstack.taier.develop.service.template.DaPluginParam;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020/2/18
 * Company: www.dtstack.com
 *
 * @author xiaochen
 */
public class MysqlBinLogReaderParam extends DaPluginParam {

    private List<String> table;
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
     * 不与journalName共存
     * 起始时间
     */
    private Long timestamp;

    private Boolean allTable;
    /**
     * 分组分表
     * 仅做展示用
     */
    private Map<String, Object> distributeTable;

    /**
     * 是否分组分表
     * 仅做展示
     */
    private Boolean multipleTable;

    /**
     * 监听操作类型
     * DAoperators数组
     */
    private List<Integer> cat;

    /**
     * 标识采集方式
     */
    private Integer rdbmsDaType;

    private Boolean pavingData;

    /**
     * doris配置规则
     * "mapper": {
     *               "databaseMapper": {
     *                 "prefix": "ods",
     *                 "suffix": "",
     *                 "connectStr": "_",
     *                 "customize": {
     *                   "test": "ods_tiezhu"
     *                 }
     *               },
     *               "tableMapper": {
     *                 "prefix": "ods",
     *                 "suffix": "test",
     *                 "connectStr": "_",
     *                 "customize": {
     *                   "doris_1": "ods_testOne",
     *                   "doris_2": "ods_testOne",
     *                   "doris_3": "ods_testOne",
     *                   "doris_4": "ods_testOne"
     *                 }
     *               },
     *               "fieldMapper": {
     *                 "prefix": "ods",
     *                 "suffix": "test",
     *                 "connectStr": "_",
     *                 "customize": {
     *                   "id": "ods_id_test"
     *                 }
     *               }
     *             }
     */
    private Map<String,Object> mapper;
    private Integer syncContent;

    public Integer getSyncContent() {
        return syncContent;
    }

    public void setSyncContent(Integer syncContent) {
        this.syncContent = syncContent;
    }

    public Map<String, Object> getMapper() {
        return mapper;
    }

    public void setMapper(Map<String, Object> mapper) {
        this.mapper = mapper;
    }
    public List<String> getTable() {
        return table;
    }

    public void setTable(List<String> table) {
        this.table = table;
    }

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

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getDistributeTable() {
        return distributeTable;
    }

    public void setDistributeTable(Map<String, Object> distributeTable) {
        this.distributeTable = distributeTable;
    }

    public Boolean getMultipleTable() {
        return multipleTable;
    }

    public void setMultipleTable(Boolean multipleTable) {
        this.multipleTable = multipleTable;
    }

    public List<Integer> getCat() {
        return cat;
    }

    public void setCat(List<Integer> cat) {
        this.cat = cat;
    }

    public Integer getRdbmsDaType() {
        return rdbmsDaType;
    }

    public void setRdbmsDaType(Integer rdbmsDaType) {
        this.rdbmsDaType = rdbmsDaType;
    }

    public Boolean getAllTable() {
        return allTable;
    }

    public void setAllTable(Boolean allTable) {
        this.allTable = allTable;
    }

    public Boolean getPavingData() {
        return pavingData;
    }

    public void setPavingData(Boolean pavingData) {
        this.pavingData = pavingData;
    }
}
