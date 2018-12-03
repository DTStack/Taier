import React from 'react'

import { OfflineTaskStatus, TaskTimeType, TaskType } from '../../../../components/status'

export function TaskInfo (props) {
    const { task, project } = props
    return (
        <div className="ant-table bd task-detail">
            <table>
                <tbody className="ant-table-tbody" >
                    <tr>
                        <td>任务名称：</td><td>{task.batchTask.name || '-'}</td>
                        <td>任务ID：</td><td>{task.batchTask.id || '-'}</td>
                    </tr>
                    <tr>
                        <td>任务类型：</td>
                        <td><TaskType value={task.batchTask.taskType} /></td>
                        <td>状态：</td>
                        <td><OfflineTaskStatus value={task.status} /></td>
                    </tr>
                    <tr>
                        <td>调度周期：</td><td><TaskTimeType value={task.taskPeriodId}/></td>
                        <td>计划时间：</td><td>{task.cycTime}</td>
                    </tr>
                    <tr>
                        <td>开始时间：</td>
                        <td>{task.execStartDate ? task.execStartDate : '-'}</td>
                        <td>结束时间：</td>
                        <td>{task.execStartDate ? task.execEndDate : '-'}</td>
                    </tr>
                    <tr>
                        <td>所属项目：</td>
                        <td>{project.projectName}</td>
                        <td>责任人：</td>
                        <td>
                            {task.batchTask && task.batchTask.createUser
                                ? task.batchTask.createUser.userName : '-'}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}

export function TaskOverView (props) {
    const { task, project } = props
    const display = task.status === 5 ? 'none' : 'table-row'
    return (
        <div className="task-floating-window ant-table bd" >
            <table>
                <tbody className="ant-table-tbody" >
                    <tr><td>任务名称：</td><td>{task.batchTask ? task.batchTask.name : '-'}</td></tr>
                    <tr><td>运行状态：</td><td><OfflineTaskStatus value={task.status} /></td></tr>
                    <tr><td>所属项目：</td><td>{project.projectName}</td></tr>
                    <tr><td>任务类型：</td><td><TaskType value={task.batchTask.taskType} /></td></tr>
                    <tr>
                        <td>定时时间：</td>
                        <td>{task.cycTime}</td>
                    </tr>
                    <tr style={{ display }}>
                        <td>开始时间：</td>
                        <td>{task.execStartDate ? task.execStartDate : '-'}</td>
                    </tr>
                    <tr style={{ display }}>
                        <td>结束时间：</td>
                        <td>{task.execEndDate ? task.execEndDate : '-'}</td>
                    </tr>
                    <tr>
                        <td>责任人：</td>
                        <td>
                            {task.batchTask && task.batchTask.createUser
                                ? task.batchTask.createUser.userName : '-'}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}
