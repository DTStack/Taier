package com.dtstack.engine.sql.hive.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.Table;
import com.dtstack.engine.sql.node.Identifier;
import com.dtstack.engine.sql.node.Node;
import com.dtstack.engine.sql.node.OtherNode;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用来解析 一些表结构操作 数据库操作
 */
public class OtherNodeParser extends NodeParser{


    /**
     * 一些特殊操作 不解析操作类型的 只获取到sql类型
     */
    public void getSqlType(ASTNode node, ParseResult parseResult){
        switch (node.getToken().getType()) {
            // 数据库操作
            case HiveParser.TOK_CREATEDATABASE:
            case HiveParser.TOK_DROPDATABASE:
            case HiveParser.TOK_ALTERDATABASE_OWNER:
            case HiveParser.TOK_ALTERDATABASE_PROPERTIES:
            case HiveParser.TOK_SWITCHDATABASE:
            case HiveParser.TOK_SHOWDATABASES:
            case HiveParser.TOK_DESCDATABASE:
                parseResult.setSqlType(SqlType.DATABASE_OPERATE);
                break;
            // 函数操作
            case HiveParser.TOK_CREATEFUNCTION:
                parseResult.setSqlType(SqlType.CREATE_FUNCTION);
                break;
            case HiveParser.TOK_DROPFUNCTION:
                parseResult.setSqlType(SqlType.DROP_FUNCTION);
                break;
            case HiveParser.TOK_RELOADFUNCTION:
                parseResult.setSqlType(SqlType.RELOAD_FUNCTION);
                break;
            case HiveParser.TOK_SHOWFUNCTIONS:
            case HiveParser.TOK_DESCFUNCTION:
                parseResult.setSqlType(SqlType.SHOW_FUNCTION);
                break;
            case HiveParser.TOK_UPDATE_TABLE:
                parseResult.setSqlType(SqlType.UPDATE);
                break;
            case HiveParser.TOK_DELETE_FROM:
                parseResult.setSqlType(SqlType.DELETE);
                break;
            case HiveParser.TOK_EXPLAIN:
                parseResult.setSqlType(SqlType.EXPLAIN);
                break;
            // 查看表信息
            case HiveParser.TOK_SHOWTABLES:
                parseResult.setSqlType(SqlType.SHOW_TABLES);
                break;
            case HiveParser.TOK_SHOW_CREATETABLE:
                parseResult.setSqlType(SqlType.SHOW_CREATETABLE);
                break;
            case HiveParser.TOK_SHOW_TBLPROPERTIES:
                parseResult.setSqlType(SqlType.SHOW_TBLPROPERTIES);
                break;
            case HiveParser.TOK_SHOWPARTITIONS:
                parseResult.setSqlType(SqlType.SHOW_PARTITIONS);
                break;
            case HiveParser.TOK_SHOWCOLUMNS:
                parseResult.setSqlType(SqlType.SHOW_COLUMNS);
                break;
            case HiveParser.TOK_DESCTABLE:
                parseResult.setSqlType(SqlType.DESC_TABLE);
                break;
            // 删表
            case HiveParser.TOK_DROPTABLE:
                parseResult.setSqlType(SqlType.DROP);
                break;
            // 清空表
            case HiveParser.TOK_TRUNCATETABLE:
                parseResult.setSqlType(SqlType.TRUNCATE);
                break;
            case HiveParser.TOK_LOAD:
                parseResult.setSqlType(SqlType.LOAD);
                break;
            case HiveParser.TOK_SHOW_SET_ROLE:
            case HiveParser.TOK_SHOW_ROLES:
                parseResult.setSqlType(SqlType.SHOW);
                break;
            default:
                parseResult.setSqlType(SqlType.OTHER);
                break;
        }
    }
    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        OtherNode otherNode = new OtherNode(defultDb,tableColumnsMap);
        if (HiveParser.TOK_TRUNCATETABLE == node.getType()){
            if (HiveParser.TOK_TABLE_PARTITION == node.getChild(0).getType()){
                Identifier identifier = new Identifier(defultDb,tableColumnsMap);
                getTableName(identifier, (ASTNode) node.getChild(0).getChild(0),defultDb);
                otherNode.setTable(identifier);
            }
        }
        return otherNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {

    }
}
