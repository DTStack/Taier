import React, { Component } from 'react'
import { connect } from 'react-redux';
import { 
    Icon, Tooltip, 
    Tabs, Dropdown, Menu 
} from 'antd';
import { isEmpty } from 'lodash';

import FolderTree from './offline/folderTree';
import TableTree from './offline/tableTree';
import ajax from '../../api';

import {
    modalAction,
    taskTreeAction,
    resTreeAction,
    fnTreeAction,
    sysFnTreeActon,
    scriptTreeAction,
    tableTreeAction,
} from '../../store/modules/offlineTask/actionType';

import {
    workbenchActions,
} from '../../store/modules/offlineTask/offlineAction';

import { showSeach } from '../../store/modules/comm';
import { MENU_TYPE } from '../../comm/const';

const TabPane = Tabs.TabPane;

class OfflineTabPane extends Component {

    constructor(props) {
        super(props);
    }

    state = {
        subMenus: [],
        expandedKeys: [],
        expandedKeys2: []
    }

    componentDidMount() {
        this.props.loadTaskParams();
        this.getCatelogue();
    }

    componentWillReceiveProps(nextProps) {
        const old = this.props.project
        const newData = nextProps.project
        if (newData && (!old || (old.id !== 0 && old.id !== newData.id))) {
            this.getCatelogue();
        }
    }

    onExpand = (expandedKeys, { expanded, node }) => {
        console.log('onExpand', expandedKeys, expanded, node)
        this.setState({
            expandedKeys,
        })
    }

    onExpand2 = (expandedKeys) => {
        this.setState({
            expandedKeys2: expandedKeys,
        })
    }

    reloadTreeNodes = (id, type) => {
        this.props.reloadTreeNodes(id, type);
        this.setState({
            expandedKeys: [`${type}-${id}`]
        })
    }

    /**
     * 定位任务在文件树的具体位置
     */
    locateFilePos = (id, name, type) => {
        // const rootId = this.props.taskTreeData.id
        const { currentTab, currentTabData} = this.props;
        if (!currentTab) return;
        this.setState({
            expandedKeys: [`${type}-${currentTabData.nodePid}`]
        })

        const getExpandPath = (data, id, path) => {
            console.log('path:', data.id, id)
            path.push(`${type}-${data.id}`)
            if (data && data.id === id) {
                return path;
            }
            if (data.children) {
                const children = data.children
                for (let i = 0; i < children.length; i += 1) {
                    getExpandPath(children[i], id, path)
                }
            }
        }

        ajax.locateCataPosition({
            id,
            catalogueType: type,
            name: name,
        }).then(res => {
            if (res.code === 1 && res.data) {
                const data = res.data.children[0];
                const path = [];

                const checkedPath = getExpandPath(data, currentTab, path);
                console.log('checkedPath:', path);
                this.props.locateFilePos(data, type);
            }
        });

    }


    getCatelogue() {
        const { dispatch } = this.props;
        // dispatch(clearTreeData())
        // 四组数据在一个接口里面, 所以不在container中dispatch
        ajax.getOfflineCatalogue({
            isGetFile: !!1,
            nodePid: 0
        }).then(res => {
            if(res.code === 1) {
                const arrData = res.data.children;

                this.setState({  subMenus: arrData, })

                for (let i = 0; i < arrData.length; i++) {
                    const menuItem = arrData[i]
                    switch (menuItem.catalogueType) {
                        case MENU_TYPE.TASK: {
                            dispatch({
                                type: taskTreeAction.RESET_TASK_TREE,
                                payload: menuItem.children[0]
                            });
                            break;
                        }
                        case MENU_TYPE.SCRIPT: {
                            dispatch({
                                type: scriptTreeAction.RESET_SCRIPT_TREE,
                                payload: menuItem.children[0]
                            });
                            break;
                        }
                        case MENU_TYPE.RESOURCE: {
                            dispatch({
                                type: resTreeAction.RESET_RES_TREE,
                                payload: menuItem.children[0]
                            });
                            break;
                        }
                        case MENU_TYPE.FUNCTION: {
                            dispatch({
                                type: fnTreeAction.RESET_FUC_TREE,
                                payload: menuItem.children[0]
                            });
                            dispatch({
                                type: sysFnTreeActon.RESET_SYSFUC_TREE,
                                payload: menuItem.children[1]
                            });
                            break;
                        }
                        case MENU_TYPE.TABLE: {
                            dispatch({
                                type: tableTreeAction.RESET_TABLE_TREE,
                                payload: menuItem.children[0]
                            });
                            break;
                        }
                    }
                }
            }
        });
    }

