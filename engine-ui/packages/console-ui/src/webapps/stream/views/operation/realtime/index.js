import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'

import {
    Button, Row, Col
} from 'antd'

import Api from '../../../api'

class RealtimeStatistics extends Component {
    state = {
        realtime: {}
    }

    componentDidMount () {
        this.loadRealtimeData()
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps (nextProps) {
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
                ctx.setState({ realtime: res.data })
            }
        })
    }

    render () {
        const { realtime } = this.state
        const faild = realtime.SUBMITFAILD + realtime.FAILED
        return (
            <article className="section">
                <header className="title bd-bottom">
                    <span className="left">实时任务</span>
                    <Button className="right" type="primary"><Link to="/operation/realtime">实时任务运维</Link></Button>
                </header>
                <Row className="operation-statistics txt-left">
                    <Col className="mini-card">
                        <div className="bd card-content" style={{ borderTop: '5px solid #5dd1f2' }}>
                            <p className="count" style={{ color: '#5dd1f2' }}>{realtime.ALL || 0}</p>
                            <p>总数</p>
                        </div>
                    </Col>
                    <Col className="mini-card">
                        <div className="bd card-content" style={{ borderTop: '5px solid #f04134' }}>
                            <p className="count" style={{ color: '#f04134' }}>{faild || 0}</p>
                            <p>失败</p>
                        </div>
                    </Col>
                    <Col className="mini-card">
                        <div className="bd card-content" style={{ borderTop: '5px solid #00a854' }}>
                            <p className="count" style={{ color: '#00a854' }}>{realtime.RUNNING || 0}</p>
                            <p>运行中</p>
                        </div>
                    </Col>
                    <Col className="mini-card">
                        <div className="bd card-content" style={{ borderTop: '5px solid #d0d0d0' }}>
                            <p className="count" style={{ color: '#d0d0d0' }}>{realtime.CANCELED || 0}</p>
                            <p>停止</p>
                        </div>
                    </Col>
                </Row>
            </article>
        )
    }
}
export default connect((state) => {
    return {
        project: state.project
    }
})(RealtimeStatistics)
