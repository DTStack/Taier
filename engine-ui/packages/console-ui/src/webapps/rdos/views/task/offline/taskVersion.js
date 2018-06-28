import React from 'react';
import { connect } from 'react-redux';
import { Row, Table, Modal } from 'antd';

import utils from 'utils';

import DiffCodeEditor from '../../../components/diff-code-editor';
import { TASK_TYPE } from '../../../comm/const';
import DiffParams from './diffParams';

export default class TaskVersion extends React.Component {

    state = {
        showDiff: false,
        campareTo: '',
        diffParams:{
            showDiffparams: false,
            tableInfo: '',
        },
    }

    constructor(props) {
        super(props);
    }

    diffCode = (target) => {
        this.setState({
            showDiff: true,
            campareTo: target
        })
    }

    diffParams = (data) => {
        const { diffParams } = this.state;
        diffParams.showDiffparams = true;
        diffParams.tableInfo = data;
        this.setState({
           diffParams
        })
    }

    close = () => {
        this.setState({
            showDiff: false,
            campareTo: '',
        })
    }

    closeParamsModal = () => {
        const { diffParams } = this.state;
        diffParams.showDiffparams = false;
        diffParams.tableInfo = "";
        this.setState({
           diffParams
        })
    }

    codeChange = (old, newVal) => {
        this.props.changeSql(newVal)
    }
    
    render() {
        const { taskInfo, taskType} = this.props;
        const { showDiff, campareTo,diffParams } = this.state;
        
        const isLocked = taskInfo.readWriteLockVO && !taskInfo.readWriteLockVO.getLock
        return (
            <div>
                <Table
                    className="m-table"
                    rowKey="id"
                    dataSource={taskInfo.taskVersions || []}
                    columns={this.taskVersionCols()}
                    pagination={false}
                />
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="代码对比"
                    width="900px"
                    bodyStyle={{height: '500px'}}
                    visible={showDiff}
                    onCancel={this.close}
                    cancelText="关闭"
                    footer={null}
                >
                    <DiffCodeEditor 
                        readOnly={isLocked}
                        value={taskInfo.sqlText} 
                        compareTo={campareTo.sqlText}
                        onChange={this.codeChange}
                    /> 
                </Modal>
                <Modal
                    wrapClassName="vertical-center-modal modal-body-nopadding"
                    title="参数对比"
                    width="900px"
                    bodyStyle={{height: '500px'}}
                    visible={diffParams.showDiffparams}
                    onCancel={this.closeParamsModal}
                    cancelText="关闭"
                    footer={null}
                >
                   <DiffParams value={taskInfo} diffParams={diffParams.tableInfo} taskType={taskType}/>
                </Modal>
            </div>
        )
    }

    taskVersionCols = () => {
        const taskInfo = this.props.taskInfo
        return [
            {
                width: 120,
                title: '发布时间',
                dataIndex: 'gmtCreate',
                key: 'gmtCreate',
                render: (text) => {
                    return utils.formatDateTime(text)
                },
            }, {
                title: '发布人',
                dataIndex: 'userName',
                key: 'userName',
            }, {
                width: 120,
                title: '描述',
                dataIndex: 'publishDesc',
                key: 'publishDesc',
            }, {
                title: '操作',
                dataIndex: 'operation',
                width: 80,
                key: 'operation',
                render: (text, record) => {
                    return <span>
                        {taskInfo.taskType === TASK_TYPE.SQL ? 
                            <div>
                                <a onClick={() => this.diffCode(record)}>
                                    代码
                                </a>
                                <span className="ant-divider"></span>
                                <a onClick={() => this.diffParams(record)}>
                                    参数
                                </a>
                            </div>
                             : '-'
                        }
                    </span>
                },
            }
        ]
    }

}