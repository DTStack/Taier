import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Row, Col, Icon } from 'antd'

import MyIcon from '../../../components/icon'
import * as ModalAction from '../../../store/modules/realtimeTask/modal'
import { modalAction } from '../../../store/modules/realtimeTask/actionTypes'

class Default extends Component {

    render() {
        const { dispatch } = this.props
        const iconStyle = { width: '60px', height: '60px', marginTop: '25px'}
        return (
            <Row className="box-card txt-left" style={{ paddingTop: '30px' }}>
                <Col className="operation-card" >
                    <div
                      onClick={() => { dispatch(ModalAction.updateModal(modalAction.ADD_TASK_VISIBLE)) }}
                      className="operation-content">
                        <MyIcon style={iconStyle}  type="add-file" /> 
                    </div>
                    <p className="txt-center">创建实时任务</p>
                </Col>
                <Col className="operation-card">
                    <div
                      onClick={() => { dispatch(ModalAction.updateModal(modalAction.ADD_RES_VISIBLE)) }}
                      className="operation-content">
                         <MyIcon style={iconStyle} type="upload" /> 
                    </div>
                    <p className="txt-center">上传实时计算资源</p>
                </Col>
            </Row>
        )
    }
}
export default connect()(Default)
