import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Row, Tabs, Modal, Alert, message, Dropdown, Menu, Icon } from 'antd'

import utils from 'utils'
import { debounce, cloneDeep } from 'lodash';

import Api from '../../../api'
import { LOCK_TYPE } from '../../../comm/const'
import SyncBadge from '../../../components/sync-badge'
import Editor from 'widgets/editor'
import TabIcon from '../../../components/tab-icon'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import { updateRealtimeTreeNode } from '../../../store/modules/realtimeTask/tree'

import RealTimeEditor from './editor'
import TaskDetail from './taskDetail'
import InputPanel from './inputPanel'
import OutputPanel from './outputPanel'
import DimensionPanel from './dimensionPanel'

const TabPane = Tabs.TabPane
const confirm = Modal.confirm;

class TaskBrowser extends Component {

    state = {
        selected: '',
        expanded: false,
    }

    onChange = (activeKey) => {
        const { dispatch, pages } = this.props
        const id = parseInt(activeKey, 10)
        const page = pages && pages.find((item) => { return item.id === id })
        if (page) {
            dispatch(BrowserAction.setCurrentPage(page))
            dispatch(BrowserAction.getInputData())
            dispatch(BrowserAction.getOutputData())
            dispatch(BrowserAction.getDimensionData())
        }
    }

    componentWillReceiveProps(nextProps) {
        if (nextProps.currentPage != this.props.currentPage) {
            this._syncEditor = true;
        }
    }

    onEdit = (targetKey, action) => {
        const { pages, dispatch } = this.props
        const targetPage = pages.filter(v => v.id == targetKey)[0] || {};
        switch (action) {
            case 'remove': {
                if (targetPage.notSynced) {
                    confirm({
                        title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                        content: '强制关闭将丢弃这些修改数据',
                        onOk() {
                            dispatch(BrowserAction.closePage(parseInt(targetKey, 10), pages, targetPage))
                            dispatch(BrowserAction.closeCurrentInputData(targetPage.id));
                            dispatch(BrowserAction.closeCurrentOutputData(targetPage.id));
                            dispatch(BrowserAction.closeCurrentDimensionData(targetPage.id));
                        },
                        onCancel() { }
                    });
                } else {
                    dispatch(BrowserAction.closePage(parseInt(targetKey, 10), pages, targetPage))
                    dispatch(BrowserAction.closeCurrentInputData(targetPage.id));
                    dispatch(BrowserAction.closeCurrentOutputData(targetPage.id));
                    dispatch(BrowserAction.closeCurrentDimensionData(targetPage.id));
                }
                break;
            }
            default:
                break
        }
    }

    mapPanels = (panes) => {
        if (panes && panes.length > 0) {
            return panes.map((pane) => {
                const title = (<span>
                    <TabIcon tabData={pane} />
                    <SyncBadge notSynced={pane.notSynced} />
                    <span className="tab-ellipsis">{pane.name}</span>
                </span>)
                return (
                    <TabPane
                        style={{ height: '0px' }}
                        tab={title}
                        key={pane.id}
                    />
                )
            })
        }
        return []
    }

    tabClick = (activeKey) => {
        const { selected, expanded } = this.state
        if (activeKey === selected && expanded) {
            this.setState({ selected: '', expanded: false });
            this.SideBench.style.width = '30px';
        } else if (activeKey !== selected) {
            this.SideBench.style.width = '500px';
            this.setState({ selected: activeKey, expanded: true });
        }
    }

