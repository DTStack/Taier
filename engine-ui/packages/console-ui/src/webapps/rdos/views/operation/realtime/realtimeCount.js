import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Card, Button, Row, Col
 } from 'antd'

import Api from '../../../api'

class RealtimeCount extends Component {

    state = {
        data: {},
        chart: '',
    }

    componentDidMount() {
        this.loadRealtimeData()
    }

    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadRealtimeData()
        }
    }

    loadRealtimeData() {
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

    render() {
        const { data } = this.state
        return (
            <div>
                <h1 className="box-title box-title-bolder">
                    实时任务
                    <Button type="primary" className="right" style={{marginTop: '8px'}}>
                        <Link to="/operation/realtime">实时任务运维</Link>
                    </Button>
                </h1>
                <div className="box-4 m-card m-card-small">
                    <Card
                        noHovering
                        bordered={false}
                        loading={false} 
                        title="任务数量"
                    >
                        <Row className="m-count">
                            <Col span={4}>
                                <section className="m-count-section">
                                    <span className="m-count-title">全部</span>
                                    <span className="m-count-content font-black">{data.ALL || 0}</span>
                                </section>
                            </Col>
                            <Col span={5}>
                                <section className="m-count-section">
                                    <span className="m-count-title">失败</span>
                                    <span className="m-count-content font-red">{data.FAILED || 0}</span>
                                </section>
                            </Col>
                            <Col span={6}>
                                <section className="m-count-section">
                                    <span className="m-count-title">运行</span>
                                    <span className="m-count-content font-organge">{data.RUNNING || 0}</span>
                                </section>
                            </Col>
                            <Col span={5}>
                                <section className="m-count-section">
                                    <span className="m-count-title">停止</span>
                                    <span className="m-count-content font-darkgreen">{data.CANCELED || 0}</span>
                                </section>
                            </Col>
                            <Col span={4}>
                                <section className="m-count-section">
                                    <span className="m-count-title">取消</span>
                                    <span className="m-count-content font-green">{data.CANCELED || 0}</span>
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
        project: state.project,
    }
})(RealtimeCount)
