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


package com.dtstack.engine.sql.hive.hive;

import com.dtstack.engine.sql.AlterColumnResult;
import com.dtstack.engine.sql.AlterResult;
import com.dtstack.engine.sql.Column;
import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.PartCondition;
import com.dtstack.engine.sql.Partition;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.TableOperateEnum;
import com.dtstack.engine.sql.hive.ASTNodeUtil;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.hadoop.hive.ql.lib.Node;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;
import org.apache.hadoop.hive.ql.parse.ParseUtils;
import org.apache.hadoop.hive.serde2.typeinfo.CharTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.DecimalTypeInfo;
import org.apache.hadoop.hive.serde2.typeinfo.VarcharTypeInfo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 解析 alter语句
 *
 * @author jiangbo
 * @date 2019/5/23
 */
public class AlterAstNodeParser extends BaseAstNodeSqlParser {

    private static final String FIRST_REGEX = "(?i)first";

    private AlterResult alterResult;

    @Override
    public void parseNode(ASTNode root, ParseResult parseResult) throws SQLException {
        result = parseResult;
        mainTable = result.getMainTable();
        alterResult = new AlterResult();

        setTableAndDb(parseResult,root);
        parseResult.setSqlType(SqlType.ALTER);
        if(ASTNodeUtil.contains(root, HiveParser.TOK_ALTERTABLE_RENAME)){
            renameTable(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_PROPERTIES)){
            alterProperty(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_SERDEPROPERTIES)){
            alterSerder(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_ADDPARTS)){
            addPartition(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_RENAMEPART)){
            renamePartition(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_DROPPARTS)){
            dropPartition(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_LOCATION)){
            alterPath(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_RENAMECOL)){
            alterColumn(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_ADDCOLS)){
            addColumn(root);
        } else if(ASTNodeUtil.contains(root,HiveParser.TOK_ALTERTABLE_REPLACECOLS)){
            replaceColumn(root);
        }

        parseResult.setAlterResult(alterResult);
    }

