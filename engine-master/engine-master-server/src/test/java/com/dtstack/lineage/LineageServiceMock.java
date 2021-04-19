//package com.dtstack.lineage;/**
// * @author chenfeixiang6@163.com
// * @date 2021/4/16
// */
//
//import com.alibaba.testable.core.annotation.MockMethod;
//import com.dtstack.sqlparser.common.client.ISqlParserClient;
//import com.dtstack.sqlparser.common.client.SqlParserClientCache;
//import com.dtstack.sqlparser.common.client.domain.Column;
//import com.dtstack.sqlparser.common.client.domain.ParseResult;
//import com.dtstack.sqlparser.common.client.domain.Table;
//import com.dtstack.sqlparser.common.client.enums.ETableType;
//
//import java.lang.reflect.Constructor;
//import java.util.List;
//import java.util.Map;
//
///**
// *类名称:LineageServiceMock
// *类描述:TODO
// *创建人:newman
// *创建时间:2021/4/16 5:03 下午
// *Version 1.0
// */
//public class LineageServiceMock {
//
//
//    @MockMethod(targetClass = SqlParserClientCache.class)
//    public SqlParserClientCache getInstance() throws Exception {
//        Constructor<SqlParserClientCache> constructor = SqlParserClientCache.class.getDeclaredConstructor();
//        constructor.setAccessible(true);
//        return constructor.newInstance();
//    }
//
//    @MockMethod(targetClass = SqlParserClientCache.class)
//    public ISqlParserClient getClient(String name) {
//        return new ISqlParserClient() {
//            @Override
//            public ParseResult parseSql(String s, String s1, Map<String, List<Column>> map, ETableType eTableType) throws Exception {
//                return null;
//            }
//
//            @Override
//            public List<Table> parseTables(String s, String s1, ETableType eTableType) throws Exception {
//                return null;
//            }
//
//            @Override
//            public ParseResult parseTableLineage(String s, String s1, ETableType eTableType) throws Exception {
//                return null;
//            }
//
//            @Override
//            public ParseResult getSqlParserNode(String s, String s1, Map<String, List<Column>> map, ETableType eTableType) throws Exception {
//                return null;
//            }
//        };
//
//    }
//
//
//}
//
//