    unLock = () => {
        const { currentPage, dispatch } = this.props;
        const lockInfo = currentPage.readWriteLockVO;
        // 解锁
        confirm({
            title: '解锁提醒',
            content: `文件正在被${lockInfo.lastKeepLockUserName}编辑中，开始编辑时间
                ${utils.formatDateTime(lockInfo.gmtModified)}。
                强制编辑可能导致${lockInfo.lastKeepLockUserName}对该文件的修改无法保存！
            `,
            okText: '确定',
            okType: 'danger', // warning
            cancelText: '取消',
            onOk() {
                const params = {
                    fileId: currentPage.id,
                    type: LOCK_TYPE.STREAM_TASK,
                    lockVersion: lockInfo.version,
                };
                Api.unlockFile(params).then(res => {
                    if (res.code === 1) {
                        const lockData = res.data;
                        if (lockData.getLock) { // 解锁成功，更改所状态
                            message.success('文件解锁成功！')
                        } else {
                            Modal.error({
                                title: '解锁失败',
                                content: `文件正在被${lockData.lastKeepLockUserName}编辑中!开始编辑时间
                                ${utils.formatDateTime(lockData.gmtModified)}.`,
                            });
                        }
                        // reload task info
                        const reqParams = {
                            id: currentPage.id,
                        }
                        Api.getTask(reqParams).then(res => {
                            if (res.code === 1) {
                                const taskInfo = res.data
                                taskInfo.merged = true;
                                const updated = {
                                    id: currentPage.id,
                                    readWriteLockVO: taskInfo.readWriteLockVO
                                }
                                dispatch(BrowserAction.setCurrentPage(taskInfo))
                                dispatch(updateRealtimeTreeNode(updated))
                            }
                        })
                    }
                })
            },
            onCancel() { console.log('Cancel'); },
        });
    }

    renderLock(tabData) {
        const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock
        return isLocked ? (
            <div className="lock-layer">
                <Alert
                    style={{ position: 'absolute', top: '-2px', left: '35%', zIndex: '999', height: '35px' }}
                    showIcon
                    message={<span>当前文件为只读状态！{<a onClick={this.unLock}>解锁</a>}</span>}
                    type="warning"
                />
            </div>
        ) : null
    }

    handleCustomParamsChange = (params) => {
        const { currentPage, dispatch } = this.props;
        const updatePage = Object.assign(currentPage, params);
        dispatch(BrowserAction.setCurrentPage(updatePage))
    }

    /**
    * @description 关闭所有/其他tab
    * @param {any} item
    * @memberof Workbench
    */
    closeAllorOthers(item) {
        const { key } = item;
        const { pages, currentPage, dispatch } = this.props;

        if (key === 'ALL') {
            let allClean = true;

            for (let tab of pages) {
                if (tab.notSynced) {
                    allClean = false;
                    break;
                }
            }

            if (allClean) {
                dispatch(BrowserAction.clearPages());
                dispatch(BrowserAction.closeAllInputData());
                dispatch(BrowserAction.closeAllOutputData());
                dispatch(BrowserAction.closeAllDimensionData());
            }
            else {
                confirm({
                    title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                    content: '强制关闭将丢弃所有修改数据',
                    onOk() {
                        dispatch(BrowserAction.clearPages());
                        dispatch(BrowserAction.closeAllInputData());
                        dispatch(BrowserAction.closeAllOutputData());
                        dispatch(BrowserAction.closeAllDimensionData());
                    },
                    onCancel() { }
                });
            }
        }
        else {
            let allClean = true;

            for (let tab of pages) {
                if (tab.notSynced && tab.id !== currentPage.id) {
                    allClean = false;
                    break;
                }
            }

            if (allClean) {
                dispatch(BrowserAction.closeOtherPages(currentPage));
                dispatch(BrowserAction.closeOtherInputData(currentPage.id));
                dispatch(BrowserAction.closeOtherOutputData(currentPage.id));
                dispatch(BrowserAction.closeOtherDimensionData(currentPage.id));
            }
            else {
                confirm({
                    title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                    content: '强制关闭将丢弃这些修改数据',
                    onOk() {
                        dispatch(BrowserAction.closeOtherPages(currentPage));
                        dispatch(BrowserAction.closeOtherInputData(currentPage.id));
                        dispatch(BrowserAction.closeOtherOutputData(currentPage.id));
                        dispatch(BrowserAction.closeOtherDimensionData(currentPage.id));
                    },
                    onCancel() { }
                });
            }
        }
    }

    editorParamsChange(value) {//切换tab会出发change,初始值未改变,导致所有tab为红色,增加this._syncEditor判断
        if (!this._syncEditor) {
            this.debounceChange(value);
        } else {
            this._syncEditor = false;
            this.editorParamsChange(value);
        }
    }

    editorChange = (newVal) => {
        let { currentPage, dispatch } = this.props;
        currentPage = cloneDeep(currentPage);
        currentPage.taskParams = newVal
        currentPage.notSynced = true; // 添加未保存标记
        dispatch(BrowserAction.setCurrentPage(currentPage));
    }

