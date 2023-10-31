/* eslint-disable react/no-unescaped-entities */
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

export const dirtyMaxRecord = <span>脏数据达到最大值时，任务自动失败</span>;

export const dirtyFailRecord = <span>当脏数据处理失败次数超过设定值时，任务失败</span>;

export const dirtySaveType = <span>仅当保存到数据库时，可展示脏数据分析内容</span>;

export const logPrintTimes = (
    <span>设定脏数据在日志中输出间隔，设置为0时不打印；若开启脏数据保存，则脏数据直接保存至指定库，不再输出至日志</span>
);

export const targetColText =
    '别名指字段的别名，如select  order_sales as order_amont from  shop_order，order_sales字段的别名即为order_amont';

export const delayTabWarning = <p>kafka中堆积的未被消费的数据量（条数）</p>;

export const componentTips = <div>选择组件后只可配置任务的输入参数与输出参数，SQL代码无法编辑。</div>;

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
        进行数据抽取，暂时不支持limit关键字过滤。利用 where 条件可进行全量同步和增量同步，具体说明如下：
        <br />
        <ul>
            <li>1）全量同步：第一次做数据导入时通常为全量导入，可不用设置 where 条件。</li>
            <li>
                2）增量同步：增量导入在实际业务场景中，往往会选择当天的数据进行同步，通常需要编写 where
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

export const jobSpeedLimit = // 作业上限速度
    <div>设置作业速率上限，则数据同步作业的总速率将尽可能按照这个上限进行同步， 需根据实际硬件配置调整，默认为5</div>;

export const jobConcurrence = // 作业并发数
    <div>作业并发数可以根据业务需求和集群资源设定，并发数最大能选择5。</div>;

export const errorCount = // 作业并发数
    <div>表示脏数据的最大容忍条数，如果您配置0， 则表示严格不允许脏数据存在；如果不填则代表容忍脏数据</div>;

