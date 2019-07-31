import * as React from 'react'
import { Tag, Badge } from 'antd'
import { TASK_STATUS, TASK_TYPE, SCRIPT_TYPE, RESOURCE_TYPE, DATA_SOURCE } from '../../comm/const'
import { Circle } from 'widgets/circle'

export function ProjectStatus (props: any) {
    const value = props.value
    switch (value) {
        case 0:
            return <span><Badge status="default" />初始化</span>
        case 1:
            return <span><Badge status="processing" />正常</span>
        case 2:
        case 3:
            return <span><Badge status="error" />创建失败</span>
        default:
            return ''
    }
}

export function TaskStatus (props: any) {
    const value = props.value
    switch (value) {
        case TASK_STATUS.RUNNING:
        case TASK_STATUS.TASK_STATUS_NOT_FOUND:
            return <span>
                <Circle className="status_running" />&nbsp;
                    运行中
            </span>
        case TASK_STATUS.FINISHED:
            return <span>
                <Circle className="status_finished" />&nbsp;
                成功
            </span>
        case TASK_STATUS.STOPED:
            return <span>
                <Circle className="status_stoped" />&nbsp;
                    取消
            </span>
        case TASK_STATUS.RUN_FAILED:
            return <span>
                <Circle className="status_run_fail" />&nbsp;
                运行失败
            </span>
        case TASK_STATUS.SUBMITTING:
            return <span>
                <Circle className="status_submitting" />&nbsp;
                提交中
            </span>
        case TASK_STATUS.SUBMIT_FAILED:
            return <span>
                <Circle className="status_submit_failed" />&nbsp;
                提交失败
            </span>
        case TASK_STATUS.PARENT_FAILD:
            return <span>
                <Circle className="status_submit_failed" />&nbsp;
                上游失败
            </span>
        case TASK_STATUS.WAIT_RUN:
            return <span>
                <Circle className="status_wait_run" />&nbsp;
                等待运行
            </span>
        case TASK_STATUS.FROZEN:
            return <span>
                <Circle className="status_frozen" />&nbsp;
                    冻结
            </span>
        case TASK_STATUS.KILLED:
            return <span>
                <Circle className="status_killed" />&nbsp;
                    已停止
            </span>
        case TASK_STATUS.RESTARTING:
            return <span>
                <Circle className="status_restarting" />&nbsp;
                    重试中
            </span>
        case TASK_STATUS.WAIT_SUBMIT:
            return <span>
                <Circle className="status_wait_submit" />&nbsp;
                等待提交
            </span>
        case TASK_STATUS.DO_FAIL:
            return <span>
                <Circle className="status_submit_failed" />&nbsp;
                失败
            </span>
        default:
            return <span>
                <Circle className="status_submit_failed" />&nbsp;
                异常
            </span>
    }
}

export function TaskBadgeStatus (props: any) {
    const value = props.value
    switch (value) {
        case 3:
        case 4:
        case 6:
        case 10:
        case 11:
            return <Badge status="processing" />
        case 5:
        case 12:
            return <Badge status="success" />
        case 8:
        case 9:
            return <Badge status="error" />
        case 0:
        case 1:
        case 2:
        case 7:
        case 13:
        default:
            return <Badge status="default" />;
    }
}

export function AlarmStatus (props: any) {
    const value = props.value
    switch (value) {
        case 1:
            return <Tag color="green">关闭</Tag>
        case 2:
            return <Tag color="red">删除</Tag>
        case 0:
        default:
            return <Tag color="blue">正常</Tag>
    }
}

export function TaskTimeType (props: any) {
    const value = props.value
    switch (value) {
        case 0:
            return <span>分钟任务</span>
        case 1:
            return <span>小时任务</span>
        case 3:
            return <span>周任务</span>
        case 4:
            return <span>月任务</span>
        case 2:
        default:
            return <span>天任务</span>
    }
}

