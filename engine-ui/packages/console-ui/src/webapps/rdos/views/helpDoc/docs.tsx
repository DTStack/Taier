// 帮助文档
import * as React from 'react'
import { HELP_DOC_URL } from '../../comm/const'
/* eslint-disable */

export const dataFilterDoc = (
    <div>
        where 条件即针对源头数据筛选条件，根据指定的 column、table、where 条件拼接 SQL 进行数据抽取，暂时不支持limit关键字过滤。利用 where 条件可进行全量同步和增量同步，具体说明如下：<br/>
        <ul>
            <li>1）全量同步：第一次做数据导入时通常为全量导入，可不用设置 where 条件。</li>
            <li>2）增量同步：增量导入在实际业务场景中，往往会选择当天的数据进行同步，通常需要编写 where 条件语句，请先确认表中描述增量字段（时间戳）为哪一个。如tableA增量的字段为create_time，则填写create_time > 您需要的日期，如果需要日期动态变化，请参考帮助文档。</li>
        </ul>
    </div>
)

export const mrTaskHelp = (
    <p>支持基于Spark API的Java、Scala处理程序</p>
)

export const mlTaskHelp = (
    <p>支持基于Spark MLLib的机器学习任务</p>
)

export const switchKey = (
    <ul>
        <li>MySQL：支持数值型切分键</li>
        <li>Oracle：支持数值型、字符串类型切分键</li>
    </ul>
)

export const selectKey = (
    <div>
        MySQL、SQLServer、PostgreSQL、Oracle：支持数值型切分键
    </div>
)

export const hdfsPath = (
    <div>
        user/hive/warehouse/projectName.db/<br/>tableName
        是RD-OS默认的HDFS文件组织方式，projectName为项目名，
        其中每个tableName是HDFS内的一个目录，储存着一张表的数据。
        如果此表的数据存储在当前项目空间内，只需修改tableName即可，
        否则需要根据HDFS的存储位置填写。
    </div>
)

export const splitCharacter = (
    <div>
        配置不可见字符，可通过“\”作为转义字符，例如\001
    </div>
)

export const jobSpeedLimit = ( // 作业上限速度
    <div>
        设置作业速率上限，则数据同步作业的总速率将尽可能按照这个上限进行同步，
        需根据实际硬件配置调整，默认为5
    </div>
)

export const jobConcurrence = ( // 作业并发数
    <div>
        作业并发数可以根据业务需求和集群资源设定，并发数最大能选择5。
    </div>
)

export const errorCount = ( // 作业并发数
    <div>
        表示脏数据的最大容忍条数，如果您配置0，
        则表示严格不允许脏数据存在；如果不填则代表容忍脏数据
    </div>
)

export const hdfsConfig = ( // hdfs config
    <div>
        高可用模式下的填写规则：
        <ul>
            <li>
                1、分别要填写：nameservice名称、
                namenode名称（多个以逗号分隔）、proxy.provider参数；</li>
            <li>2、所有参数以JSON格式填写；</li>
            <li>3、格式为：<br/>
                "dfs.nameservices": "nameservice名称",
                "dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔",
                "dfs.namenode.rpc-address.nameservice名称.namenode名称": "",
                "dfs.namenode.rpc-address.nameservice名称.namenode名称": "",
                "dfs.client.failover.proxy.provider.<br/>
                nameservice名称": "org.apache.hadoop.<br/>
                hdfs.server.namenode.ha.<br/>
                ConfiguredFailoverProxyProvider"
            </li>
            <li>
                4、详细参数含义请参考《帮助文档》或
                <a href="http://hadoop.apache.org/docs/r2.7.4/hadoop-project-dist/hadoop-hdfs/HDFSHighAvailabilityWithQJM.html"
                    target="blank">Hadoop官方文档</a>
            </li>
        </ul>
    </div>
)

export const rdbConfig = ( // relational db config
    <div>
        JDBC URL配置参考：
        <ul>
            <li>MySQL: jdbc:mysql://host:port/batch?useCursorFetch=true</li>
            <li>Oracle: jdbc:oracle:thin:@host:port:ORCL</li>
            <li>Hive: jdbc:hive2://host:port/projectName</li>
        </ul>
    </div>
)

export const hBaseConfig = ( // HBase configuration
    <div>
        HBase配置说明：
        <ul>
            <li>1、Zookeeper 集群的地址，多个地址间用逗号分割。例 如：" IP1:Port, IP2:Port, IP3:Port/子目录".默认是 localhost,是给伪分布式用的。要修改才能在完全分布式的情况下使用。如果在hbase-env.sh设置了HBASEMANAGESZK， 这些ZooKeeper节点就会和HBase一起启动</li>
            <li>2、Port: ZooKeeper的zoo.conf中的配置。客户端连接的端口， 默认2181</li>
            <li>3、子目录：HBase在ZooKeeper中配置的子目录</li>
        </ul>
    </div>
)

export const recordDirtyData = (
    <div>
        <p>保存到默认位置，您可以在“数据管理-脏数据管理”中查看，表名：defaultName（生命周期：30天）</p>
    </div>
)

