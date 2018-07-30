import React, { Component } from 'react'
import { Row, Tabs, Modal, Alert, message, Dropdown, Menu, Icon } from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { propEditorOptions, LOCK_TYPE } from '../../../comm/const'
import SyncBadge from '../../../components/sync-badge';
import Editor from '../../../components/code-editor'
import * as BrowserAction from '../../../store/modules/realtimeTask/browser'
import { updateRealtimeTreeNode } from '../../../store/modules/realtimeTask/tree'

import RealTimeEditor from './editor'
import TaskDetail from './taskDetail'
import InputPanel from './inputPanel'
import OutputPanel from './outputPanel'


const TabPane = Tabs.TabPane
const confirm = Modal.confirm;

export default class TaskBrowser extends Component {

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
        }
    }
    
    componentWillReceiveProps(nextProps) {
        if(nextProps.currentPage!=this.props.currentPage){
            this._syncEditor=true;
        }
    }

    onEdit = (targetKey, action) => {
        const { pages, currentPage, dispatch } = this.props
        switch (action) {
            case 'remove': {
                if (currentPage.notSynced) {
                    confirm({
                        title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                        content: '强制关闭将丢弃这些修改数据',
                        onOk() {
                            dispatch(BrowserAction.closePage(parseInt(targetKey, 10), pages, currentPage))
                        },
                        onCancel() { }
                    });
                } else {
                    dispatch(BrowserAction.closePage(parseInt(targetKey, 10), pages, currentPage))
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
                    <SyncBadge notSynced={pane.notSynced} />
                    {pane.name}
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
            this.SideBench.style.width = '30px'
        } else if (activeKey !== selected) {
            this.SideBench.style.width = '500px'
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
        let top = '10px';
        return isLocked ? (
            <div className="lock-layer">
                <Alert
                    style={{ position: 'absolute', top: top, left: '35%', zIndex: '999' }}
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
            }
            else {
                confirm({
                    title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                    content: '强制关闭将丢弃所有修改数据',
                    onOk() {
                        dispatch(BrowserAction.clearPages());
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
            }
            else {
                confirm({
                    title: '部分任务修改尚未同步到服务器，是否强制关闭 ?',
                    content: '强制关闭将丢弃这些修改数据',
                    onOk() {
                        dispatch(BrowserAction.closeOtherPages(currentPage));
                    },
                    onCancel() { }
                });
            }
        }
    }

    editorParamsChange(){
        this._syncEditor=false;
        this.props.editorParamsChange(...arguments);
    }

    render() {
        const {
            currentPage, pages, router,
            editorFocus, editorFocusOut,
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
                                onClick={this.closeAllorOthers.bind(this)}
                            >
                                <Menu.Item key="OHTERS">关闭其他</Menu.Item>
                                <Menu.Item key="ALL">关闭所有</Menu.Item>
                            </Menu>
                        }>
                            <Icon type="bars" style={{ margin: '10 0 0 0' }} />
                        </Dropdown>}
                    >
                        {panels}
                    </Tabs>
                    {this.renderLock(currentPage)}
                    <RealTimeEditor {...this.props} />
                    <div className="m-siderbench bd-left" ref={(e) => { this.SideBench = e }}>
                        <Tabs
                            activeKey={this.state.selected}
                            type="card"
                            className="task-params"
                            tabPosition="right"
                            onTabClick={this.tabClick}
                        >
                            <TabPane tab={<span className="title-vertical tabpanel-content">输入</span>} key="params3">
                                <InputPanel {...this.props} />
                            </TabPane>
                            <TabPane tab={<span className="title-vertical tabpanel-content">输出</span>} key="params4">
                                <OutputPanel {...this.props} />
                            </TabPane>
                            <TabPane tab={<span className="title-vertical">任务详情</span>} key="params1">
                                <TaskDetail {...this.props} />
                            </TabPane>
                            <TabPane tab={<span className="title-vertical">环境参数</span>} key="params2">
                                <Editor
                                    key="params-editor"
                                    options={propEditorOptions}
                                    onFocus={editorFocus}
                                    focusOut={editorFocusOut}
                                    sync={this._syncEditor}
                                    value={currentPage.taskParams}
                                    onChange={this.editorParamsChange.bind(this)}
                                />
                            </TabPane>
                        </Tabs>
                    </div>
                </div>
            </Row>
        )
    }
}
