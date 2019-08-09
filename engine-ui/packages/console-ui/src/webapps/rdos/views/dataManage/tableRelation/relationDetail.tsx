import * as React from 'react';
import { connect } from 'react-redux';
import { Row, Table, Pagination, Modal } from 'antd'

import CommApi from '../../../api';
import Editor from 'main/components/code-editor'
import { TaskType, ScriptType } from '../../../components/status'
import { TASK_TYPE } from '../../../comm/const'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction'
import { getProject } from '../../../store/modules/project';
function renderLevel (level: any) {
    if (level === -1) {
        return <span style={{ color: 'rgb(236, 105, 65);' }}>环形血缘</span>
    }
    return level
}

@(connect(null, (dispatch: any) => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id: any) => {
            actions.openTaskInDev(id)
        },
        getProject: (projectId: any) => {
            dispatch(getProject(projectId))
        }
    }
}) as any)
class RelationDetail extends React.Component<any, any> {
    state: any = {
        current: 1,
        pageSize: 5,
        visibleRecord: false,
        recordInfo: {},
        editKey: ''
    }

    showRecord = (item: any) => {
        const { goToTaskDev, getProject } = this.props;

        if (item.taskType !== -1) { // 任务
            CommApi.getOfflineTaskDetail({
                id: item.relationId
            }).then((res: any) => {
                if (res.code === 1) {
                    if (item.taskType === TASK_TYPE.SQL || item.taskType === TASK_TYPE.LIBRASQL) {
                        this.setState({
                            recordInfo: res.data,
                            visibleRecord: true,
                            editKey: Math.random()
                        })
                    } else {
                        goToTaskDev(item.relationId)
                        getProject(item.projectId) // 获取项目信息
                    }
                }
            });
        } else { // 脚本
            CommApi.getScriptById({
                id: item.relationId
            }).then((res: any) => {
                if (res.code === 1) {
                    this.setState({
                        recordInfo: res.data,
                        visibleRecord: true,
                        editKey: Math.random()
                    })
                }
            })
        }
    }

    initialCols = () => {
        return [{
            title: '任务/脚本',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                // 是否有权限 0-否，1-是
                return record.isPermissioned === 1
                    ? <a onClick={this.showRecord.bind(this, record)}>{text}</a> : text;
            }
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render: (text: any, record: any) => {
                return record.taskType === -1
                    ? <ScriptType value={record.scriptType} />
                    : <TaskType value={record.taskType} />
            }
        }, {
            title: '项目',
            dataIndex: 'projectName',
            key: 'projectName'
        }, {
            title: '责任人',
            dataIndex: 'createUser',
            key: 'createUser'
        }]
    }

    pageChange = (page: any) => {
        this.setState({
            current: page
        })
        this.props.loadRelTasks({
            tableId: this.props.data.tableId,
            pageIndex: page
        })
    }

    render () {
        const { data, relationTasks } = this.props
        const { recordInfo, visibleRecord } = this.state
        return (
            <div className="task-floating-window rel-table-info ant-table" style={{ top: '600px' }}>
                <div>
                    <Row className="tb-wrapper" style={{ borderBottom: 0 }}>
                        <table>
                            <tbody className="ant-table-tbody" >
                                <tr>
                                    <td>
                                        <span title={data.tableName}><b>{data.dataSource}.{data.tableName}</b></span>
                                    </td>
                                    <td><span>创建者：{data.createUser}</span></td>
                                    <td><a className="right" onClick={this.props.onShowColumn}>字段血缘关系</a></td>
                                </tr>
                            </tbody>
                        </table>

                        <table>
                            <tbody className="ant-table-tbody" >
                                <tr>
                                    <td>下游表层数：</td>
                                    <td>{renderLevel(data.downstreamLevelNum)}</td>
                                    <td>上游表层数：</td>
                                    <td>{renderLevel(data.upstreamLevelNum)}</td>
                                </tr>
                                <tr>
                                    <td>下游表数量：</td>
                                    <td>{renderLevel(data.totalDownstreamNum)}</td>
                                    <td>上游表数量：</td>
                                    <td>{renderLevel(data.totalUpstreamNum)}</td>
                                </tr>
                                <tr>
                                    <td>直接下游表数量：</td>
                                    <td>{renderLevel(data.directDownstreamNum)}</td>
                                    <td>直接上游表数量：</td>
                                    <td>{renderLevel(data.directUpstreamNum)}</td>
                                </tr>
                            </tbody>
                        </table>
                    </Row>
                    <Row className="tb-wrapper" style={{ marginTop: '20px', height: '200px' }}>
                        <Table
                            columns={this.initialCols()}
                            rowKey="relationId"
                            className="dt-ant-table"
                            pagination={false}
                            dataSource={(relationTasks && relationTasks.data) || []}
                        />
                    </Row>
                    <Pagination
                        className="txt-right"
                        style={{ margin: '20px 5px 5px' }}
                        pageSize={this.state.pageSize}
                        onChange={this.pageChange}
                        current={this.state.current}
                        total={relationTasks.totalCount} />
                </div>
                <Modal
                    width="600px"
                    title="预览"
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    visible={visibleRecord}
                    onCancel={() => { this.setState({ visibleRecord: false, recordInfo: {} }) }}
                    footer={null}
                >
                    <Editor
                        key={this.state.editKey}
                        sync={true}
                        value={recordInfo.sqlText || recordInfo.scriptText }
                        style={{ height: '600px' }}
                    />
                </Modal>
            </div>
        )
    }
}

export default RelationDetail
