import React, { Component } from 'react'
import { connect } from 'react-redux'
import { hashHistory } from 'react-router'
import { 
    Menu, Tree, message, Tabs,
    Popconfirm, Icon, Tooltip,
    Dropdown, 
} from 'antd'

import {
    ContextMenu,
    MenuItem,
} from 'widgets/context-menu'


import * as BrowserAction from '../../store/modules/realtimeTask/browser'
import * as ModalAction from '../../store/modules/realtimeTask/modal'
import * as TreeAction from '../../store/modules/realtimeTask/tree'
import * as ResAction from '../../store/modules/realtimeTask/res'
import { modalAction } from '../../store/modules/realtimeTask/actionTypes'

import TaskFormModal from './realtime/taskForm'
import CataFormModal from './realtime/cataForm'
import ResInfoModal from './realtime/resInfo'
import ResForm from './realtime/resForm'
import RenameModal from './realtime/renameModal'
import FnModal from './realtime/function/fnModal'
import FolderTree from './realtime/folderTree'
import FnMoveModal from './realtime/function/fnMoveModal'
import FnViewModal from './realtime/function/fnViewModal'

import Api from '../../api'
import { showSeach } from '../../store/modules/comm';
import { getTreeByType } from '../../comm';
import { MENU_TYPE } from '../../comm/const';
import MyIcon from '../../components/icon'


const SubMenu = Menu.SubMenu
const TreeNode = Tree.TreeNode
const TabPane = Tabs.TabPane

function isCreate(operation) {
    return operation.indexOf('ADD') > -1
}

function isUpdate(operation) {
    return operation.indexOf('EDIT') > -1
}

const rootNode = { isGetFile: true, id: 0 }

class RealTimeTabPane extends Component {

    state = {
        activeNode: {},
        selectedRes: {},
        selectedFn: {},
        taskInfo: {},
        taskTypes: [],
        subMenus: [],
        visibleResInfo: false,
        visibleFn: false,
        visibleMoveFn: false,
        visibleFnInfo: false,
    }

    componentDidMount() {
        const { dispatch } = this.props
        dispatch(TreeAction.getRealtimeTree(rootNode))
        dispatch(ResAction.getResources())
        this.loadTaskTypes();
    }

    componentWillReceiveProps(nextProps) {
        const old = this.props.project
        const newData = nextProps.project
        if (newData && old.id !== 0 && old.id !== newData.id) {
            const { dispatch } = this.props
            dispatch(BrowserAction.clearPages()) // 切换项目后，清理pages页面
            dispatch(TreeAction.getRealtimeTree(rootNode))
            dispatch(ResAction.getResources())
        }
    }

    loadTaskTypes = () => {
        Api.getRealtimeTaskTypes().then(res => {
            if (res.code === 1) {
                this.setState({
                    taskTypes: res.data || [],
                })
            }
        })
    }

    chooseTask = (selectedKeys, info) => {
        const { dispatch } = this.props
        const item = info.node.props.data
        if (item.type === 'file') {
            dispatch(BrowserAction.openPage({ id: item.id }))
        }
    }

    chooseRes = (selectedKeys, info) => {
        const ctx = this
        const item = info.node.props.data
        if (item.type === 'file') {
            Api.getRes({ resourceId: item.id }).then((res) => {
                if (res.code === 1) {
                    ctx.setState({
                        visibleResInfo: true,
                        selectedRes: res.data,
                    })
                }
            })
        }
    }

    chooseFn = (selectedKeys, target) => {
        const ctx = this
        const item = target.node.props.data
        if (item.type === 'file') {
            this.setState({
                visibleFnInfo: true,
                selectedFn: item
            })
        }
    }

    rightClick = ({ node }) => {
        const activeNode = node.props.data;
        this.setState({ activeNode: activeNode })
    }

    initEditTask = () => {
        const ctx = this
        const task = this.state.activeNode
        Api.getTask({ id: task.id }).then((res) => {
            if (res.code === 1) {
                ctx.setState({ taskInfo: res.data })
                this.doAction(modalAction.EDIT_TASK_VISIBLE)
            }
        })
    }

