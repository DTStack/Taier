import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { debounce } from 'lodash';

import {
    Row, Col, Button,
    message, Modal, Tag
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import MyIcon from '../../../components/icon'

import * as ModalAction from '../../../store/modules/realtimeTask/modal'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import * as TreeAction from '../../../store/modules/realtimeTask/tree'
import { modalAction } from '../../../store/modules/realtimeTask/actionTypes'
import { MENU_TYPE, TASK_TYPE } from '../../../comm/const';

import TaskBrowser from './taskBrowser'

const confirm = Modal.confirm;

class TaskIndex extends Component {

    componentDidMount() {}

    saveTask = () => {
        const { currentPage, dispatch } = this.props
        const resList = currentPage.resourceList
        if (resList && resList.length > 0) {
            currentPage.resourceIdList = resList.map(item => item.id)
        }
        currentPage.lockVersion = currentPage.readWriteLockVO.version; 
        Api.saveTask(currentPage).then((res) => {

            const updatePageStatus = (pageData) => {
                message.success('任务保存成功')
                pageData.notSynced = false;// 添加已保存标记
                dispatch(BrowserAction.setCurrentPage(pageData))
                // 如果mr任务更新，则需要刷新左侧文件树
                if (currentPage.taskType === TASK_TYPE.MR) {
                    dispatch(TreeAction.getRealtimeTree({
                        id: pageData.parentId,
                        catalogueType: MENU_TYPE.TASK
                    }))
                }
            }

            if (res.code === 1) {
                const lockInfo = res.data.readWriteLockVO;
                const lockStatus = lockInfo.result
                if (lockStatus === 0) { // 1-正常，2-被锁定，3-需同步
                    updatePageStatus(res.data)
                // 如果是锁定状态，点击确定按钮，强制更新，否则，取消保存
                } else if (lockStatus === 1) { // 2-被锁定
                    confirm({
                        title: '锁定提醒', // 锁定提示
                        content: <span>
                            文件正在被{lockInfo.lastKeepLockUserName}编辑中，开始编辑时间为
                            {utils.formatDateTime(lockInfo.gmtModified)}。
                            强制保存可能导致{lockInfo.lastKeepLockUserName}对文件的修改无法正常保存！
                        </span>,
                        okText: '确定保存',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            const succCall = (res) => {
                                if (res.code === 1) updatePageStatus(res.data)
                            }
                            Api.forceUpdateTask(currentPage).then(succCall)
                        },
                    });
                // 如果同步状态，则提示会覆盖代码，
                // 点击确认，重新拉取代码并覆盖当前代码，取消则退出
                } else if (lockStatus === 2) { // 3-需同步
                    confirm({
                        title: '保存警告',
                        content: <span>
                            文件已经被{lockInfo.lastKeepLockUserName}编辑过，编辑时间为
                            {utils.formatDateTime(lockInfo.gmtModified)}。
                            点击确认按钮会<Tag color="orange">覆盖</Tag>
                            您本地的代码，请您提前做好备份！
                        </span>,
                        okText: '确定覆盖',
                        okType: 'danger',
                        cancelText: '取消',
                        onOk() {
                            const reqParams = {
                                id: currentPage.id,
                                lockVersion: lockInfo.version,
                            }
                            Api.getTask(reqParams).then(res => {
                                if (res.code === 1) {
                                    const taskInfo = res.data
                                    taskInfo.merged = true;
                                    updatePageStatus(taskInfo)
                                }
                            })
                        },
                    });
                }
            }
        })
    }

    editorChange = (old, newVal) => {
        const { currentPage, dispatch } = this.props
        if (old !== newVal) {
            currentPage.sqlText = newVal;
            currentPage.notSynced = true;// 添加未保存标记
            dispatch(BrowserAction.setCurrentPage(currentPage))
        }
    }

    debounceChange = debounce(this.editorChange, 300, { 'maxWait': 2000 })

    editorParamsChange = (old, newVal) => {
        const { currentPage, dispatch } = this.props
        if (old !== newVal) {
            currentPage.taskParams = newVal
            dispatch(BrowserAction.setCurrentPage(currentPage))
        }
    }

    startTask = () => {
        const { currentPage, dispatch } = this.props
        Api.startTask({
            id: currentPage.id,
            isRestoration: 0, // 0-false, 1-true
        }).then((res) => {
            if (res.code === 1) {
                currentPage.status = 10
                dispatch(BrowserAction.setCurrentPage(Object.assign({}, currentPage)))
                message.success('任务已经成功提交！')
            }
        })
    }

    autoSaveTask() {
        const ctx = this
        this.timerID = setInterval(() => {
            if (ctx.state.editorState === 'edit') {
                const task = ctx.props.currentPage
                Api.saveTask(task).then((res) => {
                    if (res.code === 1) {
                        message.success('任务已自动保存')
                    }
                })
            }
        }, 30000)
    }

    loadTreeData = (treeNode) => {
        const { dispatch } = this.props
        const node = treeNode.props.data
        return new Promise((resolve) => {
            dispatch(TreeAction.getRealtimeTree(node))
            resolve();
        })
    }

    render() {
        const { dispatch, currentPage } = this.props

        const canSubmit = currentPage.status === 0;

        return (
            <Row className="task-editor">
                <header className="toolbar bd-bottom clear">
                    <Col className="left">
                        <Button
                        onClick={() => { 
                              dispatch(ModalAction.updateModal(modalAction.ADD_TASK_VISIBLE)) 
                        }}
                          title="创建任务"
                        >
                          <MyIcon className="my-icon" type="focus" /> 新建任务
                        </Button>
                        <Button
                          disabled={currentPage.invalid}
                          onClick={this.saveTask}
                          title="保存任务"
                        >
                          <MyIcon className="my-icon" type="save" />保存
                        </Button>
                    </Col>
                    <Col className="right">
                        {/* <Button disabled={!canSubmit}>
                            <MyIcon className="my-icon" type="fly" /> 发布
                        </Button> */}
                        <Link to={`/operation/realtime?tname=${currentPage.name}`}>
                            <Button>
                                <MyIcon className="my-icon" type="goin" /> 运维
                            </Button>
                        </Link>
                    </Col>
                </header>
                <TaskBrowser
                  {...this.props}
                  ayncTree={this.loadTreeData}
                  editorParamsChange={this.editorParamsChange}
                  editorChange={this.debounceChange}
                />
            </Row>
        )
    }
}

export default connect((state) => {
    const { resources, pages, currentPage } = state.realtimeTask
    return {
        currentPage,
        pages,
        resources,
    }
})(TaskIndex)