    onMenuClick = ({ key }) => {
        const {
            toggleCreateTask,
            toggleUpload,
            toggleCreateFolder,
            toggleCoverUpload,
            toggleCreateFn,
            toggleCreateScript,
            showSeachTask,
        } = this.props;

        switch (key) {
            case 'task:newFolder': {
                toggleCreateFolder(MENU_TYPE.TASK_DEV)
                return;
            }
            case 'task:newTask': {
                toggleCreateTask()
                return;
            }
            case 'task:search': {
                showSeachTask()
                return;
            }
            case 'script:newScript': {
                toggleCreateScript()
                return;
            }
            case 'script:newFolder': {
                toggleCreateFolder(MENU_TYPE.SCRIPT)
                return;
            }
            case 'resource:newFolder': {
                toggleCreateFolder(MENU_TYPE.RESOURCE)
                return;
            }
            case 'resource:upload': {
                toggleUpload()
                return;
            }
            case 'resource:replace': {
                toggleCoverUpload()
                return;
            }
            case 'function:newFolder': {
                toggleCreateFolder(MENU_TYPE.COSTOMFUC)
                return;
            }
            case 'function:newFunc': {
                toggleCreateFn()
                return;
            }
        }
    }

    renderTabPanes = () => {

        const {
            taskTreeData,
            resourceTreeData,
            functionTreeData,
            sysFunctionTreeData,
            scriptTreeData,
            tableTreeData,
            currentTab,
        } = this.props;

        const { subMenus, expandedKeys, expandedKeys2 } = this.state;
        const reloadTreeNodes = this.reloadTreeNodes;

        console.log('taskTreeData', taskTreeData)

        const menus = []
        if (subMenus && subMenus.length > 0) {
            for (let i = 0; i < subMenus.length; i++) {
                const menuItem = subMenus[i]
                let menuContent = ''
                switch (menuItem.catalogueType) {
                    case MENU_TYPE.TASK: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Tooltip title="定位">
                                    <Icon
                                        type="environment"
                                        onClick={() => this.locateFilePos(currentTab, null, MENU_TYPE.TASK_DEV)}
                                    />
                                </Tooltip>
                                <Tooltip title="刷新">
                                    <Icon
                                        type="sync"
                                        style={{fontSize: '12px'}}
                                        onClick={() => reloadTreeNodes(taskTreeData.id, MENU_TYPE.TASK_DEV)}
                                    />
                                </Tooltip>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="task:newTask">
                                            新建任务
                                        </Menu.Item>
                                        <Menu.Item key="task:search">
                                            搜索任务（Ctrl + P）
                                        </Menu.Item>
                                        <Menu.Item key="task:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <div>
                                {
                                    !isEmpty(taskTreeData) &&
                                    <FolderTree 
                                        type={MENU_TYPE.TASK_DEV} 
                                        expandedKeys={expandedKeys}
                                        onExpand={this.onExpand}
                                        treeData={taskTreeData} 
                                    />
                                }
                            </div>
                        </div>
                        break;
                    }
                    case MENU_TYPE.SCRIPT: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Tooltip title="刷新">
                                    <Icon
                                        type="sync"
                                        style={{fontSize: '12px'}}
                                        onClick={() => reloadTreeNodes(scriptTreeData.id, menuItem.catalogueType)}
                                    />
                                </Tooltip>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="script:newScript">
                                            新建脚本
                                        </Menu.Item>
                                        <Menu.Item key="script:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <div>
                                { 
                                    !isEmpty(scriptTreeData) &&
                                    <FolderTree 
                                        type={menuItem.catalogueType}
                                        expandedKeys={expandedKeys}
                                        onExpand={this.onExpand}
                                        treeData={scriptTreeData} 
                                    />
                                }
                            </div>
                        </div>
                        break;
                    }
                    case MENU_TYPE.RESOURCE: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Tooltip title="刷新">
                                    <Icon
                                        type="sync"
                                        style={{fontSize: '12px'}}
                                        onClick={() => reloadTreeNodes(resourceTreeData.id, menuItem.catalogueType)}
                                    />
                                </Tooltip>
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="resource:upload">
                                            上传资源
                                        </Menu.Item>
                                        <Menu.Item key="resource:replace">
                                            替换资源
                                        </Menu.Item>
                                        <Menu.Item key="resource:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                    </Menu>
                                } trigger={['click']}>
                                    <Icon type="bars" />
                                </Dropdown>
                            </header>
                            <div>
                                {
                                    !isEmpty(resourceTreeData) &&
                                    <FolderTree 
                                        type={menuItem.catalogueType} 
                                        expandedKeys={expandedKeys}
                                        onExpand={this.onExpand}
                                        treeData={resourceTreeData} 
                                    />
                                }
                            </div>
                        </div>
                        break;
                    }
                    case MENU_TYPE.FUNCTION: {
                        menuContent = <div className="menu-content">
                            <header>
                                <Tooltip title="刷新">
                                    <Icon
                                        type="sync"
                                        style={{fontSize: '12px'}}
                                        onClick={() => reloadTreeNodes(functionTreeData.id, MENU_TYPE.COSTOMFUC)}
                                    />
                                </Tooltip>
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
                            <div>
                                {
                                    !isEmpty(functionTreeData) &&
                                    <FolderTree 
                                    type={MENU_TYPE.COSTOMFUC}
                                    expandedKeys={expandedKeys}
                                    onExpand={this.onExpand}
                                    treeData={functionTreeData} />
                                }
                                {
                                    !isEmpty(sysFunctionTreeData) &&
                                    <FolderTree 
                                        type={MENU_TYPE.SYSFUC}
                                        expandedKeys={expandedKeys2}
                                        onExpand={this.onExpand2}
                                        treeData={sysFunctionTreeData}
                                    />
                                }
                            </div>
                        </div>
                        break;
                    }
                    case MENU_TYPE.TABLE: {
                        menuContent = <TableTree treeData={tableTreeData}/>
                        break;
                    }
                    default: {
                        menuContent = '';
                        break;
                    }
                }
                menus.push(
                    <TabPane tab={menuItem.name} id={`${menuItem.catalogueType}-${menuItem.id}`} key={menuItem.id}> 
                        {menuContent} 
                    </TabPane>
                )
            }
        }

        return menus;
    }

    render() {
        const {
            taskTreeData
        } = this.props;

        return (
            <div className="g-taskOfflineSidebar task-sidebar m-tabs">
                <Tabs
                    tabPosition="left"
                    animated={false}
                    className="task-tab-menu"
                    style={{ height: '100%' }}
                >
                    { this.renderTabPanes() }
                </Tabs>
            </div>
        )
    }
}

