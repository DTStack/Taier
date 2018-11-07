import React, { Component } from 'react'
import { connect } from 'react-redux'
import { hashHistory } from 'react-router'
import { union } from 'lodash';

import {
    Menu, message, Tabs, Dropdown,
    Popconfirm, Icon, Tooltip,
} from 'antd'

import {
    ContextMenu,
    MenuItem,
} from 'widgets/context-menu'
import { scrollToView } from 'funcs';


import * as BrowserAction from '../../store/modules/realtimeTask/browser'
import { actions as collectionActions } from '../../store/modules/realtimeTask/collection';
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

import { MENU_TYPE, TASK_TYPE, DATA_SYNC_TYPE } from '../../comm/const';

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
        expandedKeys: [],
        expandedKeys2: [],
    }

    componentDidMount() {
        const { dispatch, realtimeTree } = this.props
        if(!realtimeTree||!realtimeTree.length){
            dispatch(TreeAction.getRealtimeTree(rootNode))
        }
        dispatch(ResAction.getResources())
        this.loadTaskTypes();
    }

    componentWillReceiveProps(nextProps) {
        const old = this.props.project
        const newData = nextProps.project
        if (newData && old.id !== 0 && old.id !== newData.id) {
            const { dispatch } = this.props
            dispatch(TreeAction.getRealtimeTree(rootNode))
            dispatch(ResAction.getResources());
            this.setState({
                expandedKeys: [],
                expandedKeys2: [],
            })
        }

        if (this.props.currentPage !== nextProps.currentPage) {
            this.locateFilePos(nextProps.currentPage.id, MENU_TYPE.TASK_DEV)
        }
    }

    onExpand = (expandedKeys, { expanded }) => {
        let keys = expandedKeys;
        if (expanded) {
            keys = union(this.state.expandedKeys, keys)
        }
        this.setState({
            expandedKeys: keys,
        })
    }

    onExpand2 = (expandedKeys) => {
        this.setState({
            expandedKeys2: expandedKeys,
        })
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

    clickFolderOpen = (info, type) => {
        const { expandedKeys, expandedKeys2 } = this.state;
        const { eventKey } = info.node.props
        let nowExpaned;
        if (type) {
            nowExpaned = expandedKeys2;
        } else {
            nowExpaned = expandedKeys;
        }
        const eventKeyIndex = nowExpaned.indexOf(eventKey);
        if (eventKeyIndex > -1) {
            nowExpaned.splice(eventKeyIndex, 1);
        } else {
            this.loadTreeData(info.node)
            nowExpaned.push(eventKey)
        }
        this.setState({ expandedKeys, expandedKeys2 })
    }


    chooseTask = (selectedKeys, info) => {
        const { dispatch, pages } = this.props
        const { data } = info.node.props
        if (data.type === 'file') {
            const page = pages.find((item) => {
                return item.id == data.id
            })
            if (page) {
                dispatch(BrowserAction.setCurrentPage(page))
            } else {
                dispatch(BrowserAction.openPage({ id: data.id }))
            }
        } else {
            this.clickFolderOpen(info)
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
        } else {
            this.clickFolderOpen(info)
        }
    }

    chooseFn = (selectedKeys, target, type) => {
        const item = target.node.props.data
        if (item.type === 'file') {
            this.setState({
                visibleFnInfo: true,
                selectedFn: item
            })
        } else {
            this.clickFolderOpen(target, type)
        }
    }

    rightClick = ({ node }) => {
        console.log('rightClick-node', node);

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
        const node = this.state.activeNode
        const taskInfo = { nodePid: node.id }
        this.setState({ taskInfo })
        this.doAction(modalAction.ADD_TASK_VISIBLE)
    }

    createOrUpdateTask = (task) => {
        const { dispatch, currentPage, modal } = this.props
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
                    // 如果变更了存储位置
                    if (task.nodePid !== activeNode.parentId) {
                        dispatch(TreeAction.removeRealtimeTree(activeNode))
                    }
                    if(task.taskType==TASK_TYPE.DATA_COLLECTION&&task.createModel==DATA_SYNC_TYPE.GUIDE){
                        dispatch(collectionActions.initCollectionTask(task.id))
                        dispatch(collectionActions.getDataSource())
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
        return Api.uploadRes(file).then((res) => {
            if (res.code === 1) {
                this.closeModal()
                message.success('资源上传成功！')
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
        const { dispatch, modal } = this.props
        const { activeNode } = this.state
        const params = { id: cateInfo.nodePid, }
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
                this.setState({
                    visibleResRename: false,
                })
                const newNode = Object.assign(resInfo, resource)
                dispatch(TreeAction.updateRealtimeTreeNode(newNode))
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
        console.log('loadTreeData-treeNode', treeNode);

        const { dispatch } = this.props
        const node = treeNode.props.data
        return new Promise((resolve) => {
            dispatch(TreeAction.getRealtimeTree(node))
            resolve();
        })
    }

    locateFilePos = (id, type, name) => {
        if (!id) return;
        const { expandedKeys } = this.state;
        const { dispatch, realtimeTree } = this.props;

        const hasPath = (data, id, path) => {

            if (!data) return false;
            path = `${path ? path + '-' : path}${data.id}`
            if (data && data.id === id) {
                checkedPath = path;
                return true;
            }

            if (data.children) {
                const children = data.children
                for (let i = 0; i < children.length; i += 1) {
                    if (hasPath(children[i], id, path)) {
                        return true;
                    }
                }
            }
            return false;
        }

        const getExpandedKey = (path) => {
            return path && path.split('-');
        }

        const scroll = () => {
            setTimeout(() => {
                scrollToView(`JS_${id}`)
            }, 0)
        }

        let checkedPath = '', path = ''; // 路径存储

        if (hasPath(realtimeTree[0], id, path)) {
            const keys = getExpandedKey(checkedPath);
            this.setState({ expandedKeys: union(expandedKeys, keys) });
            scroll();
        } else {
            Api.locateStreamCataPosition({
                id,
                catalogueType: type,
                name: name,
            }).then(res => {
                if (res.code === 1 && res.data) {
                    const data = res.data.children[0];
                    let path = '';
                    if (hasPath(data, id, path)) {
                        const keys = getExpandedKey(checkedPath);
                        this.setState({
                            expandedKeys: keys
                        })
                    }
                    dispatch(TreeAction.mergeRealtimeTree(data));
                    scroll();
                }
            });
        }
    }

    reloadTreeNodes = (id, type) => {
        this.props.dispatch(TreeAction.getRealtimeTree({ id, catalogueType: type }))
        this.setState({
            expandedKeys: [`${id}`]
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
    /**
     * 没有内容的就不要展开了
     */
    safeExpandedKeys(expandedKeys=[],tree=[]){
        console.log(expandedKeys,tree)
        let treeKeys=[];
        function loopTree(tree){
            for(let i =0;i<tree.length;i++){
                const item=tree[i];
                treeKeys.push(''+item.id);
                if(item.children&&item.children.length){
                    loopTree(item.children)
                }
            }
        }
        loopTree(tree);
        expandedKeys=expandedKeys.filter((expandedKey)=>{
            return treeKeys.includes(expandedKey)
        })
        return expandedKeys;
    }
    renderTabPanes = () => {
        const { realtimeTree, currentPage, project } = this.props;
        const { expandedKeys, expandedKeys2 } = this.state;
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
                                <Tooltip title="定位" placement="bottom">
                                    <Icon
                                        type="environment"
                                        onClick={() => this.locateFilePos(currentPage.id, MENU_TYPE.TASK_DEV)}
                                    />
                                </Tooltip>
                                <Tooltip title="刷新" placement="bottom">
                                    <Icon
                                        type="sync"
                                        onClick={() => this.reloadTreeNodes(menuItem.children[0].id, MENU_TYPE.TASK_DEV)}
                                    />
                                </Tooltip>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="task:newTask">
                                            新建任务
                                        </Menu.Item>
                                        <Menu.Item key="task:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <div className="contentBox">
                                <div className="folder-box">
                                    <FolderTree
                                        onRightClick={this.rightClick}
                                        onSelect={this.chooseTask}
                                        loadData={this.loadTreeData}
                                        treeData={menuItem.children}
                                        treeType={menuItem.catalogueType}
                                        expandedKeys={this.safeExpandedKeys(expandedKeys,menuItem.children)}
                                        onExpand={this.onExpand}
                                        selectedKeys={[`${currentPage.id}`]}
                                    />
                                </div>
                            </div>
                        </div>
                        break;
                    }
                    case MENU_TYPE.RESOURCE: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="resource:upload">
                                            上传资源
                                        </Menu.Item>
                                        <Menu.Item key="resource:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <div className="contentBox">
                                <div className="folder-box">
                                    <FolderTree
                                        onRightClick={this.rightClick}
                                        onSelect={this.chooseRes}
                                        loadData={this.loadTreeData}
                                        treeData={menuItem.children}
                                        treeType={menuItem.catalogueType}
                                        expandedKeys={expandedKeys}
                                        onExpand={this.onExpand}
                                    />
                                </div>
                            </div>
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
                                        <Menu.Item key="function:newFunc">
                                            新建函数
                                        </Menu.Item>
                                        <Menu.Item key="function:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <div className="contentBox">
                                <div className="folder-box">
                                    <FolderTree
                                        onRightClick={this.rightClick}
                                        loadData={this.loadTreeData}
                                        onSelect={this.chooseFn}
                                        treeData={customTreeData ? [customTreeData] : []}
                                        treeType={MENU_TYPE.COSTOMFUC}
                                        expandedKeys={expandedKeys}
                                        onExpand={this.onExpand}
                                    />
                                    <FolderTree
                                        onRightClick={this.rightClick}
                                        loadData={this.loadTreeData}
                                        onSelect={(selected, e) => { this.chooseFn(selected, e, "expandedKeys2") }}
                                        treeData={systemTreeData ? [systemTreeData] : []}
                                        treeType={MENU_TYPE.SYSFUC}
                                        expandedKeys={expandedKeys2}
                                        onExpand={this.onExpand2}
                                    />
                                </div>
                            </div>
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
                            <div className="contentBox">
                                <div className="folder-box">
                                    <FolderTree
                                        loadData={this.loadTreeData}
                                        treeData={menuItem.children}
                                        treeType={menuItem.catalogueType}
                                        expandedKeys={expandedKeys}
                                        onExpand={this.onExpand}
                                    />
                                </div>
                            </div>
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
        switch (this.props.modal) {
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
            taskTree, modal,
            resourceTree, functionTree,
            project,
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
                    fnTreeData={functionTree}
                    handOk={this.moveFn}
                    handCancel={() => {
                        this.setState({ visibleMoveFn: false, activeNode: {} })
                    }}
                />

                <FnModal
                    activeNode={activeNode}
                    handOk={this.createFn}
                    visible={visibleFn}
                    fnTreeData={functionTree}
                    resTreeData={resourceTree}
                    loadTreeData={this.loadTreeData}
                    handCancel={() => { this.setState({ visibleFn: false }) }}
                />

                <FnViewModal
                    defaultData={selectedFn}
                    visible={visibleFnInfo}
                    handCancel={() => { this.setState({ visibleFnInfo: false }) }}
                />

                <div>
                    <ContextMenu targetClassName="task-folder-item">
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

                    <ContextMenu targetClassName="task-item">
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

                    <ContextMenu targetClassName="resource-folder-item">
                        <MenuItem
                            onClick={() => this.doAction(modalAction.ADD_RES_VISIBLE)}
                        >
                            上传资源
                      </MenuItem>
                        <MenuItem
                            onClick={() => this.doAction(modalAction.ADD_RES_CATA_VISIBLE)}>
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

                    <ContextMenu targetClassName="resource-item">
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

                    <ContextMenu targetClassName="function-folder-item">
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

                    <ContextMenu targetClassName="function-item">
                        <MenuItem onClick={() => {
                            this.setState({ visibleMoveFn: true })
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
