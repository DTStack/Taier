import React from 'react';
import { connect } from 'react-redux';
import {
    Row, Col, Button, message, Input, Form,
    Tabs, Menu, Dropdown, Icon, Modal, Tooltip
} from 'antd';
import { hashHistory } from "react-router";

import { cloneDeep, isEmpty } from 'lodash';

import utils from 'utils'
import FullScreenButton from 'widgets/fullscreen';

import ajax from '../../../api';

import { formItemLayout, TASK_TYPE, DATA_SYNC_TYPE, PROJECT_TYPE } from '../../../comm/const';
import MyIcon from '../../../components/icon';
import SyncBadge from '../../../components/sync-badge';
import TabIcon from '../../../components/tab-icon';

import MainBench from './mainBench';
import SiderBench from './siderBench';
import ImportData from './dataImport';

import { showSeach } from '../../../store/modules/comm';
import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction';
import { isProjectCouldEdit } from '../../../comm';


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

    toPublishView() {
        hashHistory.push({
            pathname: "/package/create",
            query: {
                type: "offline"
            }
        })
    }

    showPublish() {
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
            this.setState({ showPublish: true })
        }
    }

    checkSyncScript(currentTabData) {
        const sql = currentTabData.sqlText;

        if (utils.jsonFormat(sql)) {
            return true;
        }
        message.error("请确认JSON格式是否正确");
        return false;
    }

    searchTask = () => {
        this.props.dispatch(showSeach(true));
    }

    renderPublish = () => {
        const { user } = this.props;
        const { publishDesc } = this.state;
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
                            <span>备注</span>
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
                <Row>
                    <Col offset={6} span={15}>
                        注意：提交过的任务才能被调度执行及发布到其他项
                    </Col>
                </Row>
            </Modal>
        )
    }

    render() {
        const {
            tabs, currentTab, currentTabData,
            dataSync, taskCustomParams,
            closeTab, closeAllorOthers, project,
            user, editor
        } = this.props;

        const { sourceMap, targetMap } = dataSync;
        const { theReqIsEnd } = this.state;
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const isTest= project.projectType==PROJECT_TYPE.TEST;
        const couldEdit = isProjectCouldEdit(project, user);
        let isSaveAvaliable = false;

        if (!isEmpty(sourceMap) && !isEmpty(targetMap)) isSaveAvaliable = true;
        //不属于数据同步或者属于数据同步的脚本模式都可以保存
        if (
            currentTabData &&
            (currentTabData.taskType !== TASK_TYPE.SYNC ||
                (currentTabData.createModel == DATA_SYNC_TYPE.SCRIPT && currentTabData.taskType == TASK_TYPE.SYNC)
            )
        ) {
            isSaveAvaliable = true;
        }

        isSaveAvaliable = (currentTabData && !currentTabData.invalid) || !theReqIsEnd || (currentTabData && !currentTabData.notSynced);

        //被锁就不能保存了
        if (currentTabData && currentTabData.readWriteLockVO && !currentTabData.readWriteLockVO.getLock) {
            isSaveAvaliable = false;
        }

        const isTask = currentTabData && utils.checkExist(currentTabData.taskType)
        const isWorkflowNode = currentTabData && currentTabData.flowId && currentTabData.flowId !== 0;

        const disablePublish = !isTask || currentTabData.notSynced || isWorkflowNode;
        const showPublish = isTask;

        const themeDark = editor.options.theme !== 'vs' ? true : undefined;

        return <Row className="m-workbench task-editor">
            <header className="toolbar clear">
                <Col className="left">

                    {couldEdit && (
                        <span>
                            <Dropdown overlay={this.createMenu()} trigger={['click']}>
                                <Button title="创建">
                                    <MyIcon className="my-icon" type="focus" themeDark={themeDark}/>
                                    新建<Icon type="down" />
                                </Button>
                            </Dropdown>
                            <Button
                                onClick={this.saveTab.bind(this, true)}
                                title="保存任务"
                                disabled={!isSaveAvaliable}
                            >
                                <MyIcon className="my-icon" type="save" themeDark={themeDark}/>保存
                            </Button>
                        </span>
                    )}
                    <Dropdown overlay={this.importMenu()} trigger={['click']}>
                        <Button>
                            <MyIcon className="my-icon" type="import" themeDark={themeDark}/>
                            导入<Icon type="down" />
                        </Button>
                    </Dropdown>
                    <Button
                        onClick={this.searchTask}
                        title="打开任务"
                    >
                        <MyIcon className="my-icon" type="search" themeDark={themeDark}/>
                        搜索
                    </Button>
                    <FullScreenButton />
                </Col>

                {showPublish ? (<Col className="right">

                    {couldEdit && (<span>
                        <Tooltip
                            placement="bottom"
                            title="提交到调度系统"
                            mouseLeaveDelay={0}
                        >
                            <Button
                                disabled={disablePublish}
                                onClick={this.showPublish.bind(this)}
                            >
                                <Icon type="upload" style={{ color: "#000" }} themeDark={themeDark}/>提交
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
                                <MyIcon className="my-icon" type="fly" themeDark={themeDark}/>发布
                        </Button>
                        </Tooltip>}
                    </span>)}

                    <a href={`${location.pathname}#/operation/offline-management?tname=${currentTabData && currentTabData.name}`}>
                        <Button disabled={!isTask}>
                            <MyIcon className="my-icon" type="goin" themeDark={themeDark}/> 运维
                        </Button>
                    </a>
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
                                onClick={({ key }) => closeAllorOthers(key, tabs, currentTab)}
                            >
                                <Menu.Item key="OHTERS">关闭其他</Menu.Item>
                                <Menu.Item key="ALL">关闭所有</Menu.Item>
                            </Menu>
                        }>
                            <Icon type="bars" size="" style={{ margin: '7 0 0 0', fontSize: 18, }} />
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

    renderTabs(tabs) {
        if (tabs && tabs.length > 0) {
            return tabs.map((tab) => {
                let title = (<div>
                    <TabIcon tabData={tab} />
                    <span className="tab-ellipsis">{tab.name}</span>
                    <SyncBadge notSynced={tab.notSynced} />
                </div>);

                if (tab.flowId) {
                    title = (<div>
                        <TabIcon tabData={tab} />
                        <a className="tab-ellipsis" onClick={() => this.switchTab(this.props.currentTab, tab.flowId)}>
                            {tab.flowName}
                        </a><span className="tab-ellipsis">&nbsp;/ {tab.name}</span>
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

    saveTab(isSave) {
        this.setState({ theReqIsEnd: false, })
        const { saveTab, dataSync, currentTabData } = this.props;

        // 如果是工作流任务，需要对保存操作提前做校验
        if (
            currentTabData.taskType === TASK_TYPE.WORKFLOW
            && currentTabData.toUpdateTasks &&
            currentTabData.toUpdateTasks.length > 0
        ) {
            message.warning('您有工作流节点任务未保存！')
            return false;
        }

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
            currentTab, reloadTaskTab, project
        } = this.props;

        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const { publishDesc = '' } = this.state
        const result = this.generateRqtBody(dataSync)

        // 添加发布描述信息

        if (publishDesc.length > 200) {
            message.error('备注信息不可超过200个字符！')
            return false;
        }
        // 修改task配置时接口要求的标记位
        result.preSave = true;
        result.submitStatus = 1; // 1-提交，0-保存
        result.publishDesc = publishDesc;//发布信息
        ajax.publishOfflineTask(result).then(res => {
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

    switchTab(currentTab, tabId) {
        const { openTab, tabs } = this.props;
        +tabId !== currentTab && openTab({ id: + tabId, tabs, });
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
        scriptTreeData: scriptTree,
        project: state.project,
        editor: state.editor,
    };
};

export default connect(mapState, workbenchActions)(Workbench);