    initAddTask = () => {
        const { dispatch } = this.props
        const node = this.state.activeNode
        const taskInfo = { nodePid: node.id }
        this.setState({ taskInfo })
        this.doAction(modalAction.ADD_TASK_VISIBLE)
    }

    createOrUpdateTask = (task) => {
        const { dispatch, realtimeTree, currentPage, modal } = this.props
        const { activeNode, taskInfo } = this.state

        if (isCreate(modal)) {

            Api.saveTask(task).then((res) => {
                if (res.code === 1) {
                    message.success('创建任务成功')
                    this.closeModal()
                    dispatch(BrowserAction.newPage(res.data))
                    dispatch(TreeAction.getRealtimeTree({
                        id: task.nodePid,
                        catalogueType: MENU_TYPE.TASK_DEV
                    }))
                    hashHistory.push('/realtime/task')
                }
            })

        } else if (isUpdate(modal)) {

            task.id = activeNode.id
            task.version = taskInfo.version
            task.readWriteLockVO = Object.assign({}, taskInfo.readWriteLockVO); 

            Api.saveTask(task).then((res) => {
                if (res.code === 1) {
                    message.success('任务更新成功！')
                    this.closeModal()
                    const result = res.data;
                    if (task.id === currentPage.id) {
                        dispatch(BrowserAction.setCurrentPage(result))
                    } else {
                        dispatch(BrowserAction.updatePage(result))
                    }
                    dispatch(TreeAction.getRealtimeTree({
                        id: task.nodePid,
                        catalogueType: MENU_TYPE.TASK_DEV
                    }))
                }
            })
        }
    }

    deleteTask = () => {
        const { dispatch, pages, currentPage } = this.props
        const task = this.state.activeNode
        Api.deleteTask({ id: task.id }).then((res) => {
            if (res.code === 1) {
                message.success('任务移除成功！')
                dispatch(TreeAction.removeRealtimeTree(task))
                dispatch(BrowserAction.closePage(task.id, pages, currentPage))
            }
        })
    }

    uploadRes = (file) => {
        const { dispatch } = this.props
        Api.uploadRes(file).then((res) => {
            if (res.code === 1) {
                message.success('资源上传成功！')
                this.closeModal()
                dispatch(ResAction.getResources())
                dispatch(TreeAction.getRealtimeTree({
                    id: file.nodePid,
                    catalogueType: MENU_TYPE.RESOURCE 
                }))
            }
        })
    }

    deleteFolder = () => {
        const { dispatch } = this.props
        const cata = this.state.activeNode
        Api.deleteCatalogue({ id: cata.id }).then((res) => {
            if (res.code === 1) {
                message.success('文件夹移除成功！')
                dispatch(TreeAction.removeRealtimeTree(cata))
            }
        })
    }

    updateFolder = (cateInfo) => {
        const { dispatch, realtimeTree, modal } = this.props
        const { activeNode } = this.state
        const params = { id: cateInfo.nodePid,}
        switch (modal) {
            case modalAction.ADD_RES_CATA_VISIBLE:
            case modalAction.EDIT_RES_CATA_VISIBLE:
                params.catalogueType = MENU_TYPE.RESOURCE 
                break;
            case modalAction.ADD_TASK_CATA_VISIBLE:
            case modalAction.EDIT_TASK_CATA_VISIBLE:
                params.catalogueType = MENU_TYPE.TASK_DEV 
                break;
            case modalAction.ADD_FUNC_CATA_VISIBLE:
            case modalAction.EDIT_FUNC_CATA_VISIBLE: {
                params.catalogueType = MENU_TYPE.COSTOMFUC 
                break;
            }
            default: return []
        }
        if (isCreate(modal)) {
            Api.addCatalogue(cateInfo).then((res) => {
                if (res.code === 1) {
                    message.success('创建目录成功')
                    this.closeModal()
                    dispatch(TreeAction.getRealtimeTree(params))
                }
            })
        } else if (isUpdate(modal)) {
            cateInfo.id = activeNode.id
            Api.updateCatalogue(cateInfo).then((res) => {
                if (res.code === 1) {
                    message.success('更新任务目录成功')
                    dispatch(TreeAction.getRealtimeTree(params))
                    this.closeModal()
                }
            })
        } 
    }

