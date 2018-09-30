import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Link } from 'react-router'
import { debounce, cloneDeep } from 'lodash';

import {
    Row, Col, Button,
    message, Modal, Tag, Form, Input, Icon,
    Tooltip,
} from 'antd'

import utils from 'utils'

import FullScreenButton from 'widgets/fullscreen';

import Api from '../../../api'
import MyIcon from '../../../components/icon'
import ThemeSwitcher from '../../../components/theme-switcher';

import * as ModalAction from '../../../store/modules/realtimeTask/modal'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import * as TreeAction from '../../../store/modules/realtimeTask/tree'
import { actions as collectionActions } from '../../../store/modules/realtimeTask/collection';
import { modalAction } from '../../../store/modules/realtimeTask/actionTypes'
import { showSeach } from '../../../store/modules/comm';
import { updateEditorOptions } from '../../../store/modules/editor/editorAction';

import TaskBrowser from './taskBrowser'
import { MENU_TYPE, TASK_TYPE, formItemLayout, DATA_SYNC_TYPE } from '../../../comm/const';

const confirm = Modal.confirm;
const FormItem = Form.Item;

class TaskIndex extends Component {

    state = {
        publishDesc: "",
        showPublish: false,
    }

    componentDidMount() {
        const { dispatch } = this.props;
        const taskId = utils.getParameterByName("taskId")
        if (taskId) {
            this.props.dispatch(BrowserAction.openPage({ id: taskId }))
        }
    }

