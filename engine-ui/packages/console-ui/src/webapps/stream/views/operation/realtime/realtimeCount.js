import React, { Component } from 'react'
import { Link, hashHistory } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'
import { isEmpty } from 'lodash'

import {
    Card, Button, Row, Col
} from 'antd'

import Api from '../../../api'
import { taskStatus, PROJECT_TYPE } from '../../../comm/const'

class RealtimeCount extends Component {
    state = {
        data: {},
        chart: ''
    }

    componentDidMount () {
        this.loadRealtimeData()
    }

    componentWillReceiveProps (nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadRealtimeData()
        }
    }

    loadRealtimeData () {
        const ctx = this
        Api.taskStatistics().then((res) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data
                })
            }
        })
    }

    getSeriesData = (data) => {
        if (!data) return []
        return [{
            name: '失败',
            value: data.FAILED || 0
        }, {
            name: '运行中',
            value: data.RUNNING || 0
        }, {
            name: '停止',
            value: data.CANCELED || 0
        }, {
            name: '等待运行',
            value: data.WAITENGINE || 0
        }, {
            name: '等待提交',
            value: data.UNSUBMIT || 0
        }]
    }

    jumpToRealList (status) {
        hashHistory.push({
            pathname: '/operation/realtime',
            query: {
                status: (status || status == 0) ? status : undefined
            }
        })
    }

    render () {
        const { data } = this.state
        const { project } = this.props;
        return (
            <div>
                <h1 className="box-title box-title-bolder" style={{ padding: '0 20 0 10' }}>
                    实时任务
                    <Button type="primary" className="right" style={{ marginTop: '8px', fontWeight: 200 }}>
                        <Link to="/operation/realtime">实时任务运维</Link>
                    </Button>
                </h1>
                <div className="box-4 m-card m-card-small" style={{ margin: '0 20 0 10' }}>
                    <Card
                        noHovering
                        bordered={false}
                        loading={false}
                        title="任务数量"
                    >
                        <Row className="m-count">
                            {/* <Col span={4}>
                                <section className="m-count-section">
                                    <span className="m-count-title">全部</span>
                                    <a onClick={this.jumpToRealList.bind(this, taskStatus.ALL)} className="m-count-content font-black">{data.ALL || 0}</a>
                                </section>
                            </Col> */}
                            <Col span={6}>
                                <section className="m-count-section">
                                    <span className="m-count-title">失败</span>
                                    <a onClick={this.jumpToRealList.bind(this, taskStatus.FAILED)} className="m-count-content font-red">{data.FAILED || 0}</a>
                                </section>
                            </Col>
                            <Col span={6}>
                                <section className="m-count-section">
                                    <span className="m-count-title">运行中</span>
                                    <a onClick={this.jumpToRealList.bind(this, taskStatus.RUNNING)} className="m-count-content font-blue">{data.RUNNING || 0}</a>
                                </section>
                            </Col>
                            <Col span={6}>
                                <section className="m-count-section" style={{ width: '60px' }}>
                                    <span className="m-count-title">等待提交</span>
                                    <a onClick={this.jumpToRealList.bind(this, taskStatus.UNSUBMIT)} className="m-count-content font-organge">{data.UNSUBMIT || 0}</a>
                                </section>
                            </Col>
                            <Col span={6}>
                                <section className="m-count-section">
                                    <span className="m-count-title">取消</span>
                                    <a onClick={this.jumpToRealList.bind(this, taskStatus.CANCELED)} className="m-count-content font-gray">{data.CANCELED || 0}</a>
                                </section>
                            </Col>
                        </Row>
                    </Card>
                </div>
            </div>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project
    }
})(RealtimeCount)