    deleteResource = () => {
        const { dispatch } = this.props
        const resInfo = this.state.activeNode
        Api.deleteRes({ resourceId: resInfo.id }).then((res) => {
            if (res.code === 1) {
                message.success('资源移除成功！')
                dispatch(TreeAction.removeRealtimeTree(resInfo))
            }
        })
    }

    resRename = (resource) => {
        const { dispatch } = this.props
        const ctx = this
        const resInfo = this.state.activeNode
        resource.resourceId = resInfo.id
        Api.renameRes(resource).then((res) => {
            if (res.code === 1) {
                message.success('重命名成功！')
                this.closeModal()
                dispatch(TreeAction.getRealtimeTree({
                    id: resInfo.id,
                    catalogueType: MENU_TYPE.RESOURCE 
                }))
            }
        })
    }

    createFn = (fnObj) => {
        const { dispatch } = this.props
        Api.createFunc(fnObj).then(res => {
            if (res.code === 1) {
                message.success('添加函数成功！')
                this.setState({
                    visibleFn: false,
                })
                dispatch(TreeAction.getRealtimeTree({
                    id: fnObj.nodePid,
                    catalogueType: MENU_TYPE.COSTOMFUC 
                }))
            }
        })
    }

    moveFn = (moveObj) => {
        const { dispatch } = this.props
        Api.moveFunc(moveObj).then(res => {
            if (res.code === 1) {
                message.success('移动函数成功！')
                this.setState({
                    visibleMoveFn: false,
                })
                dispatch(TreeAction.removeAndUpdate({
                    id: moveObj.funcId,
                    parentId: moveObj.nodePid,
                    catalogueType: MENU_TYPE.COSTOMFUC
                }))
            }
        })
    }

    delFn = () => {
        const { dispatch } = this.props
        const { activeNode } = this.state
        Api.delFunc({ funcId: activeNode.id }).then(res => {
            if (res.code === 1) {
                message.success('删除函数成功！')
                dispatch(TreeAction.removeRealtimeTree(activeNode))
            }
        })
    }

    loadTreeData = (treeNode) => {
        const { dispatch } = this.props
        const treeType = treeNode.props.treeType
        const node = treeNode.props.data
        return new Promise((resolve) => {
            if (!node.children || node.children.length === 0) {
                dispatch(TreeAction.getRealtimeTree(node))
            }
            resolve();
        })
    }

    doAction = (action) => {
        this.props.dispatch(ModalAction.updateModal(action))
    }

    closeModal = () => {
        this.setState({ taskInfo: {} })
        this.doAction(modalAction.MODAL_HIDDEN)
    }

    onMenuClick = ({ key }) => {
        const { dispatch } = this.props;
        switch (key) {
            case 'task:newFolder': {
                this.setState({ activeNode: {} })
                this.doAction(modalAction.ADD_TASK_CATA_VISIBLE)
                return;
            }
            case 'task:newTask': {
                this.doAction(modalAction.ADD_TASK_VISIBLE)
                return;
            }
            case 'task:search': {
                dispatch(showSeach())
                return;
            }
            case 'resource:newFolder': {
                this.setState({ activeNode: {} })
                this.doAction(modalAction.ADD_RES_CATA_VISIBLE) 
                return;
            }
            case 'resource:upload': {
                this.setState({ activeNode: {} })
                this.doAction(modalAction.ADD_RES_VISIBLE)
                return;
            }
            case 'function:newFolder': {
                this.setState({ activeNode: {} })
                this.doAction(modalAction.ADD_FUNC_CATA_VISIBLE)
                return;
            }
            case 'function:newFunc': {
                this.setState({ visibleFn: true })
                return;
            }
        }
    }

