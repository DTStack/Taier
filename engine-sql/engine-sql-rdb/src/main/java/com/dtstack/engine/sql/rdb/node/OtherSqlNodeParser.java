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


package com.dtstack.engine.sql.rdb.node;

import com.dtstack.engine.sql.ParseResult;
import com.dtstack.engine.sql.SqlType;
import com.dtstack.engine.sql.TableOperateEnum;
import org.apache.calcite.sql.SqlDelete;
import org.apache.calcite.sql.SqlDrop;
import org.apache.calcite.sql.SqlIdentifier;
import org.apache.calcite.sql.SqlKind;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlUpdate;
import org.apache.calcite.sql.ddl.SqlDropTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.List;

/**
 * 这个类用来解析其它操作类型，只需要解析出操作类型即可
 *
 * @author jiangbo
 * @date 2019/5/22
 */
public class OtherSqlNodeParser extends BaseSqlNodeParser {

    private static Logger LOGGER = LoggerFactory.getLogger(OtherSqlNodeParser.class);

    @Override
    public void parseSqlNode(SqlNode node, ParseResult parseResult) throws SQLException {
        result = parseResult;
        mainTable = result.getMainTable();

        SqlKind kind = node.getKind();
        switch (kind) {
            case DROP_TABLE:
                parseDropTable(node);
                break;
            case DELETE:
                parseDelete(node);
                break;
            case UPDATE:
                parseUpdate(node);
                break;
            default:
        }
    }

    private void parseUpdate(SqlNode node) throws SQLException {
        SqlUpdate update = (SqlUpdate) node;
        getDbTableFromIdentifier((SqlIdentifier) update.getTargetTable());

        mainTable.setOperate(TableOperateEnum.UPDATE);
        result.setSqlType(SqlType.UPDATE);
    }

    private void parseDelete(SqlNode node) throws SQLException {
        SqlDelete delete = (SqlDelete) node;
        getDbTableFromIdentifier((SqlIdentifier) delete.getTargetTable());

        mainTable.setOperate(TableOperateEnum.DELETE);
        result.setSqlType(SqlType.DELETE);
    }

    private void parseDropTable(SqlNode node) throws SQLException {
        List<SqlNode> operandList = ((SqlDropTable) node).getOperandList();
        getDbTableFromIdentifier((SqlIdentifier) operandList.get(0));

        boolean ifExists = false;
        try {
            Class<?> clazz = node.getClass();
            while (clazz != SqlDrop.class) {
                clazz = clazz.getSuperclass();
            }
            Field field = clazz.getDeclaredField("ifExists");
            field.setAccessible(true);
            ifExists = (boolean) field.get(node);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e.getStackTrace());
        }
        mainTable.setIgnore(ifExists);
        mainTable.setOperate(TableOperateEnum.DROP);
        result.setSqlType(SqlType.DROP);
    }
}