export function DatabaseType (props: any) {
    const value = props.value
    switch (value) {
        case DATA_SOURCE.KYLIN:
            return <span>KYLIN</span>
        case DATA_SOURCE.MYSQL:
            return <span>MySQL</span>
        case DATA_SOURCE.ORACLE:
            return <span>Oracle</span>
        case DATA_SOURCE.SQLSERVER:
            return <span>SQLServer</span>
        case DATA_SOURCE.POSTGRESQL:
            return <span>PostgreSQL</span>
        case DATA_SOURCE.RDBMS:
            return <span>RDBMS</span>
        case DATA_SOURCE.HDFS:
            return <span>HDFS</span>
        case DATA_SOURCE.HIVE:
            return <span>Hive</span>
        case DATA_SOURCE.HBASE:
            return <span>HBase</span>
        case DATA_SOURCE.FTP:
            return <span>FTP</span>
        case DATA_SOURCE.MAXCOMPUTE:
            return <span>MaxCompute</span>
        case DATA_SOURCE.ES:
            return <span>ElasticSearch</span>
        case DATA_SOURCE.REDIS:
            return <span>Redis</span>
        case DATA_SOURCE.MONGODB:
            return <span>MongoDB</span>
        case DATA_SOURCE.KAFKA:
            return <span>Kafka</span>
        case DATA_SOURCE.DB2:
            return <span>DB2</span>
        case DATA_SOURCE.CARBONDATA:
            return <span>CarbonData</span>
        case DATA_SOURCE.LIBRASQL:
            return <span>LibrA</span>
        case DATA_SOURCE.GBASE:
            return <span>GBase</span>
        default:
            return <span>其他</span>
    }
}

export function TaskType (props: any) {
    const value = props.value
    switch (value) {
        case TASK_TYPE.HIVESQL:
            return <span>HiveSQL</span>
        case TASK_TYPE.CUBE_KYLIN:
            return <span>Kylin</span>
        case TASK_TYPE.VIRTUAL_NODE:
            return <span>虚节点</span>
        case TASK_TYPE.MR:
            return <span>Spark</span>
        case TASK_TYPE.SYNC:
            return <span>数据同步</span>
        case TASK_TYPE.PYTHON:
            return <span>PySpark</span>
        case TASK_TYPE.DEEP_LEARNING:
            return <span>深度学习</span>
        case TASK_TYPE.PYTHON_23:
            return <span>Python</span>
        case TASK_TYPE.SHELL:
            return <span>Shell</span>
        case TASK_TYPE.ML:
            return <span>机器学习</span>
        case TASK_TYPE.HAHDOOPMR:
            return <span>HadoopMR</span>
        case TASK_TYPE.WORKFLOW:
            return <span>工作流</span>
        case TASK_TYPE.SQL:
            return <span>SparkSQL</span>
        case TASK_TYPE.CARBONSQL:
            return <span>CarbonSQL</span>
        case TASK_TYPE.NOTEBOOK:
            return <span>Notebook</span>
        case TASK_TYPE.EXPERIMENT:
            return <span>算法实验</span>
        case TASK_TYPE.LIBRASQL:
            return <span>LibraSQL</span>
        default:
            return <span>SparkSQL</span>
    }
}

export function ResType (props: any) {
    const value = props.value
    switch (value) {
        case RESOURCE_TYPE.JAR:
            return <span>jar</span>
        case RESOURCE_TYPE.PY:
            return <span>python</span>
        default:
            return ''
    }
}

export function ScriptType (props: any) {
    const value = props.value
    switch (value) {
        case SCRIPT_TYPE.SQL:
            return <span>SQL脚本</span>
        default:
            return <span>其他脚本</span>
    }
}

export function AlarmTriggerType (props: any) {
    const value = props.value
    switch (value) {
        case 0:
            return <span>任务失败</span>
        case 2:
            return <span>未完成</span>
        case 3:
            return <span>任务停止</span>
        case 4:
            return <span>定时未完成</span>
        case 5:
            return <span>超时未完成</span>
        default:
            return <span>-</span>
    }
}

export function AlarmTypes (props: any) {
    const arr: any = []
    const data = props.value
    if (data && data.length > 0) {
        for (let i = 0; i < data.length; i++) {
            switch (data[i]) {
                case 1:
                    arr.push('邮件')
                    break;
                case 2:
                    arr.push('短信')
                    break;
                case 3:
                    arr.push('微信');
                    break;
                case 4:
                    arr.push('钉钉');
                    break;
                default:
                    break;
            }
        }
    }
    return <span>{arr.join(',')}</span>
}
