import * as React from 'react';
import { connect } from 'react-redux';
import {
    Row, Col, Button, message, Input, Form,
    Tabs, Menu, Dropdown, Icon, Modal, Tooltip
} from 'antd';
import { hashHistory } from 'react-router';

import { cloneDeep, isEmpty, assign } from 'lodash';

import utils from 'utils'
import FullScreenButton from 'widgets/fullscreen';

import ajax from '../../../api';

import { formItemLayout, TASK_TYPE, DATA_SYNC_TYPE, PROJECT_TYPE, DATA_SYNC_MODE } from '../../../comm/const';
import MyIcon from '../../../components/icon';
import SyncBadge from '../../../components/sync-badge';
import TabIcon from '../../../components/tab-icon';
import ThemeSwitcher from 'main/components/theme-switcher';
import UploaderProgressBar from '../../../components/uploader-progress';

import MainBench from './mainBench';
import SiderBench from './siderBench';
import ImportData from './dataImport';

import { showSeach } from '../../../store/modules/comm';
import {
    workbenchActions,
    getDataSyncReqParams
} from '../../../store/modules/offlineTask/offlineAction';
import { updateEditorOptions } from '../../../store/modules/editor/editorAction';

import { isProjectCouldEdit } from '../../../comm';
import { UPLOAD_STATUS } from '../../../store/modules/uploader';

const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
const FormItem = Form.Item;

