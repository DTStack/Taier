import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Card, Button, Row,
    Col, Table, DatePicker,
 } from 'antd'
 
import Api from '../../../api'

class OfflineCount extends Component {

    state = {
        data: '',
    }

    componentDidMount() {
        this.loadOfflineData()
    }


    componentWillReceiveProps(nextProps) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadOfflineData()
        }
    }

    loadOfflineData = () => {
        const ctx = this
        Api.getJobStatistics().then((res) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data
                })
            }
        })
    }

    render() {
        const { data } = this.state
        return (
            <div style={{marginTop: '10px'}}>
                <h1 className="box-title box-title-bolder">
                    离线任务
                    <Button type="primary" className="right" style={{marginTop: '8px'}}>
                        <Link to="/operation/offline-operation">离线任务运维</Link>
                    </Button>
                </h1>
                <div className="box-4 m-card m-card-small">
                    <Card
                        noHovering
                        bordered={false}
                        loading={false} 
                        title="今日任务完成情况"
                    >
                        <Row className="m-count">
                            <Col span={4}>
                                <section className="m-count-section">
                                    <span className="m-count-title">失败</span>
                                    <span className="m-count-content font-red">{data.FAILED || 0}</span>
                                </section>
                            </Col>
                            <Col span={5}>
                                <section className="m-count-section">
                                    <span className="m-count-title">运行</span>
                                    <span className="m-count-content font-organge">{data.RUNNING || 0}</span>
                                </section>
                            </Col>
                            <Col span={6}>
                                <section className="m-count-section">
                                    <span className="m-count-title">未运行</span>
                                    <span className="m-count-content font-gray">{data.WAITENGINE || 0}</span>
                                </section>
                            </Col>
                            <Col span={5}>
                                <section className="m-count-section">
                                    <span className="m-count-title">成功</span>
                                    <span className="m-count-content font-green">{data.FINISHED || 0}</span>
                                </section>
                            </Col>
                            <Col span={4}>
                                <section className="m-count-section">
                                    <span className="m-count-title">取消</span>
                                    <span className="m-count-content font-black">{data.CANCELED || 0}</span>
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
})(OfflineCount)
