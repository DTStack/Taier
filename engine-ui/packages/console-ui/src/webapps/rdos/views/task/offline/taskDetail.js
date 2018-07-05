import React from 'react';
import { connect } from 'react-redux';
import { Row, Col, Tag, Collapse, Table } from 'antd';

import utils from 'utils';

import { TaskType } from '../../../components/status';
import { TASK_TYPE } from '../../../comm/const';

import TaskVersion from './taskVersion';

import {
    workbenchAction
} from '../../../store/modules/offlineTask/actionType';

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
                <Col span="10" className="txt-right">创建人员：</Col>
                <Col span="14">{taskInfo.createUser && taskInfo.createUser.userName}</Col>
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

    render() {
        const { tabData } = this.props;
        return <div className="m-taksdetail">
            <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                <Panel key="1" header="任务属性">
                    <TaskInfo taskInfo={tabData} />
                </Panel>
                <Panel key="2" header="历史发布版本">
                    <TaskVersion
                        taskInfo={tabData}
                        changeSql={this.props.setSqlText}
                    />
                </Panel>
            </Collapse>
        </div>
    }
}

export default connect((state, ownProps) => {
    return {...ownProps};
}, dispatch => {
    return {
        setSqlText(sqlText) {
            dispatch({
                type: workbenchAction.SET_TASK_FIELDS_VALUE,
                payload: { sqlText, merged: true } 
            });
        },
    }
})(TaskDetail);