class Workbench extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }

    state: any = {
        visible: false,
        submitLoading: false
    }

    handleMenuClick = (e: any) => {
        if (e.key === '1') {
            const upload = document.getElementById('JS_importFile')
            if (upload) {
                upload.click();
            }
        }
    }

    handleCreateClick = (e: any) => {
        if (e.key === 'task') {
            this.props.toggleCreateTask()
        } else if (e.key === 'script') {
            this.props.toggleCreateScript()
        }
    }

    createMenu = () => {
        const { scriptTreeData } = this.props
        return (
            <Menu onClick={this.handleCreateClick}>
                <Menu.Item key="task">创建任务</Menu.Item>
                {scriptTreeData && <Menu.Item key="script">创建脚本</Menu.Item>}
            </Menu>
        )
    }

    importMenu = () => {
        return (
            <Menu onClick={this.handleMenuClick}>
                <Menu.Item key="1">导入本地数据</Menu.Item>
            </Menu>
        )
    }

    toPublishView () {
        hashHistory.push({
            pathname: '/package/create',
            query: {
                type: 'offline'
            }
        })
    }

    showPublish () {
        const { currentTabData } = this.props;
        const { taskType } = currentTabData;
        let vaildPass = true;

        switch (taskType) {
            case TASK_TYPE.SYNC: {
                if (currentTabData.createModel == DATA_SYNC_TYPE.SCRIPT) {
                    vaildPass = this.checkSyncScript(currentTabData);
                }
            }
        }

        if (vaildPass) {
            this.props.togglePublishModal(true)
        }
    }

    checkSyncScript (currentTabData: any) {
        const sql = currentTabData.sqlText;

        if (utils.jsonFormat(sql)) {
            return true;
        }
        message.error('请确认JSON格式是否正确');
        return false;
    }

    searchTask = () => {
        this.props.dispatch(showSeach(true));
    }
    showConfirmModal = () => {
        this.props.toggleConfirmModal(true)
    }
    renderConfirSave = () => {
        return (
            <Modal
                title='提交'
                visible={this.props.confirmSaveVisible}
                onCancel={() => {
                    this.props.toggleConfirmModal(false)
                }}
                onOk={() => {
                    this.saveTab(true, 'popOut')
                }}
            >
                <p>文件被修改，是否需要保存?</p>
            </Modal>
        )
    }
    renderPublish = () => {
        const { user } = this.props;
        const { publishDesc, submitLoading } = this.state;
        return (
            <Modal
                wrapClassName="vertical-center-modal"
                title="提交任务"
                style={{ height: '600px', width: '600px' }}
                visible={this.props.showPublish}
                onCancel={this.closePublish}
                onOk={this.submitTab.bind(this)}
                confirmLoading={submitLoading}
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
                            <span>备注</span>
                        )}
                        hasFeedback
                    >
                        <Input.TextArea
                            value={publishDesc}
                            name="publishDesc"
                            rows={4}
                            onChange={this.publishChange}
                        />
                    </FormItem>
                </Form>
                <Row>
                    <Col offset={6} span={15}>
                        注意：提交过的任务才能被调度执行及发布到其他项
                    </Col>
                </Row>
            </Modal>
        )
    }

    renderGlobalMessage = () => {
        const { uploader } = this.props;
        return <UploaderProgressBar key={uploader.status} uploader={uploader} />
    }

    render () {
        const {
            tabs, currentTab, currentTabData,
            dataSync, taskCustomParams,
            closeTab, closeAllorOthers, project,
            user, editor, dispatch, uploader
        } = this.props;

        const { sourceMap, targetMap } = dataSync;
        const { theReqIsEnd } = this.props;
        const isTest = project.projectType == PROJECT_TYPE.TEST;
        const couldEdit = isProjectCouldEdit(project, user);
        let isSaveAvaliable = false;
        if (!isEmpty(sourceMap) && !isEmpty(targetMap)) isSaveAvaliable = true;

        // 不属于数据同步或者属于数据同步的脚本模式都可以保存
        if (
            currentTabData &&
            (currentTabData.taskType !== TASK_TYPE.SYNC ||
                (currentTabData.createModel == DATA_SYNC_TYPE.SCRIPT && currentTabData.taskType == TASK_TYPE.SYNC)
            )
        ) {
            isSaveAvaliable = true;
        }

        isSaveAvaliable = (currentTabData && !currentTabData.invalid) || !theReqIsEnd || (currentTabData && !currentTabData.notSynced);

        // 被锁就不能保存了
        if (currentTabData && currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock) {
            isSaveAvaliable = false;
        }

        const isTask = currentTabData && utils.checkExist(currentTabData.taskType)
        const isWorkflowNode = currentTabData && currentTabData.flowId && currentTabData.flowId !== 0;

        const disablePublish = !isTask || currentTabData.notSynced || isWorkflowNode;
        const disableSubmit = !isTask || isWorkflowNode;
        const isModify = currentTabData.notSynced; // 是否被修改
        const showPublish = isTask;

        const themeDark = editor.options.theme !== 'vs' ? true : undefined;
        const disableImport = uploader.status === UPLOAD_STATUS.PROGRESSING;

        return <Row className="m-workbench task-editor">
            <header className="workbench-toolbar clear">
                <Col className="left">

                    {couldEdit && (
                        <span>
                            <Dropdown overlay={this.createMenu()} trigger={['click']}>
                                <Button {...{ title: '创建' }}>
                                    <MyIcon className="my-icon" type="focus" themeDark={themeDark} />
                                    新建<Icon type="down" />
                                </Button>
                            </Dropdown>
                            <Button
                                onClick={this.saveTab.bind(this, true, 'button')}
                                {...{ title: '保存任务' }}
                                disabled={!isSaveAvaliable}
                            >
                                <MyIcon className="my-icon" type="save" themeDark={themeDark} />保存
                            </Button>
                        </span>
                    )}
                    <Dropdown disabled={disableImport} overlay={this.importMenu()} trigger={['click']}>
                        <Button>
                            <MyIcon className="my-icon" type="import" themeDark={themeDark} />
                            导入<Icon type="down" />
                        </Button>
                    </Dropdown>
                    <Button
                        onClick={this.searchTask}
                        {...{ title: '打开任务' }}
                    >
                        <MyIcon className="my-icon" type="search" themeDark={themeDark} />
                        搜索
                    </Button>
                    <FullScreenButton themeDark={themeDark} />
                    <ThemeSwitcher
                        editorTheme={editor.options.theme}
                        onThemeChange={(theme: any) => {
                            dispatch(updateEditorOptions({ theme }))
                        }}
                    />
                </Col>
                {showPublish ? (<Col className="right">

                    {couldEdit && (<span>
                        <Tooltip
                            placement="bottom"
                            title="提交到调度系统"
                            mouseLeaveDelay={0}
                        >
                            <Button
                                disabled={disableSubmit}
                                onClick={
                                    isModify ? this.showConfirmModal : this.showPublish.bind(this)
                                }
                            >
                                <Icon type="upload" {...{ themeDark: themeDark }} />提交
                            </Button>
                        </Tooltip>
                        {isTest && <Tooltip
                            placement="bottom"
                            title="发布到目标项目"
                            mouseLeaveDelay={0}
                        >
                            <Button
                                disabled={disablePublish}
                                onClick={this.toPublishView.bind(this)}
                            >
                                <MyIcon className="my-icon" type="fly" themeDark={themeDark} />发布
                            </Button>
                        </Tooltip>}
                    </span>)}

                    <a href={`${location.pathname}#/operation/offline-management?tname=${currentTabData && currentTabData.name}`}>
                        <Button disabled={!isTask}>
                            <MyIcon className="my-icon" type="goin" themeDark={themeDark} /> 运维
                        </Button>
                    </a>
                </Col>) : null}
                { this.renderGlobalMessage() }
            </header>
            <Row className="task-browser">
                <div className="browser-content">
                    <Tabs
                        hideAdd
                        onTabClick={this.switchTab.bind(this, currentTab)}
                        activeKey={`${currentTab}`}
                        type="editable-card"
                        className="browser-tabs"
                        onEdit={(tabId: any) => closeTab(tabId, tabs)}
                        tabBarExtraContent={<Dropdown overlay={
                            <Menu style={{ marginRight: 2, maxHeight: '500px', overflowY: 'auto' }}
                            >
                                <Menu.Item key="OHTERS">
                                    <a onClick={() => closeAllorOthers('OHTERS', tabs, currentTab)}>关闭其他</a>
                                </Menu.Item>
                                <Menu.Item key="ALL">
                                    <a onClick={() => closeAllorOthers('ALL', tabs, currentTab)} >关闭所有</a>
                                </Menu.Item>
                                <Menu.Divider />
                                {tabs.map((tab: any) => {
                                    return <Menu.Item key={tab.id} >
                                        <a
                                            onClick={this.switchTab.bind(this, currentTab, tab.id)}
                                            style={tab.id == currentTab ? { color: '#2491F7' } : {}}
                                        >
                                            {tab.name}
                                        </a>
                                    </Menu.Item>
                                })}
                            </Menu>
                        }>
                            <Icon type="bars" style={{ margin: '7 0 0 0', fontSize: 18 }} />
                        </Dropdown>}
                    >
                        {this.renderTabs(tabs)}
                    </Tabs>
                    <MainBench
                        tabs={tabs}
                        tabData={currentTabData}
                        taskCustomParams={taskCustomParams}
                        updateTaskFields={this.props.updateTaskField}
                        updateCatalogue={this.props.updateCatalogue}
                        loadTreeNode={this.props.loadTreeNode}
                        reloadWorkflowTabNode={this.props.reloadWorkflowTabNode}
                        saveTab={this.saveTab.bind(this, true)}
                    />
                    <SiderBench tabData={currentTabData} key={currentTabData && currentTabData.id} />
                </div>
            </Row>
            <ImportData />
            {this.renderConfirSave()}
            {this.renderPublish()}
        </Row>
    }

    publishChange = (e: any) => {
        this.setState({
            publishDesc: e.target.value
        })
    }

    closePublish = () => {
        this.setState({
            publishDesc: ''
        })
        this.props.togglePublishModal(false)
    }

    renderTabs (tabs: any) {
        if (tabs && tabs.length > 0) {
            return tabs.map((tab: any) => {
                let title = (<div style={{ lineHeight: 0 }}>
                    <TabIcon tabData={tab} />
                    <span className="tab-ellipsis" title={tab.name}>{tab.name}</span>
                    <SyncBadge notSynced={tab.notSynced} />
                </div>);

                if (tab.flowId) {
                    title = (<div style={{ lineHeight: 0 }}>
                        <TabIcon tabData={tab} />
                        <span className="tab-ellipsis">
                            <a className="workflow-name" title={tab.flowName} onClick={() => this.switchTab(this.props.currentTab, tab.flowId)}>
                                {tab.flowName}
                            </a>
                            <span className="normal-tab" title={tab.name}>&nbsp;/ {tab.name}</span>
                        </span>
                        <SyncBadge className="tab-ellipsis" notSynced={tab.notSynced} />
                    </div>);
                }

                return (
                    <TabPane
                        style={{ height: '0px' }}
                        tab={title}
                        key={tab.id}
                    />
                );
            });
        }
        return []
    }

    saveTab (isSave: any, saveMode: any) {
        const isButtonSubmit = saveMode == 'popOut';
        this.props.isSaveFInish(true)
        const { saveTab, currentTabData } = this.props;
        if (currentTabData.taskType === TASK_TYPE.CUBE_KYLIN) {
            const exeArgsToJson = currentTabData.exeArgs ? JSON.parse(currentTabData.exeArgs) : '';
            if (exeArgsToJson) {
                let { sourceId, cubeName, isUseSystemVar, startTime, endTime, systemVar, noPartition }: any = { ...exeArgsToJson };
                const isCubeOk = sourceId && cubeName && ((isUseSystemVar && systemVar) || (startTime && endTime));
                if (!noPartition && !isCubeOk) {
                    message.warning('请完善相关参数！');
                    return false;
                }
            }
        }
        // 如果是工作流任务，需要对保存操作提前做校验
        if (
            currentTabData.taskType === TASK_TYPE.WORKFLOW &&
            currentTabData.toUpdateTasks &&
            currentTabData.toUpdateTasks.length > 0
        ) {
            message.warning('您有工作流节点任务未保存！')
            return false;
        }

        let saveData = this.generateRqtBody();

        let type = 'task'
        // 非任务类型，脚本类型
        if (!utils.checkExist(currentTabData.taskType)) {
            saveData = currentTabData;
            type = 'script';
        }

        saveTab(saveData, isSave, type, isButtonSubmit);
    }

    submitTab () {
        const {
            publishTask,
            currentTab, reloadTaskTab
        } = this.props;

        const { publishDesc = '' } = this.state
        const result = this.generateRqtBody()

        // 添加发布描述信息

        if (publishDesc.length > 200) {
            message.error('备注信息不可超过200个字符！')
            return false;
        }

        result.publishDesc = publishDesc;// 发布信息
        this.setState({
            submitLoading: true
        })
        ajax.publishOfflineTask(result).then((res: any) => {
            this.setState({
                submitLoading: false
            })
            if (res.code === 1) {
                message.success('提交成功！');
                publishTask(res);
                reloadTaskTab(currentTab);
                this.closePublish();
            } else {
                this.closePublish();
            }
        });
    }

    switchTab (currentTab: any, tabId: any) {
        const { openTab, tabs } = this.props;
        +tabId !== currentTab && openTab({ id: +tabId, tabs });
    }

    closeTab (tabId: any) {
        const { closeTab, tabs } = this.props;

        let dirty = tabs.filter((tab: any) => {
            return tab.id == tabId
        })[0].notSynced;

        if (!dirty) {
            closeTab(+tabId);
        } else {
            confirm({
                title: '修改尚未同步到服务器，是否强制关闭 ?',
                content: '强制关闭将丢弃当前修改数据',
                onOk () {
                    closeTab(+tabId);
                },
                onCancel () { }
            });
        }
    }

    /**
     * @description 拼装接口所需数据格式
     * @param {any} data 数据同步job配置对象
     * @returns {any} result 接口所需数据结构
     * @memberof DataSync
     */
    generateRqtBody () {
        const { currentTabData, dataSync } = this.props;

        // deepClone避免直接mutate store
        let reqBody = cloneDeep(currentTabData);
        // 如果当前任务为数据同步任务
        if (currentTabData.id === dataSync.tabId) {
            const isIncrementMode = currentTabData.syncModel !== undefined && DATA_SYNC_MODE.INCREMENT === currentTabData.syncModel;
            reqBody = assign(reqBody, getDataSyncReqParams(dataSync));
            if (!isIncrementMode) {
                reqBody.sourceMap.increColumn = undefined; // Delete increColumn
            }
        }
        // 修改task配置时接口要求的标记位
        reqBody.preSave = true;

        // 接口要求上游任务字段名修改为dependencyTasks
        if (reqBody.taskVOS) {
            reqBody.dependencyTasks = reqBody.taskVOS.map((o: any) => o);
            reqBody.taskVOS = null;
        }

        // 删除不必要的字段
        delete reqBody.taskVersions;
        delete reqBody.dataSyncSaved;

        // 数据拼装结果
        return reqBody;
    }
}

const mapState = (state: any) => {
    const { workbench, dataSync, scriptTree, modalShow } = state.offlineTask;
    const { currentTab, tabs, taskCustomParams } = workbench;
    const { confirmSaveVisible, showPublish, theReqIsEnd } = modalShow
    const currentTabData = tabs.filter((tab: any) => {
        return tab.id === currentTab;
    })[0];

    return {
        currentTab,
        currentTabData,
        tabs,
        dataSync,
        taskCustomParams,
        confirmSaveVisible,
        showPublish,
        theReqIsEnd,
        user: state.user,
        uploader: state.uploader,
        scriptTreeData: scriptTree,
        project: state.project,
        editor: state.editor
    };
};

export default connect(mapState, workbenchActions)(Workbench);
