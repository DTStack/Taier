import React, { Component } from 'react'
import { connect } from 'react-redux';
import { 
    Collapse, Icon, Tooltip, 
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
    workbenchAction,
    tableTreeAction,
} from '../../store/modules/offlineTask/actionType';

import {
    workbenchActions
} from '../../store/modules/offlineTask/offlineAction' 

import { showSeach } from '../../store/modules/comm';
import { clearTreeData } from '../../store/modules/offlineTask/folderTree';
import { MENU_TYPE } from '../../comm/const';
import MyIcon from '../../components/icon';

const Panel = Collapse.Panel;
const TabPane = Tabs.TabPane;

// const MenuItemGroup = Menu.ItemGroup;

class OfflineTabPane extends Component {

    constructor(props) {
        super(props);
    }

    state = {
        subMenus: [],
    }

    componentDidMount() {
        this.props.loadTaskParams();
        this.getCatelogue();
    }

    componentWillReceiveProps(nextProps) {
        const old = this.props.project
        const newData = nextProps.project
        if (newData && ((old.id !== 0 && old.id !== newData.id) || !old)) {
            this.getCatelogue();
            this.props.closeAll();
        }
    }

    getCatelogue() {
        const { dispatch } = this.props;
        dispatch(clearTreeData())
        // 四组数据在一个接口里面, 所以不在container中dispatch
        ajax.getOfflineCatelogue({
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
            taskTreeData,
            resourceTreeData,
            functionTreeData,
            sysFunctionTreeData,
            scriptTreeData,
            tableTreeData,
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
        } = this.props;

        const { subMenus } = this.state

        const menus = []
        if (subMenus && subMenus.length > 0) {
            for (let i = 0; i < subMenus.length; i++) {
                const menuItem = subMenus[i]
                let menuContent = ''
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
                            <div>
                                {
                                    !isEmpty(taskTreeData) &&
                                    <FolderTree 
                                        type={MENU_TYPE.TASK_DEV} 
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
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="script:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                        <Menu.Item key="script:newScript">
                                            新建脚本
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
                                <Dropdown overlay={
                                    <Menu onClick={this.onMenuClick}>
                                        <Menu.Item key="resource:newFolder">
                                            新建文件夹
                                        </Menu.Item>
                                        <Menu.Item key="resource:upload">
                                            上传资源
                                        </Menu.Item>
                                        <Menu.Item key="resource:replace">
                                            替换资源
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
                            <div>
                                {
                                    !isEmpty(functionTreeData) &&
                                    <FolderTree 
                                    type={MENU_TYPE.COSTOMFUC} 
                                    treeData={functionTreeData} />
                                }
                                {
                                    !isEmpty(sysFunctionTreeData) &&
                                    <FolderTree 
                                        type={MENU_TYPE.SYSFUC} 
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
                    <TabPane tab={menuItem.name} key={menuItem.id}> 
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
        const defaultOpen = [`${taskTreeData.id}`];

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

    return {
        project: state.project,
        taskTreeData: offlineTask.taskTree,
        resourceTreeData: offlineTask.resourceTree,
        functionTreeData: offlineTask.functionTree,
        sysFunctionTreeData: offlineTask.sysFunctionTree,
        scriptTreeData: offlineTask.scriptTree,
        tableTreeData: offlineTask.tableTree,
    }
},
dispatch => {
    const actions = workbenchActions(dispatch)

    return {
        showSeachTask: function() {
            dispatch(showSeach())
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

        closeAll: () => dispatch({
            type: workbenchAction.CLOSE_ALL_TABS
        }),
        dispatch
    }
})(OfflineTabPane);
