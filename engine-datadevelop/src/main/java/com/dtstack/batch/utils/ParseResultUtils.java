package com.dtstack.batch.utils;

import com.dtstack.batch.bo.ParseResult;
import com.dtstack.engine.lineage.adapter.AlterResultAdapter;
import com.dtstack.engine.lineage.adapter.SqlTypeAdapter;
import com.dtstack.engine.lineage.adapter.TableAdapter;

/**
 * @author beihai
 * @Description parseResult
 * @Date 2021/4/1 19:24
 */
public class ParseResultUtils {

    public static ParseResult convertParseResult(com.dtstack.sqlparser.common.client.domain.ParseResult originResult) {
        ParseResult parseResult = new ParseResult();
        parseResult.setMainDb(originResult.getCurrentDb());
        parseResult.setMainTable(TableAdapter.sqlTable2ApiTable(originResult.getMainTable()));
        parseResult.setParseSuccess(originResult.isParseSuccess());
        parseResult.setFailedMsg(originResult.getFailedMsg());
        parseResult.setStandardSql(originResult.getStandardSql());
        parseResult.setOriginSql(originResult.getOriginSql());
        parseResult.setSqlType(SqlTypeAdapter.sqlType2ApiSqlType(originResult.getSqlType()));
        parseResult.setExtraType(SqlTypeAdapter.sqlType2ApiSqlType(originResult.getExtraSqlType()));
        parseResult.setCurrentDb(originResult.getCurrentDb());
        parseResult.setAlterResult(AlterResultAdapter.sqlAlterResult2ApiResult(originResult.getAlterResult()));
        return parseResult;
    }

}
