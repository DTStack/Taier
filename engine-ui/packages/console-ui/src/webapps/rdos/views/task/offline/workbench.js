import React from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router'
import {
    Row, Col, Button, message, Input, Form,
    Tabs, Menu, Dropdown, Icon, Modal, Tag,
} from 'antd';

import { cloneDeep, isEmpty } from 'lodash';

import utils from 'utils'
import FullScreenButton from 'widgets/fullscreen';

import ajax from '../../../api';

import { formItemLayout, TASK_TYPE, DATA_SYNC_TYPE } from '../../../comm/const';
import MyIcon from '../../../components/icon';
import SyncBadge from '../../../components/sync-badge';

import MainBench from './mainBench';
import SiderBench from './siderBench';
import ImportData from './dataImport';

import {
    modalAction,
    workbenchAction,
    taskTreeAction,
    editorAction,
} from '../../../store/modules/offlineTask/actionType';

import {
    stopSql
} from '../../../store/modules/editor/editorAction';

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction';

const TabPane = Tabs.TabPane;
const confirm = Modal.confirm;
const FormItem = Form.Item;

class Workbench extends React.Component {

    constructor(props) {
        super(props);
    }

    state = {
        visible: false,
        showPublish: false,
        theReqIsEnd: true,
    }

    handleMenuClick = (e) => {
        if (e.key === '1') {
            const upload = document.getElementById('importFile')
            if (upload) {
                upload.click();
            }
        }
    }