    saveTask = () => {
        const { currentPage, dispatch, inputData, outputData, dimensionData } = this.props;
        console.log('saveTask', this.props);

        //检查页面输入输出参数配置
        const { checkFormParams = [], panelColumn = [] } = inputData[currentPage.id] || {};
        const { checkFormParams: outputCheckFormParams = [], panelColumn: outputPanelColumn = [] } = outputData[currentPage.id] || {};
        const { checkFormParams: dimensionCheckFormParams = [], panelColumn: dimensionPanelColumn = [] } = dimensionData[currentPage.id] || {};

        for (let index = 0, len = checkFormParams.length; index < len; index++) {//检查出一个未填选项,不再检查其它的选项,只弹一次错误
            const result = checkFormParams[index].checkParams();
            console.log('result', result);

            if (!result.status) {
                return message.error(`源表--输入源${checkFormParams[index].props.index + 1}: ${result.message || "您还有未填选项"}`);
            }
        }

        if (outputCheckFormParams.length > 0) {
            for (let index = 0, len = outputCheckFormParams.length; index < len; index++) {//检查出一个未填选项,不再检查其它的选项,只弹一次错误
                const result = outputCheckFormParams[index].checkParams();
                if (!result.status) {
                    return message.error(`结果表--输出源${outputCheckFormParams[index].props.index + 1}: ${result.message || "您还有未填选项"}`);
                }
            }
        }
        if (dimensionCheckFormParams.length > 0) {
            for (let index = 0, len = dimensionCheckFormParams.length; index < len; index++) {//检查出一个未填选项,不再检查其它的选项,只弹一次错误
                const result = dimensionCheckFormParams[index].checkParams();
                if (!result.status) {
                    return message.error(`维表--维表${dimensionCheckFormParams[index].props.index + 1}: ${result.message || "您还有未填选项"}`);
                }
            }
        }
        const resList = currentPage.resourceList;
        currentPage.preSave = true;
        if (resList && resList.length > 0) {
            currentPage.resourceIdList = resList.map(item => item.id)
        }
        if (panelColumn.length > 0) {
            currentPage.source = panelColumn;
        }
        if (outputPanelColumn.length > 0) {
            currentPage.sink = outputPanelColumn;
        }
        if (dimensionPanelColumn.length > 0) {
            currentPage.side = dimensionPanelColumn;
        }
        currentPage.lockVersion = currentPage.readWriteLockVO.version;

        return new Promise((resolve, reject) => {
            Api.saveTask(currentPage).then((res) => {

                const updatePageStatus = (pageData) => {
                    message.success('任务保存成功')
                    pageData.notSynced = false;// 添加已保存标记
                    dispatch(BrowserAction.setCurrentPage(pageData))
                    // 如果mr任务更新，则需要刷新左侧文件树
                    if (currentPage.taskType === TASK_TYPE.MR) {
                        dispatch(TreeAction.getRealtimeTree({
                            id: pageData.nodePid,
                            catalogueType: MENU_TYPE.TASK_DEV
                        }))
                    }
                    if(currentPage.taskType==TASK_TYPE.DATA_COLLECTION&&currentPage.createModel==DATA_SYNC_TYPE.GUIDE){
                        dispatch(collectionActions.initCollectionTask(currentPage.id))
                        dispatch(collectionActions.getDataSource())
                    }
                    resolve(true)
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
        })
    }

    editorChange = (data) => {
        let { currentPage, dispatch } = this.props;
        currentPage = cloneDeep(currentPage);
        currentPage = Object.assign(currentPage, data);
        currentPage.notSynced = true; // 添加未保存标记
        dispatch(BrowserAction.setCurrentPage(currentPage));
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

    closePublish = () => {
        this.setState({
            publishDesc: '',
            showPublish: false,
        })
    }

    searchTask = () => {
        this.props.dispatch(showSeach(true));
    }

    submitTab() {
        const {
            currentPage, dispatch
        } = this.props;

        const { publishDesc } = this.state;
        const result = cloneDeep(currentPage);

        // 添加提交描述信息
        if (publishDesc) {

            if (publishDesc.length > 200) {
                message.error('备注信息不可超过200个字符！')
                return false;
            }

            result.publishDesc = publishDesc;
        } else {
            message.error('提交备注不可为空！')
            return false;
        }
        // 修改task配置时接口要求的标记位
        result.preSave = true;
        result.submitStatus = 1; // 1-提交，0-保存

        BrowserAction.publishTask(result)
            .then(
                (result) => {
                    this.closePublish();
                    if (result) {
                        message.success('提交成功！');
                        Api.getTask({ id: currentPage.id }).then(res => {
                            if (res.code === 1) {
                                const taskInfo = res.data
                                taskInfo.merged = true;
                                taskInfo.notSynced = false;// 添加已保存标记
                                dispatch(BrowserAction.setCurrentPage(taskInfo))
                                if(taskInfo.taskType==TASK_TYPE.DATA_COLLECTION&&taskInfo.createModel==DATA_SYNC_TYPE.GUIDE){
                                    dispatch(collectionActions.initCollectionTask(taskInfo.id))
                                    dispatch(collectionActions.getDataSource())
                                }
                            }
                        })
                    }
                }
            );
    }

    publishChange = (e) => {
        this.setState({
            publishDesc: e.target.value
        })
    }

    renderPublish = () => {
        const { user } = this.props;
        const { publishDesc } = this.state
        return (
            <Modal
                wrapClassName="vertical-center-modal"
                title="提交任务"
                style={{ height: '600px', width: '600px' }}
                visible={this.state.showPublish}
                onCancel={this.closePublish}
                onOk={this.submitTab.bind(this)}
                cancelText="关闭"
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="提交人"
                        hasFeedback
                    >
                        <span>{user.userName}</span>
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label={(
                            <span className="ant-form-item-required">备注</span>
                        )}
                        hasFeedback
                    >
                        <Input
                            type="textarea"
                            value={publishDesc}
                            name="publishDesc" rows={4}
                            onChange={this.publishChange}
                        />
                    </FormItem>
                </Form>
            </Modal>
        )
    }

    render() {
        const { dispatch, currentPage, editor } = this.props
        const disablePublish = currentPage.notSynced;
        const themeDark = editor.options.theme !== 'vs' ? true : undefined;

        return (
            <Row className="task-editor">
                <header className="toolbar clear">
                    <Col className="left">
                        <span>
                            <Button
                                onClick={() => {
                                    dispatch(ModalAction.updateModal(modalAction.ADD_TASK_VISIBLE))
                                }}
                                title="创建任务"
                            >
                                <MyIcon className="my-icon" type="focus" themeDark={themeDark}/> 新建任务
                                </Button>
                            <Button
                                disabled={currentPage.invalid}
                                onClick={this.saveTask}
                                title="保存任务"
                            >
                                <MyIcon className="my-icon" type="save" themeDark={themeDark}/>保存
                            </Button>
                            <Button
                                onClick={this.searchTask}
                                title="打开任务"
                            >
                                <MyIcon className="my-icon" type="search" themeDark={themeDark}/>
                                搜索
                            </Button>
                        </span>
                        <FullScreenButton themeDark={themeDark}/>
                        <ThemeSwitcher 
                            editorTheme={editor.options.theme}
                            onThemeChange={(theme) => {
                                dispatch(updateEditorOptions({ theme }))
                            }}
                        />
                    </Col>
                    <Col className="right">
                        <span>
                            <Tooltip
                                placement="bottom"
                                title="提交到调度系统"
                                mouseLeaveDelay={0}
                            >
                                <Button disabled={disablePublish} onClick={() => { this.setState({ showPublish: true }) }}>
                                    <Icon type="upload" /> 提交
                            </Button>
                            </Tooltip>
                        </span>
                        <Link to={`/operation/realtime?tname=${currentPage.name}`}>
                            <Button>
                                <MyIcon className="my-icon" type="goin" themeDark={themeDark}/> 运维
                            </Button>
                        </Link>
                    </Col>
                </header>
                <TaskBrowser
                    {...this.props}
                    ayncTree={this.loadTreeData}
                    editorParamsChange={this.editorParamsChange}
                    editorChange={this.debounceChange}
                    saveTask={this.saveTask.bind(this)}
                />
                {this.renderPublish()}
            </Row>
        )
    }
}

export default connect((state) => {
    const { resources, pages, currentPage, inputData, outputData, dimensionData } = state.realtimeTask;
    const { user, project, editor } = state;
    return {
        currentPage,
        pages,
        resources,
        user,
        inputData,
        outputData,
        dimensionData,
        project,
        editor,
    }
})(TaskIndex) 