    /**
     * 替换字段
     * @param root
     */
    private void replaceColumn(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_REPLACECOLS);
        // 不支持
    }

    /**
     * 添加字段
     * @param root
     */
    private void addColumn(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_ADDCOLS);
        // 不支持
    }

    /**
     * 重命名字段/类型修改
     * @param root
     */
    private void alterColumn(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_RENAMECOL);

        ASTNode renameColNode = ASTNodeUtil.getNode(root,HiveParser.TOK_ALTERTABLE_RENAMECOL);

        AlterColumnResult alterColumnResult = new AlterColumnResult();
        alterColumnResult.setOldColumn(renameColNode.getChild(0).getText());
        alterColumnResult.setNewColumn(renameColNode.getChild(1).getText());
        alterColumnResult.setNewType(renameColNode.getChild(2).getText());

        if(renameColNode.getChildren().size() == 4){
            ASTNode node = (ASTNode) renameColNode.getChild(3);
            if(node.getText().matches(FIRST_REGEX)){
                alterColumnResult.setFirst(true);
            } else if(node.getToken().getType() == HiveParser.TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION){
                alterColumnResult.setFirst(false);
                alterColumnResult.setAfterColumn(node.getChild(0).getText());
            } else {
                alterColumnResult.setNewComment(node.getText());
            }
        }

        if(renameColNode.getChildren().size() == 5){
            alterColumnResult.setNewComment(renameColNode.getChild(3).getText());
            ASTNode node = (ASTNode) renameColNode.getChild(4);
            if(node.getText().matches(FIRST_REGEX)){
                alterColumnResult.setFirst(true);
            } else if(node.getToken().getType() == HiveParser.TOK_ALTERTABLE_CHANGECOL_AFTER_POSITION){
                alterColumnResult.setFirst(false);
                alterColumnResult.setAfterColumn(node.getChild(0).getText());
            }
        }
        alterResult.setAlterColumnResult(alterColumnResult);
    }

    /**
     * 修改表/分区路径
     * @param root
     */
    private void alterPath(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_LOCATION);
        if(ASTNodeUtil.contains(root,HiveParser.TOK_PARTSPEC)){
            ASTNode partSpecNode = ASTNodeUtil.getNode(root,HiveParser.TOK_PARTSPEC);
            List<Pair<String,String>> pairs = getPart(partSpecNode);
            alterResult.setNewLocationPart(pairs.get(0));
        }

        List<String> values = ASTNodeUtil.getNodeValue(ASTNodeUtil.getNode(root,HiveParser.TOK_ALTERTABLE_LOCATION));
        alterResult.setNewLocation(values.get(0));
    }

    /**
     * 删除分区
     * @param root
     */
    private void dropPartition(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_DROPPARTS);
        List<String> values;
        List<PartCondition> partConditions = Lists.newArrayList();
        List<ASTNode> nodes = ASTNodeUtil.getNodes(root,HiveParser.TOK_PARTVAL);
        for (ASTNode node : nodes) {
            values = ASTNodeUtil.getNodeValue(node);
            partConditions.add(new PartCondition(values.get(0),values.get(1),values.get(2)));
        }
        alterResult.setDropParts(partConditions);
    }

    /**
     * 重命名分区
     * @param root
     */
    private void renamePartition(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_RENAMEPART);
        ASTNode partSpecNode = ASTNodeUtil.getNode(root,HiveParser.TOK_PARTSPEC);
        alterResult.setOldPart(getPart(partSpecNode).get(0));

        partSpecNode = ASTNodeUtil.getNode(root,HiveParser.TOK_ALTERTABLE_RENAMEPART);
        partSpecNode = ASTNodeUtil.getNode(partSpecNode,HiveParser.TOK_PARTSPEC);
        alterResult.setNewPart(getPart(partSpecNode).get(0));
    }

    /**
     * 添加分区
     * @param root
     */
    private void addPartition(ASTNode root) {
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_ADDPARTS);
        List<Partition> newPartitions = Lists.newArrayList();
        ASTNode addPartsNode = ASTNodeUtil.getNode(root,HiveParser.TOK_ALTERTABLE_ADDPARTS);
        int size = addPartsNode.getChildCount();
        List<String> values;
        Partition partition;
        List<Pair<String,String>> partVals;
        for (int i = 0; i < size; i++) {
            if(((ASTNode)addPartsNode.getChild(i)).getToken().getType() == HiveParser.TOK_PARTITIONLOCATION){
                continue;
            }

            partition = new Partition();
            partVals = getPart((ASTNode)addPartsNode.getChild(i));
            partition.setPartKeyValues(partVals);

            if(i + 1 < size && ((ASTNode)addPartsNode.getChild(i+1)).getToken().getType() == HiveParser.TOK_PARTITIONLOCATION){
                values = ASTNodeUtil.getNodeValue((ASTNode)addPartsNode.getChild(i+1));
                partition.setPartLocalion(values.get(0));
            } else {
                partition.setPartLocalion(concatPart(partVals));
            }
            newPartitions.add(partition);
        }
        alterResult.setNewPartitions(newPartitions);
    }

    /**
     * 重命名表
     * @param root
     */
    private void renameTable(ASTNode root){
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_RENAME);
        // 重命名表
        Map<String,String> tableDb = ASTNodeUtil.getTableNameAndDbName(root);
        alterResult.setOldTableName(tableDb.get(ASTNodeUtil.TABLE_NAME_KEY));

        ASTNode renameNode = ASTNodeUtil.getNode(root,HiveParser.TOK_ALTERTABLE_RENAME);
        tableDb = ASTNodeUtil.getTableNameAndDbName(renameNode);
        alterResult.setNewTableName(tableDb.get(ASTNodeUtil.TABLE_NAME_KEY));
    }

    /**
     * 修改表属性
     * @param root
     */
    private void alterProperty(ASTNode root){
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_PROPERTIES);
        List<ASTNode> propertyNodes = ASTNodeUtil.getNodes(root,HiveParser.TOK_TABLEPROPERTY);
        if(CollectionUtils.isNotEmpty(propertyNodes)){
            alterResult.setTableProperties(getProperties(propertyNodes));
        }
    }

    /**
     * 修改序列化信息
     * @param root
     */
    private void alterSerder(ASTNode root){
        alterResult.setAlterType(TableOperateEnum.ALTERTABLE_SERDEPROPERTIES);
        List<ASTNode> propertyNodes = ASTNodeUtil.getNodes(root,HiveParser.TOK_TABLEPROPERTY);
        if(CollectionUtils.isNotEmpty(propertyNodes)){
            alterResult.setSerdeProperties(getProperties(propertyNodes));
        }
    }

    private List<Pair<String, String>> getProperties(List<ASTNode> propertyNodes) {
        List<Pair<String,String>> properties = Lists.newArrayList();
        List<String> values;
        for (ASTNode propertyNode : propertyNodes) {
            values = ASTNodeUtil.getNodeValue(propertyNode);
            properties.add(new Pair<>(values.get(0),values.get(1)));
        }

        return properties;
    }

    private List<Pair<String,String>> getPart(ASTNode partSpecNode){
        List<Pair<String,String>> parts = Lists.newArrayList();
        List<ASTNode> partNodes = ASTNodeUtil.getNodes(partSpecNode,HiveParser.TOK_PARTVAL);
        List<String> values;
        for (ASTNode partNode : partNodes) {
            values = ASTNodeUtil.getNodeValue(partNode);
            parts.add(new Pair<>(values.get(0),values.get(1)));
        }
        return parts;
    }

    private List<Column> getColumns(ASTNode tableColsNode) throws Exception{
        if(CollectionUtils.isEmpty(tableColsNode.getChildren())){
            return null;
        }
        List<Column> columns = new ArrayList<>();
        Column column;
        ArrayList<Node> nodes;
        String colType;
        String comment;
        String name;
        int index = 0;
        for (Node node : tableColsNode.getChildren()) {
            column = new Column();
            nodes = ((ASTNode) node).getChildren();
            name = ((ASTNode)nodes.get(0)).getToken().getText();
            if(name.matches("^[`'\"].*")){
                name = name.substring(1,name.length()-1);
            }
            column.setName(name);

            colType = ((ASTNode)nodes.get(1)).getToken().getText();
            if("TOK_DECIMAL".equals(colType)){
                DecimalTypeInfo decimalTypeInfo = ParseUtils.getDecimalTypeTypeInfo((ASTNode)nodes.get(1));
                colType = decimalTypeInfo.getTypeName();
            } else if("TOK_VARCHAR".equals(colType)){
                VarcharTypeInfo varcharTypeInfo = ParseUtils.getVarcharTypeInfo((ASTNode)nodes.get(1));
                colType = varcharTypeInfo.getTypeName();
            } else if("TOK_CHAR".equals(colType)){
                CharTypeInfo charTypeInfo = ParseUtils.getCharTypeInfo((ASTNode)nodes.get(1));
                colType = charTypeInfo.getTypeName();
            }
            column.setType(colType.startsWith("TOK_") ? colType.substring(4) : colType);

            if(nodes.size() == 3){
                comment = ((ASTNode)nodes.get(2)).getToken().getText();
                if(comment.matches("^['\"].*")){
                    comment = comment.substring(1,comment.length()-1);
                }
                column.setComment(comment);
            }

            column.setIndex(index++);
            columns.add(column);
        }
        return columns;
    }

    /**
     * 组合分区信息为路径格式
     *
     * @param parts 分区键值对
     * @return /part1=123/part2=123
     */
    private String concatPart(List<Pair<String,String>> parts){
        List<String> partStr = Lists.newArrayList();
        for (Pair part : parts) {
            partStr.add(part.getFirst() + "=" + part.getSecond());
        }
        return "/" + StringUtils.join(partStr,"/");
    }
}