    handleCreateClick = (e) => {
        if (e.key === 'task') {
            this.props.toggleCreateTask()
        }
        else if (e.key === 'script') {
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

    showPublish() {
        const { currentTabData } = this.props;
        const { taskType, createModel } = currentTabData;
        let vaildPass = true;

        switch(taskType){
            case TASK_TYPE.SYNC:{
                if(currentTabData.createModel==DATA_SYNC_TYPE.SCRIPT){
                    vaildPass = this.checkSyncScript(currentTabData);
                }
            }
        }

        if(vaildPass){
            this.setState({ showPublish: true })
        }
    }

    checkSyncScript(currentTabData){
        const sql = currentTabData.sqlText;

        if(utils.jsonFormat(sql)){
            return true;
        }
        message.error("请确认JSON格式是否正确");
        return false;
    }

    renderPublish = () => {
        const { user } = this.props;
        const { publishDesc } = this.state;
        return (
            <Modal
                wrapClassName="vertical-center-modal"
                title="发布任务"
                style={{ height: '600px', width: '600px' }}
                visible={this.state.showPublish}
                onCancel={this.closePublish}
                onOk={this.submitTab.bind(this)}
                cancelText="关闭"
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="发布人"
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
        const { 
            tabs, currentTab, currentTabData,
            dataSync, taskCustomParams, 
            closeTab 
        } = this.props;

        const { sourceMap, targetMap } = dataSync;
        const { theReqIsEnd } = this.state;
        let isSaveAvaliable = false;

        if (!isEmpty(sourceMap) && !isEmpty(targetMap)) isSaveAvaliable = true;
        if (
            currentTabData &&
            (currentTabData.taskType !== TASK_TYPE.SYNC ||
                (currentTabData.createModel == DATA_SYNC_TYPE.SCRIPT && currentTabData.taskType == TASK_TYPE.SYNC)
            )
        ) isSaveAvaliable = true;

        if (currentTabData && currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock) {
            isSaveAvaliable = false;
        }

        isSaveAvaliable = (currentTabData && !currentTabData.invalid) || !theReqIsEnd;

        const isTask = currentTabData && utils.checkExist(currentTabData.taskType)

        const disablePublish = !isTask || currentTabData.notSynced
        const showPublish = isTask;

        return <Row className="m-workbench task-editor">
            <header className="toolbar bd-bottom clear">
                <Col className="left">
                    <Dropdown overlay={this.createMenu()} trigger={['click']}>
                        <Button title="创建">
                            <MyIcon className="my-icon" type="focus" />
                            新建<Icon type="down" />
                        </Button>
                    </Dropdown>
                    <Button
                        onClick={this.saveTab.bind(this, true)}
                        title="保存任务"
                        disabled={!isSaveAvaliable}
                    >
                        <MyIcon className="my-icon" type="save" />保存
                    </Button>
                    <Dropdown overlay={this.importMenu()} trigger={['click']}>
                        <Button>
                            <MyIcon className="my-icon" type="import" />
                            导入<Icon type="down" />
                        </Button>
                    </Dropdown>
                    <FullScreenButton target="JS_APP"/>
                </Col>

                {showPublish ? (<Col className="right">
                    <Button
                        disabled={disablePublish}
                        onClick={this.showPublish.bind(this)}
                        title="发布任务"
                    >
                        <MyIcon className="my-icon" type="fly" />发布
                    </Button>
                    <Link to={`/operation/offline-management?tname=${currentTabData && currentTabData.name}`}>
                        <Button disabled={!isTask}>
                            <MyIcon className="my-icon" type="goin" /> 运维
                        </Button>
                    </Link>
                </Col>) : null}
            </header>
            <Row className="task-browser">
                <div className="browser-content">
                    <Tabs
                        hideAdd
                        onTabClick={this.switchTab.bind(this, currentTab)}
                        activeKey={`${currentTab}`}
                        type="editable-card"
                        className="browser-tabs"
                        onEdit={(tabId) => closeTab(tabId, tabs)}
                        tabBarExtraContent={<Dropdown overlay={
                            <Menu style={{ marginRight: 2 }}
                                onClick={this.closeAllorOthers.bind(this)}
                            >
                                <Menu.Item key="OHTERS">关闭其他</Menu.Item>
                                <Menu.Item key="ALL">关闭所有</Menu.Item>
                            </Menu>
                        }>
                            <Icon type="bars" style={{ margin: '10 0 0 0' }} />
                        </Dropdown>}
                    >
                        {this.renderTabs(tabs)}
                    </Tabs>
                    <MainBench
                        tabData={currentTabData}
                        taskCustomParams={taskCustomParams}
                        updateTaskFields={this.props.updateTaskField}
                        updateCatalogue={this.props.updateCatalogue}
                    />
                    <SiderBench tabData={currentTabData} key={currentTabData && currentTabData.id} />
                </div>
            </Row>
            <ImportData visible={this.state.visible} />
            {this.renderPublish()}
        </Row>
    }

    publishChange = (e) => {
        this.setState({
            publishDesc: e.target.value
        })
    }

    closePublish = () => {
        this.setState({
            publishDesc: '',
            showPublish: false,
        })
    }

    /**
     * @description 关闭所有/其他tab
     * @param {any} item
     * @memberof Workbench
     */
    closeAllorOthers(item) {
        const { key } = item;
        const { tabs, currentTab, closeAllorOthers } = this.props;
        closeAllorOthers(key, tabs, currentTab);
        // if (key === 'ALL') {
        //     let allClean = true;

        //     for (let tab of tabs) {
        //         if (tab.notSynced) allClean = false;
        //         break;
        //     }

        //     if (allClean) {
        //         this.props.closeAll(tabs);
        //     }
        //     else {
        //         confirm({
        //             title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
        //             content: '强制关闭将丢弃所有修改数据',
        //             onOk() {
        //                 closeAll(tabs);
        //             },
        //             onCancel() { }
        //         });
        //     }
        // }
        // else {
        //     let allClean = true;

        //     for (let tab of tabs) {
        //         if (tab.notSynced && tab.id !== currentTab) allClean = false;
        //         break;
        //     }

        //     if (allClean) {
        //         closeOthers(currentTab, tabs);
        //     }
        //     else {
        //         confirm({
        //             title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
        //             content: '强制关闭将丢弃这些修改数据',
        //             onOk() {
        //                 closeOthers(currentTab, tabs);
        //             },
        //             onCancel() { }
        //         });
        //     }
        // }
    }

    renderTabs(tabs) {
        if (tabs && tabs.length > 0) {
            return tabs.map((tab) => {
                const title = (<span>
                    <SyncBadge notSynced={tab.notSynced} />
                    {tab.name}
                </span>);

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

    saveTab(isSave) {
        this.setState({ theReqIsEnd: false, })
        const { saveTab, dataSync, currentTabData } = this.props;
        let result = this.generateRqtBody(dataSync);
        let type = 'task'
        // 非任务类型，脚本类型
        if (!utils.checkExist(currentTabData.taskType)) {
            result = currentTabData;
            type = 'script';
        }
        // 修改task配置时接口要求的标记位
        result.preSave = true;
        result.submitStatus = 0;
        saveTab(result, isSave, type); 
        setTimeout(() => {
            this.setState({
                theReqIsEnd: true,
            })
        }, 500);
    }

    submitTab() {
        const {
            publishTask, dataSync,
            currentTab, reloadTabTask,
        } = this.props;


        const { publishDesc } = this.state
        const result = this.generateRqtBody(dataSync)

        // 添加发布描述信息
        if (publishDesc) { 
            if (publishDesc.length > 200) {
                message.error('备注信息不可超过200个字符！')
                return false;
            }
            // 修改task配置时接口要求的标记位
            result.preSave = true;
            result.submitStatus = 1; // 1-提交，0-保存
            result.publishDesc = publishDesc;//发布信息
            ajax.publishOfflineTask(result).then(res =>{
                if (res.code === 1) {
                    message.success('发布成功！');
                    publishTask(res);
                    reloadTabTask(currentTab);
                    this.closePublish();
                }else{
                    message.error('发布失败！');
                    this.closePublish();
                }
            });
        } else {
            message.error('发布备注不可为空！')
            return false;
        }

    }

    switchTab(currentTab, tabId) {
        const { openTab } = this.props;
        +tabId !== currentTab && openTab({id: + tabId } );
    }

    closeTab(tabId) {
        const { closeTab, tabs } = this.props;

        let dirty = tabs.filter(tab => {
            return tab.id == tabId
        })[0].notSynced;

        if (!dirty) {
            closeTab(+tabId);
        }
        else {
            confirm({
                title: '修改尚未同步到服务器，是否强制关闭 ?',
                content: '强制关闭将丢弃当前修改数据',
                onOk() {
                    closeTab(+tabId);
                },
                onCancel() { }
            });
        }
    }

    /**
     * @description 拼装接口所需数据格式
     * @param {any} data 数据同步job配置对象
     * @returns {any} result 接口所需数据结构
     * @memberof DataSync
     */
    generateRqtBody(data) {
        // 深刻龙避免直接mutate store
        let clone = cloneDeep(data);

        const { tabs, currentTab, currentTabData } = this.props;
        const { keymap, sourceMap, targetMap } = clone;
        const { source, target } = keymap;
        const { name, id } = this.props;

        // 接口要求keymap中的连线映射数组放到sourceMap中
        clone.sourceMap.column = source;
        clone.targetMap.column = target;

        clone.settingMap = clone.setting;
        clone.name = currentTabData.name;
        clone.taskId = currentTabData.id;

        // type中的特定配置项也放到sourceMap中
        const targetTypeObj = targetMap.type;
        const sourceTypeObj = sourceMap.type;

        for (let key in sourceTypeObj) {
            if (sourceTypeObj.hasOwnProperty(key)) {
                sourceMap[key] = sourceTypeObj[key]
            }
        }
        for (let k2 in targetTypeObj) {
            if (targetTypeObj.hasOwnProperty(k2)) {
                targetMap[k2] = targetTypeObj[k2]
            }
        }

        // 删除接口不必要的字段
        delete clone.keymap;
        delete clone.setting;
        delete clone.dataSourceList;

        // 获取当前task对象并深克隆
        let result = cloneDeep(tabs.filter(tab => tab.id === currentTab)[0]);

        // 将以上步骤生成的数据同步配置拼装到task对象中
        for (let key in clone) {
            if (clone.hasOwnProperty(key)) {
                result[key] = clone[key];
            }
        }
        // 修改task配置时接口要求的标记位
        result.preSave = true;

        // 接口要求上游任务字段名修改为dependencyTasks
        if (result.taskVOS) {
            result.dependencyTasks = result.taskVOS.map(o => o);
            result.taskVOS = null;
        }

        // 删除不必要的字段
        delete result.taskVersions;

        // 数据拼装结果
        return result;
    }
}

const mapState = state => {
    const { workbench, dataSync, scriptTree } = state.offlineTask;
    const { currentTab, tabs, taskCustomParams } = workbench;

    const currentTabData = tabs.filter(tab => {
        return tab.id === currentTab;
    })[0];

    return {
        currentTab,
        currentTabData,
        tabs,
        dataSync,
        taskCustomParams,
        user: state.user,
        scriptTreeData: scriptTree
    };
};

const mapDispatch = dispatch => {

    const actions = workbenchActions(dispatch)

    return {
        openTab: id => {
            dispatch({
                type: workbenchAction.OPEN_TASK_TAB,
                payload: id
            })
            dispatch({
                type: editorAction.SET_SELECTION_CONTENT,
                data: '',
            })
        },

        updateTaskFields: fields => dispatch({
            type: workbenchAction.SET_TASK_FIELDS_VALUE,
            payload: fields
        }),

        updateCatalogue: catalogue => {
            dispatch({
                type: taskTreeAction.EDIT_FOLDER_CHILD_FIELDS,
                payload: catalogue
            });
        },

        closeTab: id => {
            dispatch(stopSql(id, null, true))
            dispatch({
                type: workbenchAction.CLOSE_TASK_TAB,
                payload: id
            })
        },
        closeAll: (tabs) => {
            for (let i in tabs) {
                dispatch(stopSql(tabs[i].id, null, true))
            }
            dispatch({
                type: workbenchAction.CLOSE_ALL_TABS
            })
        },
        closeOthers: (id, tabs) => {
            for (let i in tabs) {
                if (tabs[i].id == id) {
                    continue;
                }
                dispatch(stopSql(tabs[i].id, null, true))
            }
            dispatch({
                type: workbenchAction.CLOSE_OTHER_TABS,
                payload: id
            })
        },

        saveTab(params, isSave, type) {

            const updateTaskInfo = function (data) {

                dispatch({
                    type: workbenchAction.SET_TASK_FIELDS_VALUE,
                    payload: data
                });
                dispatch({
                    type: workbenchAction.MAKE_TAB_CLEAN
                })
            }

            const succCallback = (res) => {
                if (res.code === 1) {
                    const fileData = res.data;
                    const lockInfo = fileData.readWriteLockVO;
                    const lockStatus = lockInfo.result; // 1-正常，2-被锁定，3-需同步
                    if (lockStatus === 0) {
                        message.success(isSave ? '保存成功！' : '发布成功！');
                        updateTaskInfo({
                            version: fileData.version,
                            readWriteLockVO: fileData.readWriteLockVO,
                        })
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
                                    if (res.code === 1) {
                                        message.success('保存成功！')
                                        updateTaskInfo({
                                            version: res.data.version,
                                            readWriteLockVO: res.data.readWriteLockVO,
                                        })
                                    }
                                }
                                if (type === 'task') {
                                    ajax.forceUpdateOfflineTask(params).then(succCall)
                                } else if (type === 'script') {
                                    ajax.forceUpdateOfflineScript(params).then(succCall)
                                }
                            },
                        });
                        // 如果同步状态，则提示会覆盖代码，
                        // 点击确认，重新拉取代码并覆盖当前代码，取消则退出
                    } else if (lockStatus === 2) { // 2-需同步
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
                                    id: params.id,
                                    lockVersion: lockInfo.version,
                                }
                                if (type === 'task') {
                                    // 更新version, getLock信息
                                    ajax.getOfflineTaskDetail(reqParams).then(res => {
                                        if (res.code === 1) {
                                            const taskInfo = res.data
                                            taskInfo.merged = true;
                                            updateTaskInfo(taskInfo)
                                        }
                                    })
                                } else if (type === 'script') {
                                    ajax.getScriptById(reqParams).then(res => {
                                        if (res.code === 1) {
                                            const scriptInfo = res.data
                                            scriptInfo.merged = true;
                                            updateTaskInfo(scriptInfo)
                                        }
                                    })
                                }
                            },
                        });
                    }
                }
            }

            params.lockVersion = params.readWriteLockVO.version;
            if (type === 'task') {
                ajax.saveOfflineJobData(params).then(succCallback);
            }
            else if (type === 'script') {
                ajax.saveScript(params).then(succCallback);
            }
        },

        reloadTabTask: (id) => {
            actions.reloadTabTask(id)
        },

        publishTask(res) {
            console.log('publishTask', res);
            dispatch({
                type: workbenchAction.CHANGE_TASK_SUBMITSTATUS,
                payload: (res.data && res.data.submitStatus) || 1
            });
            dispatch({
                type: workbenchAction.MAKE_TAB_CLEAN
            })
        },

        toggleCreateTask: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_TASK
            });
        },

        toggleCreateFolder: function (cateType) {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FOLDER,
                payload: cateType
            });
        },

        toggleCreateScript: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_SCRIPT,
            });
        },
    }
};

export default connect(mapState, workbenchActions)(Workbench);