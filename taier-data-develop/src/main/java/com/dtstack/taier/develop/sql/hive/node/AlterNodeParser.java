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

package com.dtstack.taier.develop.sql.hive.node;

import com.dtstack.taier.develop.sql.AlterColumnResult;
import com.dtstack.taier.develop.sql.Column;
import com.dtstack.taier.develop.sql.PartCondition;
import com.dtstack.taier.develop.sql.Partition;
import com.dtstack.taier.develop.sql.Table;
import com.dtstack.taier.develop.sql.TableOperateEnum;
import com.dtstack.taier.develop.sql.node.AlterNode;
import com.dtstack.taier.develop.sql.node.Identifier;
import com.dtstack.taier.develop.sql.node.Node;
import org.apache.commons.math3.util.Pair;
import org.apache.hadoop.hive.ql.parse.ASTNode;
import org.apache.hadoop.hive.ql.parse.HiveParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlterNodeParser extends NodeParser {
    @Override
    public Node parseSql(ASTNode node, String defultDb, Map<String, List<Column>> tableColumnsMap, Map<String, String> aliasToTable) {
        AlterNode alterNode = new AlterNode(defultDb, tableColumnsMap);
        List<org.apache.hadoop.hive.ql.lib.Node> children = node.getChildren();
        for (org.apache.hadoop.hive.ql.lib.Node n : children) {
            if (((ASTNode) n).getType() == HiveParser.TOK_TABNAME) {
                Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                getTableName(identifier, (ASTNode) n, defultDb);
                alterNode.setSourceTable(identifier);
            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_RENAME) {
                Identifier identifier = new Identifier(defultDb, tableColumnsMap);
                getTableName(identifier, (ASTNode) ((ASTNode) n).getChild(0), defultDb);
                alterNode.setAlterType(TableOperateEnum.ALTERTABLE_RENAME);
                alterNode.setTargetTable(identifier);
            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_RENAMECOL) {
                List<org.apache.hadoop.hive.ql.lib.Node> columName = ((ASTNode) n).getChildren();
                AlterColumnResult alterColumnResult = new AlterColumnResult();
                alterColumnResult.setOldColumn(((ASTNode) columName.get(0)).getText());
                alterColumnResult.setNewColumn(((ASTNode) columName.get(1)).getText());
                alterColumnResult.setNewType(((ASTNode) columName.get(2)).getText().substring(4));
                //第四个子节点是after属性
                if (columName.size() > 3) {
                    ASTNode afterNode = (ASTNode) columName.get(3);
                    alterColumnResult.setAfterColumn(afterNode.getChild(0).getText());
                }
                alterNode.setAlterType(TableOperateEnum.ALTERTABLE_RENAMECOL);
                alterNode.setAlterColumnMap(alterColumnResult);
            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_ADDCOLS) {
                Column column = addColumn((ASTNode) ((ASTNode) n).getChild(0));
                alterNode.setNewColumns(Arrays.asList(column));
                alterNode.setAlterType(TableOperateEnum.ALTERTABLE_ADDCOLS);
            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_DROPPARTS) {
                List<org.apache.hadoop.hive.ql.lib.Node> partitionNode = ((ASTNode) n).getChildren();
                for (org.apache.hadoop.hive.ql.lib.Node partition : partitionNode) {
                    if (((ASTNode) partition).getType() == HiveParser.TOK_PARTSPEC) {
                        alterNode.setDropParts(Arrays.asList(getDelPartition((ASTNode) partition)));
                        alterNode.setAlterType(TableOperateEnum.ALTERTABLE_DROPPARTS);
                    }
                }

            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_ADDPARTS) {
                List<Partition> partitionList = new ArrayList<>();
                List<org.apache.hadoop.hive.ql.lib.Node> partitionChidren = ((ASTNode) n).getChildren();
                for (org.apache.hadoop.hive.ql.lib.Node par : partitionChidren) {
                    ASTNode astNode = (ASTNode) par;
                    if (astNode.getType() == HiveParser.TOK_PARTSPEC) {
                        Partition partition = getaddPartition(astNode);
                        partitionList.add(partition);
                    } else if (astNode.getType() == HiveParser.TOK_PARTITIONLOCATION) {
                        partitionList.get(partitionList.size() - 1).setPartLocalion(astNode.getChild(0).getText());
                    }
                }
                alterNode.setNewPartitions(partitionList);
                alterNode.setAlterType(TableOperateEnum.ALTERTABLE_ADDPARTS);
            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_PROPERTIES) {
                List<Pair<String, String>> pairList = new ArrayList<>();
                ASTNode tableProperties = (ASTNode) ((ASTNode) n).getChild(0);
                List<org.apache.hadoop.hive.ql.lib.Node> propertiesList = tableProperties.getChildren();
                for (org.apache.hadoop.hive.ql.lib.Node properties : propertiesList) {
                    if (((ASTNode) properties).getType() == HiveParser.TOK_TABLEPROPERTY) {
                        pairList.add(new Pair<>(((ASTNode) children.get(0)).getText(), ((ASTNode) children.get(1)).getText()));
                    }
                }
                alterNode.setTableProperties(pairList);
                alterNode.setAlterType(TableOperateEnum.ALTERTABLE_PROPERTIES);
            } else if (((ASTNode) n).getType() == HiveParser.TOK_ALTERTABLE_RENAMEPART) {
                List<Pair<String, String>> pairList = new ArrayList<>();
                ASTNode tableProperties = (ASTNode) ((ASTNode) n).getChild(0);
                List<org.apache.hadoop.hive.ql.lib.Node> propertiesList = tableProperties.getChildren();
                for (org.apache.hadoop.hive.ql.lib.Node properties : propertiesList) {
                    if (((ASTNode) properties).getType() == HiveParser.TOK_PARTVAL) {
                        pairList.add(new Pair<>(((ASTNode) children.get(0)).getText(), ((ASTNode) children.get(1)).getText()));
                    }
                }
                alterNode.setRenamePart(pairList);
                alterNode.setAlterType(TableOperateEnum.ALTERTABLE_RENAMEPART);
            }
        }
        if (null == alterNode.getTargetTable()) {
            alterNode.setTargetTable(alterNode.getSourceTable());
        }
        return alterNode;
    }

    @Override
    public void parseSqlTable(Node node, Set<Table> tables) {

    }

    /**
     * 获取新增加的column
     *
     * @param astNode
     */
    private Column addColumn(ASTNode astNode) {
        Column column = new Column();
        ASTNode addColumn = (ASTNode) astNode.getChild(0);
        if (addColumn.getChild(0).getType() == HiveParser.TOK_TABCOL) {
            List<org.apache.hadoop.hive.ql.lib.Node> children = ((ASTNode) addColumn.getChild(0)).getChildren();
            column.setName(((ASTNode) children.get(0)).getText());
            column.setType(((ASTNode) children.get(1)).getText().substring(4));
            column.setComment(((ASTNode) children.get(2)).getText());
        }
        return column;
    }

    /**
     * 获取被删除分区信息
     *
     * @param node
     * @return
     */
    private PartCondition getDelPartition(ASTNode node) {
        PartCondition partCondition = null;
        if (node.getType() == HiveParser.TOK_PARTVAL) {
            List<org.apache.hadoop.hive.ql.lib.Node> parData = node.getChildren();
            partCondition = new PartCondition(((ASTNode) parData.get(0)).getText(), ((ASTNode) parData.get(1)).getText(), ((ASTNode) parData.get(2)).getText());
        }

        return partCondition;
    }

    /**
     * 获取新增分区信息
     *
     * @param node
     * @return
     */
    private Partition getaddPartition(ASTNode node) {
        List<org.apache.hadoop.hive.ql.lib.Node> partsChildrens = node.getChildren();
        Partition partition = new Partition();
        List<Pair<String, String>> partKeyValues = new ArrayList<>();
        for (org.apache.hadoop.hive.ql.lib.Node par : partsChildrens) {
            List<org.apache.hadoop.hive.ql.lib.Node> parData = ((ASTNode) par).getChildren();
            partKeyValues.add(new Pair<>(((ASTNode) parData.get(0)).getText(), ((ASTNode) parData.get(1)).getText()));
        }
        partition.setPartKeyValues(partKeyValues);
        return partition;
    }
}
