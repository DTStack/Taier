import * as React from 'react'
import { connect } from 'react-redux'
import { Row, Col } from 'antd'
import utils from 'utils';

import * as ModalAction from '../../../store/modules/realtimeTask/modal'
import { modalAction } from '../../../store/modules/realtimeTask/actionTypes'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'

class Default extends React.Component<any, any> {
    componentDidMount () {
        const taskId = utils.getParameterByName('taskId')
        if (taskId) {
            this.props.dispatch(BrowserAction.openPage({ id: taskId }))
        }
    }
    render () {
        const { dispatch, editor } = this.props;
        const themeDark = editor.options.theme !== 'vs' ? true : undefined;
        const iconBaseUrl = themeDark ? '/public/stream/img/theme-dark' : '/public/rdos/img';

        return (
            <Row className="box-card txt-left" style={{ paddingTop: '30px' }}>
                <Col className="operation-card" >
                    <div
                        onClick={() => { dispatch(ModalAction.updateModal(modalAction.ADD_TASK_VISIBLE)) }}
                        className="operation-content">
                        <img src={`${iconBaseUrl}/create_task.png`} className="anticon" />
                        <p className="txt-center operation-title">创建任务</p>
                    </div>
                </Col><Col className="operation-card">
                    <div
                        onClick={() => { dispatch(ModalAction.updateModal(modalAction.ADD_RES_VISIBLE)) }}
                        className="operation-content">
                        <img src={`${iconBaseUrl}/upload_res.png`} className="anticon" />
                        <p className="txt-center operation-title">上传资源</p>
                    </div>
                </Col>
            </Row>
        )
    }
}
export default connect((state: any) => {
    return {
        project: state.project,
        editor: state.editor
    }
})(Default)
