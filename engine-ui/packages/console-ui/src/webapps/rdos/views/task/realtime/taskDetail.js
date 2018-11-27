import React, { Component } from 'react'
import {
    Row, Col, Modal, Tag,
    message, Select, Collapse
} from 'antd'

import utils from 'utils'
import Api from '../../../api'
import { TASK_TYPE } from "../../../comm/const"
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import TaskVersion from '../offline/taskVersion';

const Option = Select.Option;
const Panel = Collapse.Panel;

export default class TaskDetail extends Component {

    state = {
        visibleAlterRes: false,
        resList: [],
    }

    componentWillReceiveProps(nextProps) {
        const currentPage = nextProps.currentPage
        const oldPage = this.props.currentPage
        if (currentPage.id !== oldPage.id) {
            const resVal = currentPage.resourceList.length > 0 ?
                currentPage.resourceList.map(item => item.id) : []
            this.setState({
                resList: resVal,
            })
        }
    }

    handleChange = (val) => {
        this.setState({ resList: val })
    }

    alterRes = () => {
        const ctx = this
        const task = this.props.currentPage
        let resList = this.state.resList
        resList = Array.isArray(resList) ? resList : [resList]

        if (resList.length === 0) {
            message.info('您没有选择任何资源！')
            return
        }
        if (task && task.id && resList.length > 0) {
            Api.updateTaskRes({
                id: task.id,
                resources: resList,
            }).then((res) => {
                if (res.code === 1) {
                    message.success('资源修改成功！')
                    ctx.reloadTask(task.id)
                    ctx.setState({ visibleAlterRes: false, resList: [] })
                }
            })
        }
    }

    reloadTask(id) {
        const { dispatch } = this.props
        Api.getTask({ taskId: id }).then((res) => {
            if (res.code === 1) {
                dispatch(BrowserAction.setCurrentPage(res.data))
                dispatch(BrowserAction.updatePage(res.data))
            }
        })
    }
    getTaskName(type) {
        switch (type) {
            case TASK_TYPE.SQL: {
                return "FlinkSQL"
            }
            case TASK_TYPE.MR: {
                return "FlinkMR"
            }
            case TASK_TYPE.DATA_COLLECTION: {
                return "实时采集"
            }
        }
    }
    render() {
        const { visibleAlterRes, resList } = this.state
        const { resources, currentPage, editorChange, editor } = this.props
        const taskRes = currentPage.resourceList && currentPage.resourceList.map((item) => {
            return <Tag key={item.id} color="blue">{item.resourceName}</Tag>
        })
        const resOptions = resources && resources.map(item => (
            <Option value={item.id} key={item.id} name={item.resourceName}>
                {item.resourceName}
            </Option>
        ))
        const showResource = currentPage.taskType != TASK_TYPE.DATA_COLLECTION;
        return (
            <div className="m-taksdetail">
                <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                    <Panel key="1" header="任务属性">
                        <Row className="task-info">
                            <Row>
                                <Col span="10" className="txt-right">任务名称：</Col>
                                <Col span="14">
                                    {currentPage.name}
                                </Col>
                            </Row>
                            <Row>
                                <Col span="10" className="txt-right">任务类型：</Col>
                                <Col span="14">{this.getTaskName(currentPage.taskType)}</Col>
                            </Row>
                            {showResource && <Row>
                                <Col span="10" className="txt-right">资源：</Col>
                                <Col span="14" style={{ marginTop: '10px' }}>{taskRes}
                                    {/* <a onClick={() => { this.setState({ visibleAlterRes: true }) }}>修改</a>*/}
                                </Col>
                            </Row>}
                            <Row>
                                <Col span="10" className="txt-right">创建人员：</Col>
                                <Col span="14">{currentPage.createUserName}</Col>
                            </Row>
                            <Row>
                                <Col span="10" className="txt-right">创建时间：</Col>
                                <Col span="14">
                                    {utils.formatDateTime(currentPage.gmtCreate)}
                                </Col>
                            </Row>
                            <Row>
                                <Col span="10" className="txt-right">最近修改时间：</Col>
                                <Col span="14">{utils.formatDateTime(currentPage.gmtModified)}</Col>
                            </Row>
                            <Row>
                                <Col span="10" className="txt-right">描述：</Col>
                                <Col span="14">{currentPage.taskDesc}</Col>
                            </Row>
                            <Modal
                                title="修改任务资源"
                                wrapClassName="vertical-center-modal"
                                visible={visibleAlterRes}
                                onCancel={() => { this.setState({ visibleAlterRes: false }) }}
                                onOk={this.alterRes}
                            >
                                <Select
                                    mode={currentPage.taskType === 0 ? 'multiple' : ''}
                                    style={{ width: '100%' }}
                                    showSearch
                                    value={resList}
                                    placeholder="请选择资源"
                                    optionFilterProp="name"
                                    onChange={this.handleChange}
                                >
                                    {resOptions}
                                </Select>
                            </Modal>
                        </Row>
                    </Panel>
                    <Panel key="2" header={`历史提交版本`}>
                        <TaskVersion
                            taskInfo={currentPage}
                            changeSql={editorChange}
                            taskType="realTimeTask"
                            editor={editor}
                        />
                    </Panel>
                </Collapse>
            </div>
        )
    }
}