    debounceChange = debounce(this.editorChange, 300, { 'maxWait': 2000 })
    editorParamsChange = (newVal) => {
        const { currentPage, dispatch } = this.props
        currentPage.taskParams = newVal
        dispatch(BrowserAction.setCurrentPage(currentPage))
    }

    tableParamsChange = () => {
        this.props.editorChange();
    }

    render() {
        const {
            currentPage, pages, router,
        } = this.props
        if (pages.length === 0) router.push('/realtime')
        const panels = this.mapPanels(pages)
        return (
            <Row className="task-browser">
                <div className="browser-content">
                    <Tabs
                        hideAdd
                        onTabClick={this.onChange}
                        activeKey={`${currentPage.id}`}
                        type="editable-card"
                        className="browser-tabs"
                        onEdit={this.onEdit}
                        tabBarExtraContent={<Dropdown overlay={
                            <Menu style={{ marginRight: 2 }}
                            >
                                <Menu.Item key="OHTERS">
                                    <a onClick={this.closeAllorOthers.bind(this, "OHTERS")} >关闭其他</a>
                                </Menu.Item>
                                <Menu.Item key="ALL">
                                    <a onClick={this.closeAllorOthers.bind(this, "ALL")} >关闭所有</a>
                                </Menu.Item>
                                <Menu.Divider />
                                {pages.map((tab) => {
                                    return <Menu.Item key={tab.id} >
                                        <a
                                            onClick={()=>{
                                                if(currentPage.id==tab.id){
                                                    return;
                                                }
                                                this.onChange(tab.id)
                                            }}
                                            style={tab.id == currentPage.id ? { color: "#2491F7" } : {}}
                                        >
                                            {tab.name}
                                        </a>
                                    </Menu.Item>
                                })}
                            </Menu>
                        }>
                            <Icon type="bars" style={{ margin: '7 0 0 0', fontSize: 18, }} />
                        </Dropdown>}
                    >
                        {panels}
                    </Tabs>
                    {this.renderLock(currentPage)}
                    <RealTimeEditor {...this.props} />
                    <div className="m-siderbench" ref={(e) => { this.SideBench = e }}>
                        <Tabs
                            activeKey={this.state.selected}
                            type="card"
                            className="task-params"
                            tabPosition="right"
                            onTabClick={this.tabClick}
                        >
                            <TabPane tab={<span className="title-vertical">任务详情</span>} key="params1">
                                <TaskDetail {...this.props} />
                            </TabPane>
                            {
                                currentPage.taskType === 0 ? <TabPane tab={<span className="title-vertical tabpanel-content" style={{ marginTop: 10, paddingBottom: 10 }}>源表</span>} key="params3">
                                    <InputPanel {...this.props} tableParamsChange={this.tableParamsChange} />
                                </TabPane> : ""
                            }
                            {
                                currentPage.taskType === 0 ? <TabPane tab={<span className="title-vertical tabpanel-content" style={{ marginTop: 5, paddingBottom: 3 }}>结果表</span>} key="params4">
                                    <OutputPanel {...this.props} tableParamsChange={this.tableParamsChange} />
                                </TabPane> : ""
                            }
                            {
                                currentPage.taskType === 0 ? <TabPane tab={<span className="title-vertical tabpanel-content" style={{ marginTop: 10, paddingBottom: 10 }}>维表</span>} key="params5">
                                    <DimensionPanel {...this.props} tableParamsChange={this.tableParamsChange} />
                                </TabPane> : ""
                            }
                            <TabPane tab={<span className="title-vertical">环境参数</span>} key="params2">
                                <Editor
                                    key="params-editor"
                                    sync={this._syncEditor}
                                    value={currentPage.taskParams}
                                    onChange={this.editorParamsChange.bind(this)}
                                    language="ini"
                                />
                            </TabPane>
                        </Tabs>
                    </div>
                </div>
            </Row>
        )
    }
}

export default connect((state) => {
    const { resources, pages, currentPage } = state.realtimeTask;
    return {
        currentPage,
        pages,
        resources,
    }
})(TaskBrowser) 
