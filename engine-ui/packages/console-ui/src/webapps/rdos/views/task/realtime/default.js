import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Row, Col } from 'antd'
import utils from "utils";

import * as ModalAction from '../../../store/modules/realtimeTask/modal'
import { modalAction } from '../../../store/modules/realtimeTask/actionTypes'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'

class Default extends Component {
    componentDidMount() {
        const taskId = utils.getParameterByName("taskId")
        if (taskId) {
            this.props.dispatch(BrowserAction.openPage({ id: taskId }))
        }
    }
    render() {
        const { dispatch } = this.props
        return (
            <Row className="box-card txt-left" style={{ paddingTop: '30px' }}>
                <Col className="operation-card" >
                    <div
                        onClick={() => { dispatch(ModalAction.updateModal(modalAction.ADD_TASK_VISIBLE)) }}
                        className="operation-content">
                        <img src="/public/rdos/img/icon_createtask.png" className="anticon" />
                        <p className="txt-center operation-title">创建实时任务</p>
                    </div>
                </Col><Col className="operation-card">
                    <div
                        onClick={() => { dispatch(ModalAction.updateModal(modalAction.ADD_RES_VISIBLE)) }}
                        className="operation-content">
                        <img src="/public/rdos/img/icon_upload.png" className="anticon" />
                            <p className="txt-center operation-title">上传实时计算资源</p>
                    </div>
                </Col>
            </Row>
        )
    }
}
export default connect(state => {
    return {
        project: state.project
    }
})(Default)
