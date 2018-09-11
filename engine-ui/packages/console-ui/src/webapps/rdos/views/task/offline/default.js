import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Row, Col, Icon } from 'antd';
import { isEmpty } from 'lodash';
import utils from "utils";

import MyIcon from '../../../components/icon'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction';

import Workbench from './workbench';
import { PROJECT_TYPE } from '../../../comm/const';

class Default extends Component {
    componentDidMount() {
        const taskId = utils.getParameterByName("taskId")
        if (taskId) {
            this.props.openTaskInDev(+taskId)
        }
    }
    render() {
        const {
            workbench, scriptTree,
            toggleCreateTask, toggleUpload, toggleCreateScript, project
        } = this.props;
        const iconStyle = { width: '60px', height: '60px', marginTop: '25px' }
        const isPro = project.projectType == PROJECT_TYPE.PRO
        return (
            workbench.tabs.length ?
                <Workbench /> :
                (!isPro && <Row className="box-card txt-left" style={{ paddingTop: '30px' }}>
                    <Col className="operation-card" >
                        <div
                            onClick={toggleCreateTask}
                            className="operation-content">
                            <img src="/public/rdos/img/icon_createtask.png" className="anticon" />
                            <p className="txt-center operation-title">创建离线任务</p>
                        </div>
                    </Col>
                    <Col className="operation-card">
                        <div
                            onClick={toggleUpload}
                            className="operation-content">
                            <img src="/public/rdos/img/icon_upload.png" className="anticon" />
                            <p className="txt-center operation-title">上传离线计算资源</p>
                        </div>
                    </Col>
                    {
                        !isEmpty(scriptTree) && <Col className="operation-card">
                            <div
                                onClick={toggleCreateScript}
                                className="operation-content">
                                <img src="/public/rdos/img/icon_createscript.png" className="anticon" />
                                <p className="txt-center operation-title">创建脚本</p>
                            </div>
                        </Col>
                    }
                </Row>)
        )
    }
}

export default connect((state) => {
    const { createTask, upload } = state.offlineTask.modalShow;
    const { workbench, scriptTree } = state.offlineTask;
    return { createTask, upload, workbench, scriptTree, project: state.project };
}, workbenchActions)(Default);
