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

import http from './http';
import req from './reqStream';

export default {
	// 获取类型数据源
	getTypeOriginData(params: any) {
		return http.post(req.GET_TYPE_ORIGIN_DATA, params);
	},
	listTablesBySchema(params: any) {
		return http.post(req.LIST_TABLE_BY_SCHEMA, params);
	},
	// 获取kafka topic预览数据
	getDataPreview(params: any) {
		return http.post(req.GET_DATA_PREVIEW, params);
	},
	pollPreview(params: any) {
		return http.post(req.POLL_PREVIEW, params);
	},
	// 添加或更新任务
	saveTask(params: any) {
		return http.post(req.SAVE_TASK, params);
	},
	getTask(params: any) {
		return http.post(req.GET_TASK, params).then((res) => {
			res.data = typeof res.data === 'string' ? JSON.parse(res.data) : res.data;
			if (res.data.taskVersionsStr) {
				res.data.taskVersions = JSON.parse(res.data.taskVersionsStr);
			}
			if (res.data.sideStr) {
				res.data.side = JSON.parse(res.data.sideStr);
			}
			if (res.data.sinkStr) {
				res.data.sink = JSON.parse(res.data.sinkStr);
			}
			if (res.data.sourceStr) {
				res.data.source = JSON.parse(res.data.sourceStr);
			}
			return Promise.resolve(res);
		});
	},
	// 获取Topic
	getTopicType(params: any) {
		return http.post(req.GET_TOPIC_TYPE, params);
	},
	getStreamTableColumn(params: {
		schema: string;
		sourceId: number;
		tableName: string;
		flinkVersion: string;
	}) {
		return http.post(req.GET_STREAM_TABLECOLUMN, params);
	},
	// 获取源表中的时区列表
	getTimeZoneList(params?: any) {
		return http.post(req.GET_TIMEZONE_LIST, params);
	},
	// 转换向导到脚本模式
	convertToScriptMode(params: any) {
		return http.post(req.CONVERT_TO_SCRIPT_MODE, params);
	},
	// TODO: 语法检查
	checkSyntax(data: any) {
		return new Promise<any>((resolve) =>
			resolve({
				code: 1,
				data: {
					code: 999,
					errorMsg:
						"org.apache.flink.client.program.ProgramInvocationException: The main method caused an error: com.dtstack.flinkx.throwable.DtSqlParserException: \n----------sql start---------\n1>    \n2>    CREATE TABLE test(\n3>        id INT,\n4>        name VARCHAR,\n5>        age INT,\n6>        chinese_score DOUBLE,\n7>        math_score DOUBLE,\n8>        english_score DOUBLE,\n9>        integrated_score DOUBLE,\n10>       integrated VARCHAR,\n11>       target_colleges VARCHAR,\n12>       dt VARCHAR\n13>    )WITH(\n14>       'password'='DT@Stack#123',\n15>       'connector'='mysql-x',\n16>       'sink.buffer-flush.interval'='1000',\n17>       'sink.all-replace'='false',\n18>       'sink.buffer-flush.max-rows'='100',\n19>       'table-name'='aaa_xy0307_1547',\n20>       'sink.parallelism'='1',\n21>       'url'='jdbc:mysql://172.16.23.23:3306/test',\n22>       'username'='drpeco'\n23>    )\n\n----------sql end--------- \n\nCould not execute CreateTable in path `default_catalog`.`default_database`.`test`\norg.apache.flink.client.program.ProgramInvocationException: The main method caused an error: com.dtstack.flinkx.throwable.DtSqlParserException: \n----------sql start---------\n1>    \n2>    CREATE TABLE test(\n3>        id INT,\n4>        name VARCHAR,\n5>        age INT,\n6>        chinese_score DOUBLE,\n7>        math_score DOUBLE,\n8>        english_score DOUBLE,\n9>        integrated_score DOUBLE,\n10>       integrated VARCHAR,\n11>       target_colleges VARCHAR,\n12>       dt VARCHAR\n13>    )WITH(\n14>       'password'='DT@Stack#123',\n15>       'connector'='mysql-x',\n16>       'sink.buffer-flush.interval'='1000',\n17>       'sink.all-replace'='false',\n18>       'sink.buffer-flush.max-rows'='100',\n19>       'table-name'='aaa_xy0307_1547',\n20>       'sink.parallelism'='1',\n21>       'url'='jdbc:mysql://172.16.23.23:3306/test',\n22>       'username'='drpeco'\n23>    )\n\n----------sql end--------- \n\nCould not execute CreateTable in path `default_catalog`.`default_database`.`test`\n\tat org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:371)\n\tat org.apache.flink.client.program.PackagedProgram.invokeInteractiveModeForExecution(PackagedProgram.java:224)\n\tat org.apache.flink.client.program.PackagedProgramUtils.getPipelineFromProgram(PackagedProgramUtils.java:158)\n\tat org.apache.flink.client.program.PackagedProgramUtils.createJobGraph(PackagedProgramUtils.java:82)\n\tat org.apache.flink.client.program.PackagedProgramUtils.createJobGraph(PackagedProgramUtils.java:117)\n\tat com.dtstack.engine.flink.FlinkClient.grammarCheck(FlinkClient.java:1119)\n\tat com.dtstack.engine.common.client.ClientProxy.lambda$null$21(ClientProxy.java:373)\n\tat com.dtstack.engine.common.callback.ClassLoaderCallBackMethod.callbackAndReset(ClassLoaderCallBackMethod.java:13)\n\tat com.dtstack.engine.common.client.ClientProxy.lambda$grammarCheck$22(ClientProxy.java:373)\n\tat java.util.concurrent.CompletableFuture$AsyncSupply.run(CompletableFuture.java:1604)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n\tat java.lang.Thread.run(Thread.java:748)\nCaused by: com.dtstack.flinkx.throwable.FlinkxRuntimeException: com.dtstack.flinkx.throwable.DtSqlParserException: \n----------sql start---------\n1>    \n2>    CREATE TABLE test(\n3>        id INT,\n4>        name VARCHAR,\n5>        age INT,\n6>        chinese_score DOUBLE,\n7>        math_score DOUBLE,\n8>        english_score DOUBLE,\n9>        integrated_score DOUBLE,\n10>       integrated VARCHAR,\n11>       target_colleges VARCHAR,\n12>       dt VARCHAR\n13>    )WITH(\n14>       'password'='DT@Stack#123',\n15>       'connector'='mysql-x',\n16>       'sink.buffer-flush.interval'='1000',\n17>       'sink.all-replace'='false',\n18>       'sink.buffer-flush.max-rows'='100',\n19>       'table-name'='aaa_xy0307_1547',\n20>       'sink.parallelism'='1',\n21>       'url'='jdbc:mysql://172.16.23.23:3306/test',\n22>       'username'='drpeco'\n23>    )\n\n----------sql end--------- \n\nCould not execute CreateTable in path `default_catalog`.`default_database`.`test`\n\tat com.dtstack.flinkx.Main.exeSqlJob(Main.java:149)\n\tat com.dtstack.flinkx.Main.main(Main.java:108)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)\n\tat sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)\n\tat sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)\n\tat java.lang.reflect.Method.invoke(Method.java:498)\n\tat org.apache.flink.client.program.PackagedProgram.callMainMethod(PackagedProgram.java:354)\n\t... 12 more\nCaused by: com.dtstack.flinkx.throwable.DtSqlParserException: \n----------sql start---------\n1>    \n2>    CREATE TABLE test(\n3>        id INT,\n4>        name VARCHAR,\n5>        age INT,\n6>        chinese_score DOUBLE,\n7>        math_score DOUBLE,\n8>        english_score DOUBLE,\n9>        integrated_score DOUBLE,\n10>       integrated VARCHAR,\n11>       target_colleges VARCHAR,\n12>       dt VARCHAR\n13>    )WITH(\n14>       'password'='DT@Stack#123',\n15>       'connector'='mysql-x',\n16>       'sink.buffer-flush.interval'='1000',\n17>       'sink.all-replace'='false',\n18>       'sink.buffer-flush.max-rows'='100',\n19>       'table-name'='aaa_xy0307_1547',\n20>       'sink.parallelism'='1',\n21>       'url'='jdbc:mysql://172.16.23.23:3306/test',\n22>       'username'='drpeco'\n23>    )\n\n----------sql end--------- \n\nCould not execute CreateTable in path `default_catalog`.`default_database`.`test`\n\tat com.dtstack.flinkx.sql.parser.SqlParser.lambda$parseSql$1(SqlParser.java:71)\n\tat java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)\n\tat java.util.stream.ReferencePipeline$2$1.accept(ReferencePipeline.java:175)\n\tat java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1384)\n\tat java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)\n\tat java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:472)\n\tat java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)\n\tat java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)\n\tat java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)\n\tat java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)\n\tat com.dtstack.flinkx.sql.parser.SqlParser.parseSql(SqlParser.java:65)\n\tat com.dtstack.flinkx.Main.exeSqlJob(Main.java:140)\n\t... 18 more\nCaused by: org.apache.flink.table.api.ValidationException: Could not execute CreateTable in path `default_catalog`.`default_database`.`test`\n\tat org.apache.flink.table.catalog.CatalogManager.execute(CatalogManager.java:794)\n\tat org.apache.flink.table.catalog.CatalogManager.createTable(CatalogManager.java:632)\n\tat org.apache.flink.table.api.internal.TableEnvironmentImpl.executeOperation(TableEnvironmentImpl.java:776)\n\tat org.apache.flink.table.api.internal.TableEnvironmentImpl.executeSql(TableEnvironmentImpl.java:666)\n\tat com.dtstack.flinkx.sql.parser.AbstractStmtParser.handleStmt(AbstractStmtParser.java:55)\n\tat com.dtstack.flinkx.sql.parser.AbstractStmtParser.handleStmt(AbstractStmtParser.java:52)\n\tat com.dtstack.flinkx.sql.parser.AbstractStmtParser.handleStmt(AbstractStmtParser.java:52)\n\tat com.dtstack.flinkx.sql.parser.SqlParser.lambda$parseSql$1(SqlParser.java:68)\n\t... 29 more\nCaused by: org.apache.flink.table.catalog.exceptions.TableAlreadyExistException: Table (or view) default_database.test already exists in Catalog default_catalog.\n\tat org.apache.flink.table.catalog.GenericInMemoryCatalog.createTable(GenericInMemoryCatalog.java:220)\n\tat org.apache.flink.table.catalog.CatalogManager.lambda$createTable$10(CatalogManager.java:633)\n\tat org.apache.flink.table.catalog.CatalogManager.execute(CatalogManager.java:790)\n\t... 36 more\n",
				},
			}),
		);
		// return http.post(, params);
	},
};
