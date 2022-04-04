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

import React from 'react';
import { HELP_DOC_URL } from '@/constant';

export const componentTips = (
	<div>选择组件后只可配置任务的输入参数与输出参数，SQL代码无法编辑。</div>
);

export const es7BulkAction = <p>bulkAction：批量写入记录数</p>;

export const es7Query = <p>query：查询表达式</p>;

export const indexTypeDoc = <p>type: 索引类型</p>;

export const es7Index = <p>index: 索引</p>;

export const incrementColumnHelp = (
	<div>
		每次同步时，自动记录增量标识的最大值，下次运行时，会从上一次的最大值继续同步数据，实现增量同步
		<br />
		支持将数值类型、Timestamp类型作为增量标识字段
	</div>
);

export const syncModeHelp = (
	<div>
		无增量标识：可通过简单的过滤语句实现增量同步；
		<br />
		有增量标识：系统记录每次同步的点位，执行时可从上次点位继续同步
	</div>
);

export const syncTaskHelp = (
	<div>
		向导模式：便捷、简单，可视化字段映射，快速完成同步任务配置
		<br />
		脚本模式：全能 高效，可深度调优，支持全部数据源
		<br />
	</div>
);

export const transTableConcurrence = <div>Inceptor事务表不支持多并发写数据</div>;

export const S3Concurrence = <div>AWS S3仅支持单通道的数据写入</div>; // s3 作业并发数

export const extraHive = (hadoopName: string) => (
	<div>
		<p>系统需要对接到 {hadoopName} 上，并使用 Hive Metastore 存储元数据。</p>
		<p>创建：建立一个新的 Database / Schema </p>
		<p>
			对接已有 Database /
			Schema，可将已有表导入本平台进行管理，原系统内的数据本身不会移动或改变，在导入进行过程中，请勿执行表结构变更操作。
		</p>
	</div>
);

export const dataFilterDoc = (
	<div>
		where 条件即针对源头数据筛选条件，根据指定的 column、table、where 条件拼接 SQL
		进行数据抽取，暂时不支持limit关键字过滤。利用 where
		条件可进行全量同步和增量同步，具体说明如下：
		<br />
		<ul>
			<li>1）全量同步：第一次做数据导入时通常为全量导入，可不用设置 where 条件。</li>
			<li>
				2）增量同步：增量导入在实际业务场景中，往往会选择当天的数据进行同步，通常需要编写
				where
				条件语句，请先确认表中描述增量字段（时间戳）为哪一个。如tableA增量的字段为create_time，则填写create_time{' '}
				{'>'} 您需要的日期，如果需要日期动态变化，请参考帮助文档。
			</li>
		</ul>
	</div>
);

export const selectKey = <div>MySQL、SQLServer、PostgreSQL、Oracle：支持数值型切分键</div>;

export const hdfsPath = (
	<div>
		user/hive/warehouse/projectName.db/
		<br />
		tableName 是RD-OS默认的HDFS文件组织方式，projectName为项目名，
		其中每个tableName是HDFS内的一个目录，储存着一张表的数据。
		如果此表的数据存储在当前项目空间内，只需修改tableName即可， 否则需要根据HDFS的存储位置填写。
	</div>
);

export const splitCharacter = <div>配置不可见字符，可通过“\”作为转义字符，例如\001</div>;

export const jobSpeedLimit = ( // 作业上限速度
	<div>
		设置作业速率上限，则数据同步作业的总速率将尽可能按照这个上限进行同步，
		需根据实际硬件配置调整，默认为5
	</div>
);

export const jobConcurrence = ( // 作业并发数
	<div>作业并发数可以根据业务需求和集群资源设定，并发数最大能选择5。</div>
);

export const errorCount = ( // 作业并发数
	<div>
		表示脏数据的最大容忍条数，如果您配置0， 则表示严格不允许脏数据存在；如果不填则代表容忍脏数据
	</div>
);