    renderTabPanes = () => {
        const { realtimeTree, dispatch } = this.props;
        const menus = []
        if (realtimeTree && realtimeTree.length > 0) {
            for (let i = 0; i < realtimeTree.length; i++) {
                const menuItem = realtimeTree[i]
                let menuContent = ''
                if (!menuItem.children || menuItem.children.length === 0) continue;
                switch (menuItem.catalogueType) {
                    case MENU_TYPE.TASK: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="task:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                        <Menu.Item key="task:newTask">
                                            新建任务
                                        </Menu.Item>
                                        <Menu.Item key="task:search">
                                            搜索任务（Ctrl + P）
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <FolderTree 
                                onRightClick={this.rightClick}
                                onSelect={this.chooseTask}
                                loadData={this.loadTreeData}
                                treeData={menuItem.children}
                                treeType={menuItem.catalogueType}
                            />
                        </div>
                        break;
                    }
                    case MENU_TYPE.RESOURCE: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="resource:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                        <Menu.Item key="resource:upload">
                                            上传资源
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <FolderTree
                                onRightClick={this.rightClick}
                                onSelect={this.chooseRes}
                                loadData={this.loadTreeData}
                                treeData={menuItem.children}
                                treeType={menuItem.catalogueType}
                            />
                        </div>
                        break;
                    }
                    case MENU_TYPE.FUNCTION: {
                        const customTreeData = menuItem.children.find(item => {
                            return item.catalogueType === MENU_TYPE.COSTOMFUC
                        })
                        const systemTreeData = menuItem.children.find(item => {
                            return item.catalogueType === MENU_TYPE.SYSFUC
                        })
                        
                        menuContent = <div className="menu-content">
                            <header>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="function:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                        <Menu.Item key="function:newFunc">
                                            新建函数
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <FolderTree
                                onRightClick={this.rightClick}
                                loadData={this.loadTreeData}
                                onSelect={this.chooseFn}
                                treeData={customTreeData ? [customTreeData] : []}
                                treeType={MENU_TYPE.COSTOMFUC}
                            />
                            <FolderTree
                                onRightClick={this.rightClick}
                                loadData={this.loadTreeData}
                                onSelect={this.chooseFn}
                                treeData={systemTreeData ? [systemTreeData] : []}
                                treeType={MENU_TYPE.SYSFUC}
                            />
                        </div>
                        break;
                    }
                    default: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Tooltip title="新建文件夹">
                                    <Icon
                                        className="anticon right"
                                        onClick={() => {
                                            this.setState({ activeNode: {} })
                                            this.doAction(modalAction.ADD_TASK_CATA_VISIBLE)
                                        }}
                                        type="folder-add" />
                                </Tooltip>
                            </header>
                            <FolderTree
                                loadData={this.loadTreeData}
                                treeData={menuItem.children}
                                treeType={menuItem.catalogueType}
                            />
                        </div>;
                        break;
                    }
                }
                menus.push(
                    <TabPane tab={menuItem.name} key={menuItem.id}>
                        {menuContent}
                    </TabPane>
                )
            }
        }
        return menus;
    }

    getCataFormTreeData = () => {
        switch(this.props.modal) {
            case modalAction.ADD_RES_CATA_VISIBLE:
            case modalAction.EDIT_RES_CATA_VISIBLE:
                return this.props.resourceTree
            case modalAction.ADD_TASK_CATA_VISIBLE:
            case modalAction.EDIT_TASK_CATA_VISIBLE:
                return this.props.taskTree 
            case modalAction.ADD_FUNC_CATA_VISIBLE:
            case modalAction.EDIT_FUNC_CATA_VISIBLE: {
                const customFuncs = this.props.functionTree.find(item => item.catalogueType === MENU_TYPE.COSTOMFUC)
                return [customFuncs]
            }
            default: return []
        }
    }

    render() {
        const {
            activeNode, selectedFn, selectedRes, taskInfo, taskTypes,
            visibleResInfo, visibleMoveFn,
            visibleResRename, visibleFn, visibleFnInfo
        } = this.state

        const {
            taskTree, modal, dispatch,
            resourceTree, functionTree,
        } = this.props

        const disableEdit = activeNode && activeNode.level === 1 ? 'none' : 'block'
        const visibleTask = modal.indexOf('TASK_VISIBLE') > -1
        const visibleCata = modal.indexOf('CATA_VISIBLE') > -1

        return (
            <div className="task-sidebar m-tabs">
                <Tabs
                    tabPosition="left"
                    animated={false}
                    className="task-tab-menu"
                    style={{ height: '100%' }}
                >
                    {this.renderTabPanes()}
                </Tabs>

                <TaskFormModal
                    taskTypes={taskTypes}
                    taskRoot={taskTree}
                    operation={modal}
                    resRoot={resourceTree}
                    taskInfo={taskInfo}
                    visible={visibleTask}
                    ayncTree={this.loadTreeData}
                    handOk={this.createOrUpdateTask}
                    handCancel={this.closeModal}
                />

                <CataFormModal
                    defaultData={activeNode}
                    visible={visibleCata}
                    operation={modal}
                    loadTreeData={this.loadTreeData}
                    treeData={this.getCataFormTreeData()}
                    handOk={this.updateFolder}
                    handCancel={() => { 
                        this.setState({ activeNode: {} })
                        this.closeModal()
                    }}
                />

                <ResForm
                    {...this.props}
                    resRoot={resourceTree}
                    activeNode={activeNode}
                    visible={modal === modalAction.ADD_RES_VISIBLE}
                    handOk={this.uploadRes}
                    ayncTree={this.loadTreeData}
                    handCancel={this.closeModal}
                    title="上传资源"
                />

                <ResInfoModal
                    title="资源信息"
                    data={selectedRes}
                    handCancel={() => { this.setState({ visibleResInfo: false }) }}
                    visible={visibleResInfo}
                />

                <RenameModal
                    visible={visibleResRename}
                    data={activeNode}
                    handOk={this.resRename}
                    handCancel={() => { this.setState({ visibleResRename: false }) }}
                />

                <FnMoveModal 
                    defaultData={activeNode}
                    visible={visibleMoveFn}
                    loadTreeData={this.loadTreeData}
                    fnTreeData={ functionTree }
                    handOk={this.moveFn}
                    handCancel={() => {
                        this.setState({ visibleMoveFn: false, activeNode: {} })
                    }}
                />

                <FnModal 
                    activeNode={activeNode}
                    handOk={this.createFn}
                    visible={visibleFn}
                    fnTreeData={ functionTree }
                    resTreeData={ resourceTree }
                    loadTreeData={ this.loadTreeData }
                    handCancel={() => { this.setState({ visibleFn: false }) }}
                />

                <FnViewModal
                    defaultData={selectedFn}
                    visible={visibleFnInfo}
                    handCancel={() => { this.setState({ visibleFnInfo: false }) }}
                />

                <ContextMenu forEle=".task-folder-item">
                    <MenuItem
                      onClick={this.initAddTask}>
                        新建任务
                      </MenuItem>
                    <MenuItem onClick={() => {
                        this.doAction(modalAction.ADD_TASK_CATA_VISIBLE)
                    }}>
                        新建文件夹
                    </MenuItem>
                    <MenuItem
                      style={{ display: disableEdit }}
                      onClick={() => { 
                            this.doAction(modalAction.EDIT_TASK_CATA_VISIBLE)
                        }}>
                      编辑
                    </MenuItem>
                        <Popconfirm
                          title="确定删除这个文件夹吗?"
                          onConfirm={this.deleteFolder}
                          okText="确定"
                          cancelText="取消"
                        >
                        <MenuItem style={{ display: disableEdit }}>
                            删除
                        </MenuItem>
                        </Popconfirm>
                </ContextMenu>

                <ContextMenu forEle=".task-item">
                    <MenuItem onClick={this.initEditTask}>编辑</MenuItem>
                    <Popconfirm
                        title="确定删除这个任务吗?"
                        onConfirm={this.deleteTask}
                        okText="确定"
                        cancelText="取消"
                    >
                    <MenuItem>
                        删除
                    </MenuItem>
                    </Popconfirm>
                </ContextMenu>

                <ContextMenu forEle=".resource-folder-item">
                    <MenuItem
                      onClick={() => this.doAction(modalAction.ADD_RES_VISIBLE) }
                    >
                        上传资源
                      </MenuItem>
                    <MenuItem
                      onClick={() => this.doAction(modalAction.ADD_RES_CATA_VISIBLE) }>
                        新建文件夹
                    </MenuItem>
                    <MenuItem
                        style={{ display: disableEdit }}
                        onClick={() => this.doAction(modalAction.EDIT_RES_CATA_VISIBLE)}>
                    编辑
                    </MenuItem>
                    <Popconfirm
                        title="确定删除这个文件夹吗?"
                        onConfirm={this.deleteFolder}
                        okText="确定"
                        cancelText="取消"
                    >
                        <MenuItem style={{ display: disableEdit }}>
                            删除
                        </MenuItem>
                    </Popconfirm>
                </ContextMenu>

                <ContextMenu forEle=".resource-item">
                    <MenuItem
                      onClick={() => { this.setState({ visibleResRename: true }) }}>重命名</MenuItem>
                    <Popconfirm
                      title="确定删除这个资源吗?"
                      onConfirm={this.deleteResource}
                      okText="确定"
                      cancelText="取消"
                    >
                        <MenuItem>
                            删除
                        </MenuItem>
                    </Popconfirm>
                </ContextMenu>

                <ContextMenu forEle=".function-folder-item">
                    <MenuItem
                         onClick={() => {
                            this.setState({ visibleFn: true })
                        }}
                    >
                        新建函数
                    </MenuItem>
                    <MenuItem 
                        onClick={() => this.doAction(modalAction.ADD_FUNC_CATA_VISIBLE)}>
                        新建文件夹
                    </MenuItem>
                    <MenuItem 
                        style={{ display: disableEdit }}
                        onClick={() => this.doAction(modalAction.EDIT_FUNC_CATA_VISIBLE)
                    }>
                        编辑
                    </MenuItem>
                    <Popconfirm
                          title="确定删除这个文件夹吗?"
                          onConfirm={this.deleteFolder}
                          okText="确定"
                          cancelText="取消"
                        >
                        <MenuItem
                            style={{ display: disableEdit }}
                        >
                            删除
                        </MenuItem>
                    </Popconfirm>
                </ContextMenu>

                <ContextMenu forEle=".function-item">
                    <MenuItem onClick={ () => {
                        this.setState({  visibleMoveFn: true })
                    }}>
                        移动
                    </MenuItem>
                    <Popconfirm
                      title="确定删除这个函数吗?"
                      onConfirm={this.delFn}
                      okText="确定"
                      cancelText="取消"
                    >
                        <MenuItem>
                            删除
                        </MenuItem>
                    </Popconfirm>
                </ContextMenu>
            </div>
        )
    }
}

export default connect(state => {

    const { 
        modal, resources,
        realtimeTree, pages, currentPage
    } = state.realtimeTask

    const treeData = realtimeTree.children || []
    let taskTree = [], resourceTree = [], functionTree = [];

    // 数据提取
    for (let i = 0; i < treeData.length; i++) {
        const treeItem = treeData[i]
        if (treeItem.catalogueType === MENU_TYPE.TASK) {
            taskTree = treeItem.children
        } else if (treeItem.catalogueType === MENU_TYPE.RESOURCE) {
            resourceTree = treeItem.children
        } else if (treeItem.catalogueType === MENU_TYPE.FUNCTION) {
            functionTree = treeItem.children
        }
    }

    return {
        pages,
        resources,
        currentPage,
        taskTree,
        resourceTree,
        functionTree,
        modal,
        realtimeTree: treeData,
        project: state.project,
    }
})(RealTimeTabPane)
