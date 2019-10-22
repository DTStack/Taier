// 帮助文档
import * as React from 'react'
import { HELP_DOC_URL } from '../../comm/const'
/* eslint-disable */

export const dataFilterDoc = (
    <div>
        where 条件即针对源头数据筛选条件，根据指定的 column、table、where 条件拼接 SQL 进行数据抽取，暂时不支持limit关键字过滤。利用 where 条件可进行全量同步和增量同步，具体说明如下：<br />
        <ul>
            <li>1）全量同步：第一次做数据导入时通常为全量导入，可不用设置 where 条件。</li>
            <li>2）增量同步：增量导入在实际业务场景中，往往会选择当天的数据进行同步，通常需要编写 where 条件语句，请先确认表中描述增量字段（时间戳）为哪一个。如tableA增量的字段为create_time，则填写create_time {'>'} 您需要的日期，如果需要日期动态变化，请参考帮助文档。</li>
        </ul>
    </div>
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
        user/hive/warehouse/projectName.db/<br />tableName
        是RD-OS默认的HDFS文件组织方式，projectName为项目名，
        其中每个tableName是HDFS内的一个目录，储存着一张表的数据。
        如果此表的数据存储在当前项目空间内，只需修改tableName即可，
        否则需要根据HDFS的存储位置填写。
    </div>
)

export const jobSpeedLimit = ( // 作业上限速度
    <div>
        设置作业速率上限，则实时采集作业的总速率将尽可能按照这个上限进行同步，
        需根据实际硬件配置调整，默认为不限制上传速率
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
            <li>3、格式为：<br />
                {`"dfs.nameservices": "nameservice名称",`}
                {`"dfs.ha.namenodes.nameservice名称": "namenode名称，以逗号分隔",`}
                {`"dfs.namenode.rpc-address.nameservice名称.namenode名称": "",`}
                {`"dfs.namenode.rpc-address.nameservice名称.namenode名称": "",`}
                {`"dfs.client.failover.proxy.provider.`}<br />
                {`nameservice名称": "org.apache.hadoop.`}<br />
                hdfs.server.namenode.ha.<br />
                {`ConfiguredFailoverProxyProvider"`}
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
            <li>{`1、Zookeeper 集群的地址，多个地址间用逗号分割。例 如：" IP1:Port, IP2:Port, IP3:Port{'/'}子目录".默认是 localhost,是给伪分布式用的。要修改才能在完全分布式的情况下使用。如果在hbase-env.sh设置了HBASEMANAGESZK， 这些ZooKeeper节点就会和HBase一起启动`}</li>
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
            <span>${`{bdp.system.premonth}`}</span><br />
            <span>${`{bdp.system.cyctime}`}</span><br />
            <span>${`{bdp.system.bizdate}`}</span><br />
            <span>${`{bdp.system.currmonth}`}</span>
        </p>
    </div>
)

export const partitionDesc = (
    <div>
        <p>功能释义</p>
        <p>分区配置支持调度参数，比如常用系统变量:</p>
        <p>
            <span>${`{bdp.system.premonth}`}</span><br />
            <span>${`{bdp.system.cyctime}`}</span><br />
            <span>${`{bdp.system.bizdate}`}</span><br />
            <span>${`{bdp.system.currmonth}`}</span>
        </p>
    </div>
)

// 自定义参数配置
export const customParams = (
    <div>
        <p>支持常量或变量</p>
        <p>常量直接输入字符串或数字</p>
        变量基于bdp.system.cyctime取值，例如“key1={'${'}yyyy{'}'}”取bdp.system.cyctime的年的部分
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

export const alarmWarning = (
    <div>
        <p>延迟消费数：Kafka延迟消费的消息条数，多个分区时，任一分区超过阈值时都会触发告警；</p>
        <p>延迟消费比例：Kafka延迟消费消息条数/总条数，多个分区时，每个分区的延迟消费比例=此分区延迟消费消息条数/此分区的总消息条数，任一分区延迟消费占比超过阈值时都会触发告警。</p>
        <p>后台统计周期：延迟消费数、延迟消费比例，2个值都是每隔10秒钟统计一次；</p>
    </div>
)

export const delayTabWarning = (
    <p>kafka中堆积的未被消费的数据量（条数）</p>
)

export const newStreamTask = (
    <div>
        <p>FlinkSQL：WEB上编辑SQL代码实现流数据处理，简单高效</p>
        <p>Flink：基于Flink API的Java、Scala处理程序，灵活定制</p>
        <p>实时采集：关系型数据库、filebeat等各类数据源的实时采集，方便快捷</p>
    </div>
)

export const binlogPortHelp = (
    <div>
        端口号可手动指定，若不指定，任务运行时会自动分配
    </div>
)

export const splitCharacter = (
    <div>
        配置不可见字符，可通过“\”作为转义字符，例如\001
    </div>
)

export const sourceFormat = (
    <div>
        <p>将多层嵌套格式的JSON分解为单层结构</p>
        <p>例如：</p>
        <p>{'{"a":1,  "b": {"c":3}}'}</p>
        <p>将会被分解为：</p>
        <p>{'{"a":1,"b_c":3}'}</p>
    </div>
)

export const analyticalRules = (
    <div>
        <p>自动建表的表名，将按照固定前缀（stream）、源表所属的schema、表名拼接</p>
        <p>若配置了分表模式，则{'{table}'}将被替换为每个分组名称</p>
    </div>
)
export const multipleTable = (
    <div>
        <p>分表模式下，可选择多个表分组，按照不同分组的表，写入不同的目标，实现多对多写入</p>
    </div>
)

export const partitionType = (
    <div>
        <p>按照天或小时粒度，自动创建分区（字段名pt），并按照数据写入时间，自动写入不同分区</p>
    </div>
)
export const writeTableType = (
    <div>
        <p>自动建表的表名，将按照固定前缀（stream）、源表所属的schema、表名拼接，若数据源选择了分表模式，则每个表分组会自动创建一张Hive表</p>
    </div>
)