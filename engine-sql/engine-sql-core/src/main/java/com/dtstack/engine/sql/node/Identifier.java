package com.dtstack.engine.sql.node;

import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.KeywordsHelper;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlNode;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: 尘二(chener @ dtstack.com)
 * @Date: 2019/10/26 10:14
 * @Description: 对应于: {@link SqlIdentifier}
 * 通常是一个复合标志符。identifier是一个叶子结点
 * names列表中依次为db,table,column
 *
 */
public class Identifier extends Node {

    private static final String DOT = ".";
    private static final String STAR = "*";
    private static final Integer ZORE =0;
    private static final Integer ONE =1;
    private static final Integer TWO =2;
    private static final Integer THREE =3;


    private List<String> names;

    private String db;

    private String table;

    private String column;

    public String getDb() {
        return db;
    }

    public void setDb(String db){
        this.db = db;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Identifier(String defaultDb, Map<String, List<Column>> tableColumnsMap) {
        super(defaultDb,tableColumnsMap);
    }

    @Override
    public Node parseSql(SqlNode node) {
        SqlIdentifier sqlIdentifier = checkNode(node);
        handleNames(sqlIdentifier);
        return null;
    }

    private void handleNames(SqlIdentifier sqlIdentifier) {
        this.names = sqlIdentifier.names;
        int size = names.size();
        if (size == THREE){
            this.db = names.get(ZORE);
            this.table = names.get(ONE);
            this.column = KeywordsHelper.removeKeywordsSuffix(names.get(2));
        }else if (size == TWO){
            if (Context.IDENTIFIER_COLUMN == getContext()){
                this.table = names.get(ZORE);
                this.column = KeywordsHelper.removeKeywordsSuffix(names.get(1));
            }else if (Context.IDENTIFIER_TABLE == getContext()){
                this.db = names.get(ZORE);
                this.table = names.get(ONE);
            }
        }else if (size == ONE){
            if (Context.IDENTIFIER_COLUMN == getContext()){
                this.column = KeywordsHelper.removeKeywordsSuffix(names.get(0));
            }else if (Context.IDENTIFIER_TABLE == getContext()){
                this.table = names.get(ZORE);
            }
        }
        if (db == null){
            db = getDefaultDb();
        }
    }

    private SqlIdentifier checkNode(SqlNode node) {
        if (!(node instanceof SqlIdentifier)){
            throw new IllegalStateException("sqlNode类型不匹配");
        }
        return (SqlIdentifier) node;
    }

    public boolean isSelectStarFromTable(){
        if (Context.IDENTIFIER_COLUMN != this.getContext()){
            throw new IllegalStateException("只有字段类型的identifier才有该属性");
        }
        if (STAR.equals(this.table)){
            return true;
        }
        return StringUtils.isEmpty(column);
    }

    public Column map(){
        Column column = new Column();
        column.setTable(this.table);
        column.setName(this.column);
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Identifier that = (Identifier) o;
        return Objects.equals(db, that.db) &&
                Objects.equals(table, that.table) &&
                Objects.equals(column, that.column);
    }

    @Override
    public int hashCode() {

        return Objects.hash(db, table, column);
    }

    public String getFullColumn(){
        if (Context.IDENTIFIER_COLUMN != getContext()){
            throw new IllegalArgumentException("该方法仅用于代表column的identifier");
        }
        return db+DOT+table+DOT+column;
    }

    public String getFullTable(){
        if (Context.IDENTIFIER_COLUMN != getContext() && Context.IDENTIFIER_TABLE != getContext()){
            throw new IllegalArgumentException("该方法只能用于代表column和table的identifier");
        }
        return db+DOT+table;
    }

    @Override
    public String toString() {
        return "Identifier{" +
                "db='" + db + '\'' +
                ", table='" + table + '\'' +
                ", column='" + column + '\'' +
                '}';
    }
}
