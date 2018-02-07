import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Row, Col, Icon } from 'antd';
import { isEmpty } from 'lodash';

import {
    modalAction
} from '../../../store/modules/offlineTask/actionType';
import MyIcon from '../../../components/icon'

import Workbench from './workbench';

class Default extends Component {
    render() {
        const { 
            createTask, upload, workbench, scriptTree,
            toggleCreateTask, toggleUpload, toggleCreateScript
        } = this.props;
        const iconStyle = { width: '60px', height: '60px', marginTop: '25px'}
        return (
            workbench.tabs.length ?
            <Workbench />:
            <Row className="inner-content txt-left" style={{ marginTop: '30px' }}>
                <Col className="operation-card" >
                    <div
                      onClick={ toggleCreateTask }
                      className="operation-content">
                        <MyIcon style={iconStyle}  type="add-file" />
                    </div>
                    <p className="txt-center">创建离线任务</p>
                </Col>
                <Col className="operation-card">
                    <div
                      onClick={ toggleUpload }
                      className="operation-content">
                        <MyIcon style={iconStyle} type="upload" />
                    </div>
                    <p className="txt-center">上传离线计算资源</p>
                </Col>
                {
                    !isEmpty(scriptTree) && <Col className="operation-card">
                        <div
                            onClick={toggleCreateScript}
                            className="operation-content">
                            <Icon style={iconStyle} type="code-o" />
                        </div>
                        <p className="txt-center">创建脚本</p>
                    </Col>
                }
            </Row>
        )
    }
}

export default connect((state) => {
    const { createTask, upload } = state.offlineTask.modalShow;
    const { workbench, scriptTree } = state.offlineTask;
    return { createTask, upload, workbench, scriptTree };
},
(dispatch) => {
    return {
        toggleCreateTask: function() {
            dispatch({
                type: modalAction.TOGGLE_CREATE_TASK
            });
        },

        toggleUpload: function() {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD
            });
        },

        toggleCreateScript: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_SCRIPT
            });
        }
    }
})(Default);
