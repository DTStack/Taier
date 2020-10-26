/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.dtstack.engine.sql;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 字段血缘解析类
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class ColumnLineageParser {

    private static Logger logger = LoggerFactory.getLogger(ColumnLineageParser.class);

    private static final String SPLIT_DOT = ".";

    protected Map<String,List<Column>> tableColumnMap;

    public void setTableColumnMap(Map<String, List<Column>> tableColumnMap) {
        this.tableColumnMap = tableColumnMap;
    }

    /**
     * 解析字段血缘
     *
     * @return
     */
    public List<ColumnLineage> parseLineage(QueryTableTree rootTree){
        List<ColumnLineage> columnLineages = null;
        try {
            // fill parent
            fillParentNode(rootTree);

            moveUpNullNode(rootTree);

            // find leaf node
            columnLineages = new ArrayList<>();
            List<QueryTableTree> leafNodes = findLeafNode(rootTree);
            for (QueryTableTree leafNode : leafNodes) {
                fillMetaColumn(leafNode);
            }
            for (QueryTableTree child : rootTree.getChildren()) {
                fillMetaColumn(child);
            }

            for (QueryTableTree leafNode : leafNodes) {
                columnLineages.addAll(getColumnLineage(leafNode));
            }
            for (QueryTableTree child : rootTree.getChildren()) {
                columnLineages.addAll(getColumnLineage(child));
            }

            trimCols(columnLineages);
        } catch (Exception e) {
            logger.error("parse column lineage {}", JSONObject.toJSONString(rootTree));
        }

        return columnLineages;
    }


    /**
     * 将tableName和字段为空的查询上移
     * 需要注意 ConcurrentModificationException
     */
    private void moveUpNullNode(QueryTableTree rootTree){
        if (CollectionUtils.isEmpty(rootTree.getChildren())) {
            return;
        }

        List<QueryTableTree> newChildren = new ArrayList<>();
        Iterator<QueryTableTree> parentChildIterator = rootTree.getChildren().iterator();
        while (parentChildIterator.hasNext()){
            QueryTableTree parentChild = parentChildIterator.next();
            if (parentChild.getName() == null && CollectionUtils.isEmpty(parentChild.getColumns())) {
                if(CollectionUtils.isNotEmpty(parentChild.getChildren())){
                    Iterator<QueryTableTree> iterator = parentChild.getChildren().iterator();
                    while (iterator.hasNext()){
                        this.moveUpNullNode(iterator.next());
                    }
                }

                newChildren.addAll(parentChild.getChildren());
                parentChildIterator.remove();
            } else {
                moveUpNullNode(parentChild);
            }
        }

        for (QueryTableTree newChild : newChildren) {
            newChild.setParent(rootTree);
            rootTree.addChild(newChild);
        }
    }

    private void trimCols(List<ColumnLineage> columnLineages){
        for (ColumnLineage columnLineage : columnLineages) {
            if(columnLineage.getToColumn().contains(SPLIT_DOT)){
                String toCol = columnLineage.getToColumn();
                toCol = toCol.substring(toCol.lastIndexOf(SPLIT_DOT)+1);
                columnLineage.setToColumn(toCol);
            }

            if(columnLineage.getFromColumn().contains(SPLIT_DOT)){
                String fromCol = columnLineage.getFromColumn();
                fromCol = fromCol.substring(fromCol.lastIndexOf(SPLIT_DOT)+1);
                columnLineage.setFromColumn(fromCol);
            }
        }
    }

    private List<ColumnLineage> getColumnLineage(QueryTableTree leafNode){
        if (null == leafNode) {
            return new ArrayList<>();
        }
        List<ColumnNode> columnNodes = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(leafNode.getColumns())) {
            int ind = 0;
            for (SelectColumn column : leafNode.getColumns()) {
                ColumnNode columnNode = new ColumnNode();
                String name = leafNode.getName();
                String alias = leafNode.getAlias();
                if(StringUtils.isBlank(name) && CollectionUtils.isNotEmpty(leafNode.getChildren())){
                    for (QueryTableTree child : leafNode.getChildren()) {
                        if(StringUtils.isNotBlank(child.getAlias()) && column.getName().contains(child.getAlias())){
                            name = child.getName();
                            alias = child.getAlias();
                        }
                    }
                }
                getDbAndTable(columnNode, name);
                columnNode.setTableAlias(alias);
                columnNode.setColumn(KeywordsHelper.removeKeywordsSuffix(column.getName()));
                columnNode.setColumnAlias(column.getAlias());

                columnNode = findColumnParent(columnNode, leafNode.getParent(),ind);
                if (columnNode != null) {
                    columnNodes.add(columnNode);
                }
                ind ++;
            }
        }

        // columnNode to columnLineage
        List<ColumnLineage> lineages = new ArrayList<>();
        for (ColumnNode columnNode : columnNodes) {
            lineages.add(getColumnLineage(columnNode));
        }

        return lineages;
    }

    private ColumnLineage getColumnLineage(ColumnNode columnNode){
        if(null == columnNode){
            return new ColumnLineage();
        }
        ColumnLineage lineage = new ColumnLineage();
        lineage.setFromDb(columnNode.getDb());
        lineage.setFromTable(columnNode.getTableName());
        String tableName = columnNode.getTableName();
        String tableAlias = columnNode.getTableAlias();
        String columnName = columnNode.getColumn();
        if (StringUtils.isNotEmpty(tableAlias)){
            if (columnName.startsWith(tableAlias+SPLIT_DOT)){
                columnName = columnName.substring(tableAlias.length()+1,columnName.length());
            }
        }
        lineage.setFromColumn(columnName);

        ColumnNode currentNode = columnNode;
        while (true){
            ColumnNode parentColumn = currentNode.getParent();
            if(parentColumn == null){
                break;
            }
            currentNode = parentColumn;
        }

        lineage.setToDb(currentNode.getDb());
        lineage.setToTable(currentNode.getTableName());
        lineage.setToColumn(currentNode.getColumn());
        return lineage;
    }

    private ColumnNode findColumnParent(ColumnNode columnNode, QueryTableTree leafNode,int ind){
        if(null == columnNode){
            return null;
        }
        if(null == leafNode){
            return null;
        }
        if(isSelectAllColumn(leafNode,columnNode.getTableAlias())){
            ColumnNode parentColumn = new ColumnNode();
            String name = leafNode.getName();
            if(StringUtils.isEmpty(name)){
                name = this.getParentName(leafNode);
            }
            getDbAndTable(parentColumn, name);
            parentColumn.setTableAlias(leafNode.getAlias());
            parentColumn.setColumn(KeywordsHelper.removeKeywordsSuffix(columnNode.column));
            parentColumn.setColumnAlias(columnNode.columnAlias);

            columnNode.setParent(parentColumn);
        } else {
            ColumnNode parentColumn = null;
            for (SelectColumn column : leafNode.getColumns()) {
                if(isNameEquals(columnNode.getColumnAlias(), KeywordsHelper.removeKeywordsSuffix(column.getName()))){
                    parentColumn = new ColumnNode();
                    getDbAndTable(parentColumn, leafNode.getName());
                    parentColumn.setTableAlias(leafNode.getAlias());
                    parentColumn.setColumn(KeywordsHelper.removeKeywordsSuffix(column.getName()));
                    parentColumn.setColumnAlias(column.getAlias());

                    columnNode.setParent(parentColumn);
                    break;
                }
            }

            if(parentColumn == null){
                logger.info("通过index查找血缘");
                //通过ind查找
                if (CollectionUtils.isEmpty(leafNode.getColumns())){
                    return  null;
                }
                if (ind >= leafNode.getColumns().size()){
                    return null;
                }
                SelectColumn selectColumn = leafNode.getColumns().get(ind);

                parentColumn = new ColumnNode();
                getDbAndTable(parentColumn, leafNode.getName());
                parentColumn.setTableAlias(leafNode.getAlias());
                parentColumn.setColumn(KeywordsHelper.removeKeywordsSuffix(selectColumn.getName()));
                parentColumn.setColumnAlias(selectColumn.getAlias());

                columnNode.setParent(parentColumn);
            }
        }

        if (leafNode.getParent() != null){
            findColumnParent(columnNode.getParent(), leafNode.getParent(),ind);
        }

        return columnNode;
    }

    private String getColumnNameWithTableAlias(ColumnNode columnNode){
        if (columnNode.getTableAlias() == null) {
            return columnNode.getColumnAlias();
        }
        if(columnNode.getColumnAlias().startsWith(columnNode.getTableAlias()+SPLIT_DOT)){
            return columnNode.getColumnAlias();
        }
        return columnNode.getTableAlias() + SPLIT_DOT + columnNode.getColumnAlias();
    }

    private void getDbAndTable(ColumnNode columnNode, String name){
        if(StringUtils.isEmpty(name)){
            return;
        }

        if(name.contains(SPLIT_DOT)){
            String[] dbName = name.split("\\.");
            columnNode.setDb(dbName[0]);
            columnNode.setTableName(dbName[1]);
        } else {
            columnNode.setTableName(name);
        }
    }

    private List<QueryTableTree> findLeafNode(QueryTableTree rootTree) {
        if(null==rootTree){
            return new ArrayList<>();
        }
        List<QueryTableTree> leafNodes = new ArrayList<>();
        if (CollectionUtils.isEmpty(rootTree.getChildren())){
            leafNodes.add(rootTree);
            return leafNodes;
        }

        for (QueryTableTree child : rootTree.getChildren()) {
            leafNodes.addAll(findLeafNode(child));
        }

        return leafNodes;
    }


    private String getParentName(QueryTableTree tableTree) {
        if (tableTree.getName() != null) {
            return tableTree.getName();
        } else if (tableTree.getParent() != null) {
            return this.getParentName(tableTree.getParent());
        }
        return null;
    }

    private void fillParentNode(QueryTableTree rootTree){
        if(null==rootTree){
            return;
        }
        if(CollectionUtils.isEmpty(rootTree.getChildren())){
            return;
        }

        for (QueryTableTree child : rootTree.getChildren()) {
            child.setParent(rootTree);
            fillParentNode(child);
        }
    }

    private boolean isSelectAllColumn(QueryTableTree rootTree,String tableAlias){
        if(null == rootTree){
            return false;
        }
        if(CollectionUtils.isEmpty(rootTree.getColumns())){
            return true;
        }

        for (SelectColumn selectColumn : rootTree.getColumns()){
            if ((tableAlias+".*").equals(selectColumn.getName())){
                return true;
            }
        }

        return false;
    }

    private boolean isNameEquals(String srcName,String distName){
        //libra sql并不是所有子查询都必须有alias。
        if(srcName.contains(SPLIT_DOT)){
            srcName = srcName.split("\\.")[1];
        }

        if(distName.contains(SPLIT_DOT)){
            distName = distName.split("\\.")[1];
        }

        return srcName.equals(distName);
    }

    /**
     * 对查询树进行预处理
     *
     * @param root
     */
    public void pretreatment(QueryTableTree root) throws SQLException{
        fillMetaColumn(root);
    }

    /**
     * 从表的元数据中获取字段
     *
     * @param tableName
     * @return
     */
    public List<SelectColumn> getSelectColumn(String tableName){
        List<SelectColumn> selectColumns = new ArrayList<>();

        List<Column> metaColumns = tableColumnMap.get(tableName);
        if(CollectionUtils.isNotEmpty(metaColumns)){
            for (Column metaColumn : metaColumns) {
                selectColumns.add(new SelectColumn(metaColumn.getName(), null));
            }
        }

        return selectColumns;
    }

    /**
     * 如果叶子节点的表没有指定字段，或者字段为 * ,需要填充字段信息
     */
    private void fillMetaColumn(QueryTableTree root){
        if(CollectionUtils.isNotEmpty(root.getChildren())){
            for (QueryTableTree child : root.getChildren()) {
                fillMetaColumn(child);
            }
        }

        // name为空表示为临时查询
        if(root.getName() == null){
            return;
        }

        if(CollectionUtils.isEmpty(root.getColumns())){
            root.setColumns(getSelectColumn(root.getName()));
            return;
        }

        List<SelectColumn> columns = new ArrayList<>();
        for (SelectColumn column : root.getColumns()) {
            if (column.getName().contains("*")){
                columns.addAll(getSelectColumn(root.getName()));
            } else {
                columns.add(column);
            }
        }
        root.setColumns(columns);
    }

    class ColumnNode {
        private String db;
        private String tableName;
        private String tableAlias;
        private String column;
        private String columnAlias;

        private ColumnNode parent;

        public String getTableAlias() {
            return tableAlias;
        }

        public void setTableAlias(String tableAlias) {
            this.tableAlias = tableAlias;
        }

        public String getColumnAlias() {
            return columnAlias;
        }

        public void setColumnAlias(String columnAlias) {
            this.columnAlias = columnAlias;
        }

        public String getDb() {
            return db;
        }

        public void setDb(String db) {
            this.db = db;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getColumn() {
            return column;
        }

        public void setColumn(String column) {
            this.column = column;
        }

        public ColumnNode getParent() {
            return parent;
        }

        public void setParent(ColumnNode parent) {
            this.parent = parent;
        }
    }
}
