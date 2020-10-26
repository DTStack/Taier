package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import javafx.util.Pair;
import org.apache.calcite.sql.SqlBasicCall;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlNodeList;
import org.apache.calcite.sql.SqlOperator;

import java.util.List;
import java.util.Map;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/26 10:09
 * @Description: 对应于:{@link SqlNode} sql查询树的结点
 * {@link SqlNodeList}也是一个SqlNode
 */
public abstract class Node {

    public Node(String defaultDb,Map<String, List<Column>> tableColumnsMap) {
        this.defaultDb = defaultDb;
        this.tableColumnMap = tableColumnsMap;
    }

    private String defaultDb;

    public String getDefaultDb() {
        return defaultDb;
    }

    public void setDefaultDb(String defaultDb) {
        this.defaultDb = defaultDb;
    }

    /**
     * hive 血缘专用 侧视图中属性和真正来源字段的映射  key 侧视图字段 value来源字段
     */
    private Map<String,List<Identifier>> lateralView;

    public Map<String,List<Identifier>> getLateralView() {
        return lateralView;
    }

    public void setLateralView(Map<String,List<Identifier>> lateralView) {
        this.lateralView = lateralView;
    }

    /**
     * 别名。将SqlBasicCall operator为AS的树简化掉AS的部分
     */
    private String alias;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Map<String, List<Column>> tableColumnMap;

    public Map<String, List<Column>> getTableColumnMap() {
        return tableColumnMap;
    }

    public void setTableColumnMap(Map<String, List<Column>> tableColumnMap) {
        this.tableColumnMap = tableColumnMap;
    }

    /**
     * 解析结点  用于calcite 解析  astNode 解析是放到各自的parser里
     * @param node
     * @return
     */
    public abstract Node parseSql(SqlNode node);

    /**
     * 重要：将as子树合并后再去parseSql
     * 合并as子树
     * @param node
     * @return
     */
    public Pair<String,SqlNode> removeAs(SqlNode node){
        if (node instanceof SqlBasicCall){
            SqlOperator operator = ((SqlBasicCall)node).getOperator();
            if (SqlKind.AS == operator.kind){
                return new Pair<>(((SqlBasicCall) node).operands[1].toString(),((SqlBasicCall) node).operands[0]);
            }
        }
        return null;
    }

    public SqlNode removeAsAndSetAlias(SqlNode node, Node targetNode){
        Pair<String, SqlNode> sqlNodePair = removeAs(node);
        SqlNode handledNode = node;
        String alias = null;
        if (sqlNodePair != null){
            handledNode = sqlNodePair.getValue();
            alias = sqlNodePair.getKey();
        }
        targetNode.setAlias(alias);
        return handledNode;
    }
    /**
     * 语法树的上下文。同一种类型的结点在不同的上下文环境中可能需要不同的处理
     */
    public enum Context{
        /**
         * 代表表的identifier
         */
        IDENTIFIER_TABLE,
        /**
         * 代表字段的identifier
         */
        IDENTIFIER_COLUMN,
        /**
         * select中的子查询
         */
        SELECT_SUB_QUERY,

        /**
         * select中字段的子查询
         */
        SELECT_COLUMN_QUERY,
        /**
         * from中的子查询
         */
        FROM_SUB_QUERY,
        /**
         * 函数对字段的处理
         */
        CALL_IN_COLUMN,
        /**
         * case函数
         */
        CASE_IN_COLUMN,
        /**
         * join对表的处理
         */
        CALL_JOIN,
        /**
         * from union
         */
        CALL_UNION,
        /**
         * insert into select union
         * 这样的语句有双重血缘
         */
        INSERT_FROM_UNION,


    }
}
