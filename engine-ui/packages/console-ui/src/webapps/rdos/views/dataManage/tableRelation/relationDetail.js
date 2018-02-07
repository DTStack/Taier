import React from 'react';
import { Link, hashHistory } from 'react-router';
import { connect } from 'react-redux';
import { Row, Col, Table, Pagination, Modal } from 'antd'

import utils from 'utils'

import Api from '../../../api'
import Editor from '../../../components/code-editor'
import { TaskType, ScriptType } from '../../../components/status'
import { TASK_TYPE } from '../../../comm/const'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction' 

const titleStyle = {
    height: '28px',
    display: 'block',
    lineHeight: '28px',
    textIndent: '5px',
}

function renderLevel(level) {
    if (level === -1) {
        return <span style={{color: 'rgb(236, 105, 65);'}}>环形血缘</span>
    }
    return level
}

@connect(null, (dispatch) => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        }
    }
})
class RelationDetail extends React.Component {

    state = {
        current: 1,
        pageSize: 5,
        visibleRecord: false,
        recordInfo: {},
    }

    showRecord = (item) => {
        const { goToTaskDev } = this.props;

        if (item.taskType !== -1) { // 任务
            Api.getOfflineTaskDetail({
                id: item.relationId
            }).then(res => {
                if(res.code === 1) {
                    if (item.taskType === TASK_TYPE.SQL) {
                        this.setState({
                            recordInfo: res.data,
                            visibleRecord: true
                        })
                    } else {
                        goToTaskDev(item.relationId)
                    }
                }
            });
        } else { // 脚本
            Api.getScriptById({
                scriptId: item.relationId,
            }).then(res => {
                if (res.code === 1) {
                    this.setState({
                        recordInfo: res.data,
                        visibleRecord: true
                    })
                }
            })
        }
    }

    initialCols = () => {
        return [{
            title: '名称',
            dataIndex: 'name',
            key: 'name',
            render: (text, record) => {
                return <a onClick={this.showRecord.bind(this, record)}>{text}</a>
            }
        },{
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render: (text, record) => {
                return record.taskType === -1 ? 
                <ScriptType value={record.scriptType} /> : 
                <TaskType value={record.taskType} />
            }
        }, {
            title: '创建者',
            dataIndex: 'createUser',
            key: 'createUser'
        }]
    }

    pageChange = (page) => {
        this.setState({
            current: page
        })
        this.props.loadRelTasks({
            tableId: this.props.data.tableId,
            pageIndex: page,
        })
    }

    render() {
        const { data, relationTasks } = this.props
        const { recordInfo, visibleRecord } = this.state

        return (
            <div className="task-floating-window rel-table-info ant-table bd"
                style={{ width: '90%'}}>
                <header style={{ padding: '5px' }} className="bd-bottom">
                    <span style={{ fontSize: '12px' }}>{data.catalogue}/</span>
                    <span><b>{data.tableName}</b></span>
                </header>
                <Row>
                    <Col span={12} 
                        className="bd-right table-info" 
                        style={{height: '193px'}}
                    >
                        <table>
                            <tbody className="ant-table-tbody" >
                                <tr><td>创建者：</td><td>{data.createUser}</td></tr>
                                <tr><td>创建时间：</td><td>{utils.formateDateTime(data.createTime)}</td></tr>
                                <tr><td>描述：</td><td>{data.comment}</td></tr>
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
                    </Col>
                    <Col span={12}>
                        <span className="bd-bottom" style={titleStyle}>
                            <b>相关任务与脚本</b>
                        </span>
                        <Table
                            columns={this.initialCols()}
                            rowKey="taskId"
                            pagination={false}
                            showHeader={false}
                            dataSource={(relationTasks && relationTasks.data) || []}
                        />
                        <Pagination
                            className="txt-right"
                            size="small"
                            style={{ margin: '0 5px 5px'}}
                            pageSize={this.state.pageSize}
                            onChange={this.pageChange}
                            current={this.state.current}
                            total={relationTasks.totalCount} />
                    </Col>
                </Row>
                <Modal
                    width="600px"
                    title="预览"
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    visible={visibleRecord}
                    onCancel={() => { this.setState({ visibleRecord: false, recordInfo: {} }) }}
                    footer={null}
                >
                    <Editor 
                        sync={true}
                        value={recordInfo.sqlText || recordInfo.scriptText }
                        style={{height: '600px'}}
                    />
                </Modal>
            </div>
        )
    }
}

export default RelationDetail 