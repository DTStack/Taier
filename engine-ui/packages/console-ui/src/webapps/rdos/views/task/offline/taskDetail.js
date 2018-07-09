import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Tag, Collapse, message } from 'antd';

import utils from 'utils';

import { TaskType } from '../../../components/status';
import { getProjectUsers } from '../../../store/modules/user';

import TaskVersion from './taskVersion';
import Api from '../../../api';

import {
    workbenchAction
} from '../../../store/modules/offlineTask/actionType';
import { workbenchActions } from '../../../store/modules/offlineTask/offlineAction';

import UpdateTaskOwnerModal from './updateTaskOwnerModal';

const Panel = Collapse.Panel;

function TaskInfo(props) {
    const taskInfo = props.taskInfo
    return (
        <Row className="task-info">
            <Row>
                <Col span="10" className="txt-right">任务名称：</Col>
                <Col span="14">
                    {taskInfo.name}
                </Col>
            </Row>
            <Row>
                <Col span="10" className="txt-right">任务类型：</Col>
                <Col span="14">
                    <TaskType value={taskInfo.taskType} />
                </Col>
            </Row>
            {taskInfo.resourceList.length && <Row>
                <Col span="10" className="txt-right">资源：</Col>
                <Col span="14t">{taskInfo.resourceList.map(o =>
                    <Tag key={o.id} color="blue" style={{ marginTop: 10 }}>
                        {o.resourceName}
                    </Tag>)} 
                </Col>
            </Row>}

            <Row>
                <Col span="10" className="txt-right">责任人：</Col>
                <Col span="14">
                    {
                        taskInfo.owneruser && taskInfo.owneruser.userName
                    } <a onClick={props.modifyTaskOwner}>修改</a>
                </Col>
            </Row>
            <Row>
                <Col span="10" className="txt-right">创建时间：</Col>
                <Col span="14">
                    {utils.formatDateTime(taskInfo.gmtCreate)}
                </Col>
            </Row>
            <Row>
                <Col span="10" className="txt-right">最近修改人员：</Col>
                <Col span="14">{taskInfo.readWriteLockVO.lastKeepLockUserName}</Col>
            </Row>
            <Row>
                <Col span="10" className="txt-right">最近修改时间：</Col>
                <Col span="14">{utils.formatDateTime(taskInfo.gmtModified)}</Col>
            </Row>
            <Row>
                <Col span="10" className="txt-right">描述：</Col>
                <Col span="14" style={{
                        lineHeight: '20px',
                        padding: '10 0'
                    }}>{taskInfo.taskDesc}</Col>
            </Row>
        </Row>
    )
}

class TaskDetail extends React.Component {

    constructor(props) {
        super(props);
    }

    state = {
        visible: false,
        selectedUser: '',
    }

    componentDidMount() {
        this.props.dispatch(getProjectUsers())
    }

    onSelectUser = (value) => {
        this.setState({
            selectedUser: value,
        })
    }

    modifyTaskOwner = () => {
        const ownerId = this.state.selectedUser;
        const taskId = this.props.tabData.id;
        if (ownerId) {
            Api.updateTaskOwner({
                ownerUserId: ownerId,
                taskId,
            }).then((res) => {
                if (res.code === 1) {
                    message.success('修改成功！');
                    this.props.reloadTabTask(taskId)
                    this.setState({
                        visible: false,
                        selectedUser: '',
                    });
                }
            })
        } else {
            message.error('请选择所属用户！');
        }
    }

    setSqlText(sqlText) {
        this.props.updateTaskField({ sqlText, merged: true })
    }

    render() {
        const { visible } = this.state;
        const { tabData, projectUsers } = this.props;

        return <div className="m-taksdetail">
            <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                <Panel key="1" header="任务属性">
                    <TaskInfo 
                        taskInfo={tabData} 
                        modifyTaskOwner={() => {this.setState({visible: true})}}
                    />
                    <UpdateTaskOwnerModal 
                        visible={visible}
                        projectUsers={projectUsers}
                        onSelect={this.onSelectUser}
                        onOk={this.modifyTaskOwner}
                        defaultValue={`${tabData.ownerUserId || ''}`}
                        onCancel={() => {this.setState({visible: false})}}
                    />
                </Panel>
                <Panel key="2" header="历史发布版本">
                    <TaskVersion
                        taskInfo={tabData}
                        changeSql={this.setSqlText}
                    />
                </Panel>
            </Collapse>
        </div>
    }
}

export default connect((state, ownProps) => {
    return {
        projectUsers: state.projectUsers,
    };

}, workbenchActions)(TaskDetail);