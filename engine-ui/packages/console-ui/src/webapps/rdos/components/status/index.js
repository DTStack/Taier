import React from 'react'
import { Tag, Badge, Tooltip } from 'antd'
import { TASK_TYPE, SCRIPT_TYPE, RESOURCE_TYPE } from '../../comm/const'
import { Circle } from 'widgets/circle' 

export function ProjectStatus(props) {
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

// 实时任务
export function TaskStatus(props) {
    const value = props.value
    switch (value) {
        case 4:
            return <span>
                <Circle style={{ background: '#2491F7' }} />&nbsp;
                    运行中
                </span>
        case 5:
            return <span color="green">
                <Circle style={{ background: '#00A755' }} />&nbsp;
                已完成
            </span>
        case 7:
            return <span>
            <Circle style={{ background: '#EF5350' }} />&nbsp;
                取消
            </span>
        case 8:
            return <span color="red">
                <Circle style={{ background: '#EF5350' }} />&nbsp;
                失败
            </span>
        case 10:
            return <span color="green">
                <Circle style={{ background: '#2491F7' }} />&nbsp;
                提交中
            </span>
        case 16:
        return <span color="green" >
            <Circle style={{ background: '#F5A623' }} />&nbsp;
            等待运行
        </span>
        case 18:
            return <span>
                <Circle style={{ background: '#26DAD2' }} />&nbsp;
                    冻结
                </span>
        case 0:
        default:
            return <span>
                <Circle style={{ background: '#d9d9d9' }} />&nbsp;
                未提交
            </span>
    }
}

// 0--->未提交
// 10-->提交中
// 16--->等待运行
// 4--->运行中
// 5--->已完成
// 8--->失败
// 7--->取消
// 18--->冻结
export function OfflineTaskStatus(props) {
    const value = props.value
    switch (value) {
        case 4:
            return <span>
                    <Circle style={{ background: '#2491F7' }} />&nbsp;
                    运行中
                </span>
        case 5:
            return <span color="green">
                <Circle style={{ background: '#00A755' }} />&nbsp;
                已完成
            </span>
        case 7:
            return <span>
            <Circle style={{ background: '#EF5350' }} />&nbsp;
                取消
            </span>
        case 8:
            return <span color="red">
                <Circle style={{ background: '#EF5350' }} />&nbsp;
                失败
            </span>
        case 10:
            return <span color="green">
                <Circle style={{ background: '#2491F7' }} />&nbsp;
                提交中
            </span>
        case 16:
        return <span color="green" >
            <Circle style={{ background: '#F5A623' }} />&nbsp;
            等待运行
        </span>
        case 18:
            return <span>
                <Circle style={{ background: '#26DAD2' }} />&nbsp;
                    冻结
                </span>
        case 0:
        default:
            return <span>
                <Circle style={{ background: '#d9d9d9' }} />&nbsp;
                未提交
            </span>
    }
}

export function TaskBadgeStatus(props) {
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

export function AlarmStatus(props) {
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

export function TaskTimeType(props) {
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

export function DatabaseType(props) {
    const value = props.value
    switch (value) {
    case 1:
        return <span>MySQL</span>
    case 2:
        return <span>Oracle</span>
    case 3:
        return <span>SQLServer</span>
    case 6:
        return <span>HDFS</span>
    case 7:
        return <span>Hive</span>
    case 8:
        return <span>HBASE</span>
    case 9:
        return <span>FTP</span>
    default:
        return <span>其他</span>
    }
}

export function TaskType(props) {
    const value = props.value
    switch (value) {
        case TASK_TYPE.VIRTUAL_NODE:
            return <span>虚节点</span>
        case TASK_TYPE.MR:
            return <span>MR</span>
        case TASK_TYPE.SYNC:
            return <span>数据同步</span>
        case TASK_TYPE.PYTHON:
            return <span>Python</span>
        case TASK_TYPE.SQL:
        default:
            return <span>SQL</span>
    }
}

export function ResType(props) {
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

export function ScriptType(props) {
    const value = props.value
    switch (value) {
        case SCRIPT_TYPE.SQL:
            return <span>SQL脚本</span>
        default:
            return <span>其他脚本</span>
    }
}

export function AlarmTriggerType(props) {
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

export function AlarmTypes(props) {
    const arr = []
    const data = props.value
    if (data && data.length > 0) {
        for (let i = 0; i < data.length; i++) {
            switch(data[i]) {
                case 1:
                    arr.push('邮件')
                    break;
                case 2:
                    arr.push('短信')
                    break;
                default:
                    break;
            }
        }
    }
    return <span>{arr.join(',')}</span>
}