export const recordDirtyData = (
	<div>
		<p>
			保存到默认位置，您可以在“数据管理-脏数据管理”中查看，表名：defaultName（生命周期：30天）
		</p>
	</div>
);

export const errorPercentConfig = (
	<div>
		<p>任务执行结束后统计错误记录占比，当比例过高时，将此任务实例置为失败</p>
	</div>
);

const baseSystemParams = (
	<p>
		<span>${`{bdp.system.bizdate}`} --业务日期，格式：yyyyMMdd</span>
		<br />
		<span>${`{bdp.system.bizdate2}`} --业务日期，格式：yyyy-MM-dd</span>
		<br />
		<span>${`{bdp.system.cyctime}`} --计划时间，格式：yyyyMMddHHmmss</span>
		<br />
		<span>${`{bdp.system.premonth}`} --上个月（以计划时间为基准），格式：yyyyMM</span>
		<br />
		<span>${`{bdp.system.currmonth}`} --当前月（以计划时间为基准），格式：yyyyMM</span>
		<br />
		<span>
			${`{bdp.system.runtime}`} --当前时间，即任务实际运行的时间，格式：yyyyMMddHHmmss
		</span>
	</p>
);
export const customSystemParams = (
	<div>
		<p>常用系统变量:</p>
		{baseSystemParams}
	</div>
);

export const partitionDesc = (
	<div>
		<p>分区配置支持调度参数，比如常用系统变量:</p>
		{baseSystemParams}
	</div>
);

// 自定义参数配置
export const customParams = (
	<div>
		<p>在代码中输入的格式为：${`{key1}`}，key1 为变量名，在当前面板中为 key1 赋值</p>
		<p>支持常量或变量赋值，常量直接输入字符串或数字</p>
		<p>变量有 $[yyyyMMdd] 和${`{yyyyMMdd}`}格式，二者时间基点不同</p>
		<p>
			$[]格式：变量基于 bdp.system.cyctime 取值，格式为：key1=$[yyyy]，其中的 yyyy 是取
			bdp.system.cyctime 的年的部分
		</p>
		<p>
			${`{}`}格式：变量基于 bdp.system.bizdate 取值，格式为：key1=$
			{`{yyyy}`}，其中的 yyyy 是取 bdp.system.bizdate 的年的部分
		</p>
	</div>
);

//
export const taskDependentTypeDesc = (
	<div>
		<p>任务结束包括成功、失败、取消3种情况</p>
	</div>
);

export const incrementModeScheduleTypeHelp = (
	<div>
		每次同步时，自动记录增量标识的最大值，下次运行时，会从上一次的最大值继续同步数据，实现增量同步
		<br />
		支持将数值类型、Timestamp类型作为增量标识字段
	</div>
);

export const stringColumnFormat = (
	<span>如果源库的一个字符串类型，映射到了目标库的date或time类型，则需要配置转换规则</span>
);

export const dataSyncExtralConfigHelp = (
	<div>
		以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize，每类数据源支持不同的参数，可参考
		<a href={HELP_DOC_URL.DATA_SYNC} target="blank">
			《帮助文档》
		</a>
	</div>
);

export const breakpointContinualTransferHelp = (
	<div>
		支持关系型数据库（MySQL、Oracle、SQLServer、PostgreSQL、DB2）到关系型数据库，数栈、MaxCompute的断点续传
	</div>
);
export const theLastExample = (
	<div>
		始终保留：无论是否延迟都可正常执行。
		<br />
		延迟至第二天后自动取消：当天最后一个实例延迟至第二天还未执行完成则自动取消。
	</div>
);

export const autoSkipJobHelp = (
	<div>
		仅适用于周期为小时和分钟的调度任务，假设某任务的调度周期为10分钟，1:00的实例到1:53才运行完成，勾选此项后将直接运行2:00的实例，1:10
		- 1:50的实例会被置为“自动取消”状态。
	</div>
);