export const recordDirtyData = (
    <div>
        <p>保存到默认位置，您可以在“数据管理-脏数据管理”中查看，表名：defaultName（生命周期：30天）</p>
    </div>
);
export const recordDirtyStream = (
    <div>
        <p>开启后，系统将进行脏数据管理，您可以在“任务运维-任务详情-脏数据”中查看。</p>
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
        <span>${`{bdp.system.runtime}`} --当前时间，即任务实际运行的时间，格式：yyyyMMddHHmmss</span>
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
            $[]格式：变量基于 bdp.system.cyctime 取值，格式为：key1=$[yyyy]，其中的 yyyy 是取 bdp.system.cyctime
            的年的部分
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
    <div>支持关系型数据库（MySQL、Oracle、SQLServer、PostgreSQL、DB2）到关系型数据库，数栈、MaxCompute的断点续传</div>
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
export const writerChannel = (
    <div>
        <p>作业写入并发数支持用户根据业务需求和集群资源手动设定。</p>
    </div>
);
export const dirtySource = (
    <div>
        脏数据表会写入选择的hive库中，表名默认系统分配"dirty_任务名称"（存储时间默认为90天）同时，支持写入自定义表，用户自定义表名，数据写入时进行新建。
    </div>
);
export const hiveWithAllTable = (
    <div>
        <p>Hive仅支持当前已有数据表自动建表，后续新增表将无法自动生成</p>
    </div>
);
// 选择目标 - 数据有序
export const writeDataSequence = (
    <div>
        <p>
            <span>开启后实时采集将在写入时保证数据的有序性，此时作业读取、写入并发度仅能为1，更多参考&nbsp;</span>
            <a rel="noopener noreferrer" target="_blank" href={HELP_DOC_URL.FORCE_ORDER}>
                帮助文档
            </a>
        </p>
    </div>
);
// 选择目标 - Partition Key
export const writePartitionKey = (
    <div>
        <p>
            <span>默认根据表名自动分区，用户可指定表字段作为分区主键，更多请参考&nbsp;</span>
            <a rel="noopener noreferrer" target="_blank" href={HELP_DOC_URL.FORCE_ORDER}>
                帮助文档
            </a>
        </p>
    </div>
);
export const writeTableType = (
    <div>
        <p>
            自动建表的表名，将按照固定前缀（stream）、源表所属的schema、表名拼接，若数据源选择了分表模式，则每个表分组会自动创建一张Hive表
        </p>
    </div>
);
export const analyticalRules = (
    <div>
        <p>自动建表的表名，将按照固定前缀（stream）、源表所属的schema、表名拼接</p>
        <p>若配置了分表模式，则{'{table}'}将被替换为每个分组名称</p>
    </div>
);
export const partitionType = (
    <div>
        <p>按照天或小时粒度，自动创建分区（字段名pt），并按照数据写入时间，自动写入不同分区</p>
    </div>
);
export const isCleanSession = (
    <div>
        <p>是:MQTT服务器不保存于客户端会话的的主题与确认位置</p>
        <p>否:MQTT服务器保存于客户端会话的的主题与确认位置</p>
    </div>
);
export const writeDocForADB = (
    <div>选择更新模式时，引擎将自动获取对应源表的唯一索引；若索引不存在，则仍使用追加模式写入数据</div>
);
export const kafkaTip = (
    <div>
        <p>从Kafka1.0开始，高版本可以兼容低版本的Kafka。</p>
    </div>
);
export const sqlserverTip = (
    <div>
        <p>
            当前支持SQL Server 2014、2016、
            <br />
            2017、2019版本
        </p>
    </div>
);
export const syncSourceType = (
    <div>
        <p>
            间隔轮询：当源库未开启实时备份机制时（MySQL_Binlog、Oracle_LogMiner等），可通过JDBC轮询获取数据，源表需包含稳定的自增标识
        </p>
    </div>
);
export const intervalColumn = (
    <div>
        <p>每次同步时，自动记录增量标识的最大值，下次运行时，会从上一次的最大值继续同步数据，实现增量同步</p>
        <p>目前支持将VARCHAR（纯数字）INT、LONG、TIMESATAMP、DATE类型作为增量标识字段</p>
    </div>
);
export const startLocation = (
    <div>
        <p>
            若不填则默认从头开始拉取数据，输入格式请在“数据预览”中参考所选增量标示字段内容。采集时不包含采集起点，例如采集起点为40
            则采集开始时不会包含id=40这一条数据。
        </p>
    </div>
);
export const extralConfig = (
    <div>
        <p>
            以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize，每类数据源支持不同的参数，可参考
            <a target="blank" href={HELP_DOC_URL.JOB_CONFIG}>
                《帮助文档》
            </a>
        </p>
    </div>
);
export const multipleTableTip = (
    <div>
        <p>分表模式下，可选择多个表分组，按照不同分组的表，写入同一Hive下的不同表，实现多对多写入</p>
    </div>
);
export const temporary = (
    <div>
        <p>当任务停止时，临时Slot删除，用户无法再进行续跑操作</p>
    </div>
);
export const transferTypeFormat = (
    <div>
        <p>表结构解析：将采集到的日志信息按照表结构解析</p>
        <p>{`嵌套JSON平铺：将多层嵌套格式的JSON分解为单层结构，例如：{"a": 1, "b": {"c":3}}将会被分解为：{“a”:1,”b_c”:3}；`}</p>
        <p>当目标数据源为Hive时，必须勾选Json平铺</p>
    </div>
);
export const sourceFormat = (
    <div>
        <p>将多层嵌套格式的JSON分解为单层结构</p>
        <p>例如：</p>
        <p>{'{"a":1,  "b": {"c":3}}'}</p>
        <p>将会被分解为：</p>
        <p>{'{"a":1,"b_c":3}'}</p>
        <p>当目标数据源为Hive时，必须勾选Json平铺</p>
    </div>
);
export const binlogPortHelp = <div>端口号可手动指定，若不指定，任务运行时会自动分配</div>;
export const parseRules = (
    <div>
        <p>text：不对Socket数据进行处理，直接写入相应目标源</p>
    </div>
);
export const restfulParam = (
    <div style={{ wordBreak: 'break-all' }}>
        <p>Key：支持用户手动输入Body中的参数名；</p>
        <p>Value：Body参数请求的具体参数值；</p>
        <p>
            NextValue：可通过${`{}`}填写body和response动态参数，例如${`{body.a}`}
            +1；时间类型的参数变化量单位为ms；
        </p>
        <p>Format：参数值格式化，例如yyyy-MM-dd hh:mm:ss</p>
        <p>
            更多参数配置，请参考{' '}
            <a target="blank" href="/public/helpSite/stream/v4.0/StreamSync/RestfulAPI.html">
                帮助手册
            </a>
        </p>
    </div>
);
export const fieldDelimiter = (
    <div>
        <p>嵌套切分键对所有勾选嵌套的Key、Value、NextValue生效；</p>
        <p>使用切分键可指向动态参数中的嵌套字段，例如{'${body.a / b}'}</p>
        <p>
            更多可参考{' '}
            <a target="blank" href="/public/helpSite/stream/v4.0/StreamSync/RestfulAPI.html">
                帮助手册
            </a>
        </p>
    </div>
);
export const strategy = (
    <div>
        <p>
            Key：可通过${`{}`}
            指向body和response的参数，若为嵌套格式，字段名称由JSON的各层级Key以“.”隔开组合而成，例如：a.b；
        </p>
        <p>Value：Key参数对应的具体参数值，支持通过${`{}`}指向body和response的参数；</p>
        <p>
            更多配置，请参考{' '}
            <a target="blank" href="/public/helpSite/stream/v4.0/StreamSync/RestfulAPI.html">
                帮助手册
            </a>
        </p>
    </div>
);
export const restfulDecode = (
    <div>
        <p>返回类型为text时，平台对采集到的信息不做任何处理</p>
        <p>
            更多详细配置，请参考
            <a target="blank" href="/public/helpSite/stream/v4.0/StreamSync/RestfulAPI.html">
                帮助手册
            </a>
        </p>
    </div>
);
export const restfulFields = (
    <div>
        <p>若为嵌套格式，字段名称由JSON的各层级Key以“.”隔开组合而成，例如：a.b</p>
    </div>
);
export const intervalTime = (
    <div>
        <p>第一次请求返回与第二次请求发送的间隔时间</p>
    </div>
);
export const asyncTimeoutNumDoc = <span>错误数据达到指定数据量时，实时任务失败</span>;
export const queryFault = <span>维表查询失败是否扫描第二个副本，默认false</span>;
