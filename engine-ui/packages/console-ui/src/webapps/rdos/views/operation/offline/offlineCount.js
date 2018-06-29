import React, { Component } from 'react'
import { Link, hashHistory } from 'react-router'
import { connect } from 'react-redux'
import moment from 'moment'

import {
    Card, Button, Row,
    Col, Table, DatePicker,
 } from 'antd'

import { taskStatus } from '../../../comm/const'
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

    jumpToOfflineList(status){
        hashHistory.push({
            pathname:"/operation/offline-operation",
            query:{
                status:(status||status==0)?status:undefined
            }
        })
    }

    render() {
        const { data } = this.state
        const flex = {
            flexGrow: 1,
            flex: 1
        }
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
                        title="今日周期实例完成情况" 
                    >
                        <Row className="m-count" style={{display: 'flex'}}>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">全部</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.ALL)} className="m-count-content font-black">{data.ALL || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">失败</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.FAILED)} className="m-count-content font-red">{data.FAILED || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">运行中</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.RUNNING)} className="m-count-content font-organge">{data.RUNNING || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">成功</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.FINISHED)} className="m-count-content font-green">{data.FINISHED || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section" style={{width:"60px"}}>
                                    <span className="m-count-title">等待提交</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.UNSUBMIT)} className="m-count-content font-gray">{data.UNSUBMIT || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">提交中</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.SUBMITTING)} className="m-count-content font-organge">{data.SUBMITTING || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section" style={{width:"60px"}}>
                                    <span className="m-count-title" >等待运行</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.WAITING_RUN)} className="m-count-content font-organge">{data.WAITENGINE || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">冻结</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.FROZEN)} className="m-count-content font-blue">{data.FROZEN || 0}</a>
                                </section>
                            </Col>
                            <Col style={flex}>
                                <section className="m-count-section">
                                    <span className="m-count-title">取消</span>
                                    <a onClick={this.jumpToOfflineList.bind(this,taskStatus.CANCELED)} className="m-count-content font-gray">{data.CANCELED || 0}</a>
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