export default connect(state => {
    const { offlineTask } = state;
    const { currentTab, tabs } = offlineTask.workbench;

    const currentTabData = tabs.filter(tab => {
        return tab.id === currentTab;
    })[0];

    return {
        project: state.project,
        taskTreeData: offlineTask.taskTree,
        resourceTreeData: offlineTask.resourceTree,
        functionTreeData: offlineTask.functionTree,
        sysFunctionTreeData: offlineTask.sysFunctionTree,
        scriptTreeData: offlineTask.scriptTree,
        tableTreeData: offlineTask.tableTree,
        currentTab,
        currentTabData,
    }
},
dispatch => {
    const actions = workbenchActions(dispatch)

    return {
        showSeachTask: function() {
            dispatch(showSeach(true))
        },

        goToTaskDev: (id) => {
            actions.openTaskInDev(id)
        },

        toggleCreateTask: function() {
            dispatch({
                type: modalAction.TOGGLE_CREATE_TASK
            });
        },

        toggleCreateScript: function () {
            dispatch({
                type: modalAction.TOGGLE_CREATE_SCRIPT
            });
        },

        toggleUpload: function() {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD
            });
        },

        toggleCoverUpload: function() {
            dispatch({
                type: modalAction.TOGGLE_UPLOAD,
                payload: {
                    isCoverUpload: true,
                }
            });
        },

        toggleCreateFolder: function(cateType) {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FOLDER,
                payload: cateType
            });
        },

        toggleCreateFn: function() {
            dispatch({
                type: modalAction.TOGGLE_CREATE_FN
            });
        },

        loadTaskParams: function() {
            actions.loadTaskParams();
        },

        reloadTreeNodes: function(nodePid, type) {
            actions.loadTreeNode(nodePid, type);
        },

        locateFilePos: function(data, type) {
            actions.locateFilePos(data, type);
        },

        dispatch
    }
})(OfflineTabPane);
