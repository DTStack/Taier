import * as React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Tag, Collapse, message } from 'antd';

import utils from 'utils';

import { TaskType } from '../../../components/status';
import { getProjectUsers } from '../../../store/modules/user';
import LockPanel from '../../../components/lockPanel'

import TaskVersion from './taskVersion/taskVersion';
import Api from '../../../api';

import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';
import UpdateTaskOwnerModal from './updateTaskOwnerModal';
import { PROJECT_TYPE } from '../../../comm/const';
import SchedulingConfig from './schedulingConfig';

const Panel = Collapse.Panel;

function TaskInfo (props: any) {
    const taskInfo = props.taskInfo
    const couldEdit = props.couldEdit;
    const labelPrefix = props.labelPrefix || '任务';
    return (
        <Row className="task-info">
            <Row>
                <Col span={10} className="txt-right">{labelPrefix}名称：</Col>
                <Col span={14}>
                    {taskInfo.name}
                </Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">{labelPrefix}类型：</Col>
                <Col span={14}>
                    <TaskType value={taskInfo.taskType} />
                </Col>
            </Row>
            {taskInfo.resourceList.length && <Row>
                <Col span={10} className="txt-right">资源：</Col>
                <Col span={14}>{taskInfo.resourceList.map((o: any) =>
                    <Tag key={o.id} color="blue" style={{ marginTop: 10 }}>
                        {o.resourceName}
                    </Tag>)}
                </Col>
            </Row>}

            <Row>
                <Col span={10} className="txt-right">责任人：</Col>
                <Col span={14} style={{ position: 'relative' }}>
                    <LockPanel lockTarget={taskInfo} />
                    {
                        taskInfo.ownerUser && taskInfo.ownerUser.userName
                    }&nbsp;{couldEdit && <a onClick={props.modifyTaskOwner}>修改</a>}
                </Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">创建时间：</Col>
                <Col span={14}>
                    {utils.formatDateTime(taskInfo.gmtCreate)}
                </Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">最近修改人员：</Col>
                <Col span={14}>{taskInfo.readWriteLockVO.lastKeepLockUserName}</Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">最近修改时间：</Col>
                <Col span={14}>{utils.formatDateTime(taskInfo.gmtModified)}</Col>
            </Row>
            <Row>
                <Col span={10} className="txt-right">描述：</Col>
                <Col span={14} style={{
                    lineHeight: '20px',
                    padding: '10 0'
                }}>{taskInfo.taskDesc}</Col>
            </Row>
        </Row>
    )
}

class TaskDetail extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    state: any = {
        visible: false,
        selectedUser: ''
    }

    componentDidMount () {
        this.props.dispatch(getProjectUsers())
    }

    onSelectUser = (value: any) => {
        this.setState({
            selectedUser: value
        })
    }

    modifyTaskOwner = () => {
        const ownerId = this.state.selectedUser;
        const taskId = this.props.tabData.id;
        if (ownerId) {
            Api.updateTaskOwner({
                ownerUserId: ownerId,
                taskId
            }).then((res: any) => {
                if (res.code === 1) {
                    message.success('修改成功！');
                    this.props.reloadTaskTab(taskId)
                    this.setState({
                        visible: false,
                        selectedUser: ''
                    });
                }
            })
        } else {
            message.error('请选择所属用户！');
        }
    }

    setSqlText = (sqlText: any) => {
        this.props.updateTaskField({ sqlText, merged: true })
    }

    render () {
        const { visible } = this.state;
        const { tabData, projectUsers, isWorkflowNode, project, tabs, couldEdit, editor, updateTaskField } = this.props;
        const isPro = project.projectType == PROJECT_TYPE.PRO;

        const labelPrefix = isWorkflowNode ? '节点' : '任务';
        return <div className="m-taksdetail">
            <Collapse bordered={false} defaultActiveKey={['1', '2', '3']}>
                <Panel key="1" header={`${labelPrefix}属性`}>
                    <TaskInfo
                        couldEdit={couldEdit}
                        isPro={isPro}
                        taskInfo={tabData}
                        labelPrefix={labelPrefix}
                        modifyTaskOwner={() => { this.setState({ visible: true }) }}
                    />
                    <UpdateTaskOwnerModal
                        visible={visible}
                        projectUsers={projectUsers}
                        onSelect={this.onSelectUser}
                        onOk={this.modifyTaskOwner}
                        defaultValue={`${tabData.ownerUserId || ''}`}
                        onCancel={() => { this.setState({ visible: false }) }}
                    />
                </Panel>
            </Collapse>
            {
                isWorkflowNode
                    ? <SchedulingConfig
                        couldEdit={couldEdit}
                        isWorkflowNode={isWorkflowNode}
                        tabData={tabData}
                        tabs={tabs}
                    >
                    </SchedulingConfig> : ''
            }
            <Collapse bordered={false} defaultActiveKey={['3']}>
                <Panel key="3" header={`历史版本`}>
                    <TaskVersion
                        isPro={isPro}
                        taskInfo={tabData}
                        editor={editor}
                        updateTaskField={updateTaskField}
                        changeSql={this.setSqlText}
                    />
                </Panel>
            </Collapse>
        </div>
    }
}

export default connect((state: any, ownProps: any) => {
    const { workbench } = state.offlineTask;
    return {
        projectUsers: state.projectUsers,
        project: state.project,
        tabs: workbench.tabs,
        editor: state.editor
    };
}, workbenchActions)(TaskDetail);