export const errorPercentConfig = (
    <div>
        <p>任务执行结束后统计错误记录占比，当比例过高时，将此任务实例置为失败</p>
    </div>
)

export const customSystemParams = (
    <div>
        <p>
            常用系统变量:
        </p>
        <p>
            <span>${`{bdp.system.premonth}`}</span><br/>
            <span>${`{bdp.system.cyctime}`}</span><br/>
            <span>${`{bdp.system.bizdate}`}</span><br/>
            <span>${`{bdp.system.currmonth}`}</span>
        </p>
    </div>
)

export const partitionDesc = (
    <div>
        <p>分区配置支持调度参数，比如常用系统变量:</p>
        <p>
            <span>${`{bdp.system.premonth}`}</span><br/>
            <span>${`{bdp.system.cyctime}`}</span><br/>
            <span>${`{bdp.system.bizdate}`}</span><br/>
            <span>${`{bdp.system.currmonth}`}</span>
        </p>
    </div>
)

// 自定义参数配置
export const customParams = (
    <div>
        <p>在代码中输入的格式为：${`{key1}`}，key1为变量名，在当前面板中为key1赋值</p>
        <p>支持常量或变量赋值</p>
        <p>常量直接输入字符串或数字</p>
        <p>变量基于bdp.system.cyctime取值，格式为：key1=$[yyyy]，其中的yyyy是取bdp.system.cyctime的年的部分</p>
        <p>详细说明请参考<a href={HELP_DOC_URL.TASKPARAMS} target="blank">《帮助文档》</a></p>
    </div>
)

//
export const taskDependentTypeDesc = (
    <div>
        <p>
            任务结束包括成功、失败、取消3种情况
        </p>
    </div>
)


export const incrementModeScheduleTypeHelp = (
    <div>
        每次同步时，自动记录增量标识的最大值，下次运行时，会从上一次的最大值继续同步数据，实现增量同步
        <br />
        支持将数值类型、Timestamp类型作为增量标识字段
    </div>
)

export const inputTaskHelp = (
    <div>
        利用TensorFlow或MXNet进行数据处理时，可指定此任务在HDFS上的路径信息，无需更新任务代码，方便您通过修改路径来更新数据
    </div>
)

export const outputTaskHelp = (
    <div>
        利用python、TensorFlow或shell进行模型训练时，可将训练完成的模型参数保存在此位置，无需更新任务代码，方便您通过修改路径来频繁训练
    </div>
)

export const optionsTaskHelp = (
    <div>
        任务执行时的命令行参数
    </div>
)

export const switchPartition = (
    <span>
        将上游节点与 JoinTable 节点的数据传输改成按 key 分区。这样通常可以缩小单个节点的 key 个数，提高缓存的命中率
    </span>
);

export const stringColumnFormat = (
    <span>
        如果源库的一个字符串类型，映射到了目标库的date或time类型，则需要配置转换规则
    </span>
)

export const dateTimeFormat = (
    <span>
        修改Date类型的格式
    </span>
)

export const taskFailRetry = (
    <span>
        默认出错自动重试3次，时间间隔2分钟
    </span>
)

export const syncTaskHelp = (
    <div>
        向导模式：便捷、简单，可视化字段映射，快速完成同步任务配置
        <br />
        脚本模式：全能 高效，可深度调优，支持全部数据源
        <br />
        <a href={HELP_DOC_URL.DATA_SOURCE} target="blank">查看支持的数据源</a>
    </div>
)

export const syncModeHelp = (
    <div>
        无增量标识：可通过简单的过滤语句实现增量同步；
        <br />
        有增量标识：系统记录每次同步的点位，执行时可从上次点位继续同步
    </div>
)

export const incrementColumnHelp = (
    <div>
        每次同步时，自动记录增量标识的最大值，下次运行时，会从上一次的最大值继续同步数据，实现增量同步
        <br />
        支持将数值类型、Timestamp类型作为增量标识字段
    </div>
)
export const minuteParticleHelp = (
    <div>
        产生指定的业务日期内，指定的时间范围内计划开始运行的实例，例如：
        <br />
        业务日期：2019-01-01~2019-01-03
        <br />
        具体时间：01:30~03:00
        <br />
        表示：2019-01-01~2019-01-03期间内，每天的01:30~03:00开始运行的实例，时间范围为闭区间，时间范围选择了23:59后，计划23:59开始运行的实例也会产生
        支持将数值类型、Timestamp类型作为增量标识字段
        <br />
        选择分钟粒度后，补数据时，跨周期依赖配置无效
    </div>
)
export const dataSyncExtralConfigHelp = (
    <div>
        以JSON格式添加高级参数，例如对关系型数据库可配置fetchSize，每类数据源支持不同的参数，可参考<a href={HELP_DOC_URL.DATA_SYNC} target='blank'>《帮助文档》</a>
    </div>
)

export const kylinRestfulHelp = (
    <div>
        访问Kylin的认证地址，格式为：http://ip:port 
    </div>
)

export const breakpointContinualTransferHelp = (
    <div>
        支持关系型数据库（MySQL、Oracle、SQLServer、PostgreSQL、DB2）到关系型数据库，数栈、MaxCompute的断点续传
    </div>
)
/* eslint-disable */

