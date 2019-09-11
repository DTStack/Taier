import * as React from 'react'
import { Link, hashHistory } from 'react-router'
import { connect } from 'react-redux'

import {
    Card, Button, Row, Tooltip,
    Col, Icon
} from 'antd'

import { taskStatus, PROJECT_TYPE } from '../../../comm/const'
import Api from '../../../api'

class OfflineCount extends React.Component<any, any> {
    state: any = {
        data: ''
    }

    componentDidMount () {
        this.loadOfflineData()
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadOfflineData()
        }
    }

    loadOfflineData = () => {
        Api.getJobStatistics().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data
                })
            }
        })
    }

    jumpToOfflineList (status: any) {
        const joinStatus = status && status.join(',');
        hashHistory.push({
            pathname: '/operation/offline-operation',
            query: {
                status: joinStatus
            }
        })
    }

    render () {
        const { data } = this.state
        const { project } = this.props;
        const isTest = project.projectType == PROJECT_TYPE.TEST;
        // const flex: any = {
        //     flexGrow: 1,
        //     flex: 1
        // }
        const colSpan = 5;
        const notRunCount = (data.UNSUBMIT || 0) + (data.SUBMITTING || 0) + (data.WAITENGINE || 0)

        return (
            <div>
                <div style={{ width: '400px', height: '40px', position: 'absolute', zIndex: 10, right: '20px', top: '0px' }}>
                    {isTest && <Button type="primary" className="right" style={{ marginTop: '6px', marginRight: '10px', fontWeight: 200 }}>
                        <Link to="/package/create?type=offline">任务发布</Link>
                    </Button>}
                    <Button type="primary" className="right" style={{ marginTop: '6px', marginRight: '10px', fontWeight: 200 }}>
                        <Link to="/operation/offline-operation">任务运维</Link>
                    </Button>
                </div>
                <div className="box-4 m-card m-card-small">
                    <Card
                        noHovering
                        bordered={false}
                        loading={false}
                        title="今日周期实例执行汇总"
                    >
                        <Row className="m-count" >
                            <Col span={4}>
                                <section className="m-count-section">
                                    <span className="m-count-title">失败</span>
                                    <a onClick={this.jumpToOfflineList.bind(this, [taskStatus.FAILED, taskStatus.SUBMITFAILD, taskStatus.PARENTFAILED])} className="m-count-content font-red">{data.FAILED || 0}</a>
                                </section>
                            </Col>
                            <Col span={colSpan}>
                                <section className="m-count-section">
                                    <span className="m-count-title">运行中</span>
                                    <a onClick={this.jumpToOfflineList.bind(this, [taskStatus.RUNNING])} className="m-count-content font-blue">{data.RUNNING || 0}</a>
                                </section>
                            </Col>
                            <Col span={colSpan}>
                                <section className="m-count-section" style={{ width: 60 }}>
                                    <span className="m-count-title">未运行 <Tooltip title="包括等待提交、提交中、等待运行3种状态"><Icon type="question-circle-o" /></Tooltip></span>
                                    <a onClick={this.jumpToOfflineList.bind(this, [taskStatus.UNSUBMIT, taskStatus.SUBMITTING, taskStatus.WAITING_RUN])} className="m-count-content font-organge">{notRunCount}</a>
                                </section>
                            </Col>
                            <Col span={colSpan}>
                                <section className="m-count-section">
                                    <span className="m-count-title">成功</span>
                                    <a onClick={this.jumpToOfflineList.bind(this, [taskStatus.FINISHED])} className="m-count-content font-green">{data.FINISHED || 0}</a>
                                </section>
                            </Col>
                            <Col span={colSpan}>
                                <section className="m-count-section" style={{ width: 60 }}>
                                    <span className="m-count-title">冻结/取消</span>
                                    <a onClick={this.jumpToOfflineList.bind(this, [taskStatus.FROZEN, taskStatus.CANCELED])} className="m-count-content font-gray">{(data.FROZEN || 0) + (data.CANCELED || 0)}</a>
                                </section>
                            </Col>
                        </Row>
                    </Card>
                </div>
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        project: state.project
    }
})(OfflineCount)
