package com.dtstack.engine.api.pojo.lineage;

import com.dtstack.engine.api.enums.TableOperateEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 表描述类
 *
 * @author jiangbo
 */
public class Table{

    /**
     * 表所在数据库
     */
    private String db;


    /**schema名称**/
    private String schemaName;

    /**
     * 名称
     */
    private String name;

    /**
     * 别名
     */
    private String alias;

    /**
     * 描述
     */
    private String comment;

    /**
     * 分隔符，hive表属性
     */
    private String delim;

    /**
     * 存储格式，hive表属性
     */
    private String storeType;

    /**
     * 表路径
     */
    private String path;

    /**
     * 生命周期
     */
    private Integer lifecycle;

    /**
     * 字段
     */
    private List<Column> columns;

    /**
     * 分区字段
     */
    private List<Column> partitions;

    /**
     * 是否临时表
     */
    private boolean isTemp;

    /**
     * 是否为视图
     */
    private boolean isView;

    /**
     * 是否分区表
     */
    private boolean partitionTable;

    /**
     * sql对表的操作类型
     */
    private TableOperateEnum operate;

    /**
     * ETableType
     */
    private Integer tableType;

    /**
     * 类目id
     */
    private Long catalogueId;

    /**
     * 是否忽略
     */
    private boolean isIgnore;

    /**
     * 表类型:EXTERNAL-外部表，MANAGED-内部表
     */
    private String externalOrManaged;

    /**
     * 是否为主表
     */
    private boolean isMain;


    public TableOperateEnum getOperate() {
        return operate;
    }

    public void setOperate(TableOperateEnum operate) {
        this.operate = operate;
    }

    public Integer getTableType() {
        return tableType;
    }

    public void setTableType(Integer tableType) {
        this.tableType = tableType;
    }

    public boolean isPartitionTable() {
        return partitionTable;
    }

    public void setPartitionTable(boolean partitionTable) {
        this.partitionTable = partitionTable;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public Table() {
    }

    public Table(String db, String name) {
        this.db = db;
        this.name = name;
    }

    public String getDb() {
        return db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDelim() {
        return delim;
    }

    public void setDelim(String delim) {
        this.delim = delim;
    }

    public String getStoreType() {
        return storeType;
    }

    public void setStoreType(String storeType) {
        this.storeType = storeType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(Integer lifecycle) {
        this.lifecycle = lifecycle;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Column> getPartitions() {
        return partitions;
    }

    public void setPartitions(List<Column> partitions) {
        this.partitions = partitions;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Table)) {
            return false;
        }

        boolean isSameDb = StringUtils.equalsIgnoreCase(db, ((Table) obj).db);
        if (!isSameDb) {
            return false;
        }

        return StringUtils.equalsIgnoreCase(name, ((Table) obj).name);
    }

    public Long getCatalogueId() {
        return catalogueId;
    }

    public void setCatalogueId(Long catalogueId) {
        this.catalogueId = catalogueId;
    }

    public boolean isIgnore() {
        return isIgnore;
    }

    public void setIgnore(boolean ignore) {
        isIgnore = ignore;
    }

    public String getExternalOrManaged() {
        return externalOrManaged;
    }

    public void setExternalOrManaged(String externalOrManaged) {
        this.externalOrManaged = externalOrManaged;
    }

    public boolean isMain() {
        return isMain;
    }

    public void setMain(boolean main) {
        isMain = main;
    }

    @Override
    public String toString() {
        return "Table{" +
                "db='" + db + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", comment='" + comment + '\'' +
                ", delim='" + delim + '\'' +
                ", storeType='" + storeType + '\'' +
                ", path='" + path + '\'' +
                ", lifecycle=" + lifecycle +
                ", columns=" + columns +
                ", partitions=" + partitions +
                ", isTemp=" + isTemp +
                '}';
    }

    public String toSimpleString() {
        StringBuilder columnsStr = new StringBuilder();
        columnsStr.append("[");
        columns.forEach(col -> columnsStr.append(col.getName()).append("-").append(col.getAlias()).append(","));
        columnsStr.append("]");

        return "Table{" +
                "db='" + db + '\'' +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", columns=" + columnsStr.toString() +
                ", isTemp=" + isTemp +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int hashCode() {
        String dbStr = Objects.isNull(db)?null:db.toLowerCase();
        String nameStr = Objects.isNull(name)?null:name.toLowerCase();
        return Objects.hash(dbStr, nameStr);
    }
}
