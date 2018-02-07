import React, { Component } from 'react'
import { connect } from 'react-redux';
import { Collapse, Icon, Tooltip, Menu } from 'antd';
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
const SubMenu = Menu.SubMenu;
const MenuItemGroup = Menu.ItemGroup;

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

    renderSubMenu = () => {
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
                                <Tooltip title="新建文件夹">
                                    <Icon
                                        style={{ marginRight: '8px' }}
                                        className="right"
                                        type="folder-add"
                                        onClick={toggleCreateFolder.bind(this, MENU_TYPE.TASK_DEV)}
                                    />
                                </Tooltip>
                                <Tooltip title="新建任务">
                                    <span className="anticon right">
                                        <MyIcon
                                            onClick={toggleCreateTask}
                                            type="create-task" />
                                    </span>
                                </Tooltip>
                                <Tooltip title={`搜索任务 Ctrl + P`}>
                                    <Icon
                                        style={{ marginRight: '0px' }}
                                        className="right"
                                        onClick={showSeachTask}
                                        type="search" />
                                </Tooltip>
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
                                <Tooltip title="新建脚本">
                                    <Icon onClick={toggleCreateScript} type="code-o" />
                                </Tooltip>
                                <Tooltip title="新建文件夹">
                                    <Icon
                                        className="right"
                                        type="folder-add"
                                        onClick={toggleCreateFolder.bind(this, MENU_TYPE.SCRIPT)}
                                    />
                                </Tooltip>
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
                                <Tooltip title="新建文件夹">
                                    <Icon
                                        style={{ marginRight: '8px' }}
                                        className="right"
                                        type="folder-add"
                                        onClick={toggleCreateFolder.bind(this, MENU_TYPE.RESOURCE)} />
                                </Tooltip>
                                <Tooltip title="上传资源">
                                    <span className="anticon right">
                                        <MyIcon
                                            onClick={toggleUpload}
                                            type="upload-res" />
                                    </span>
                                </Tooltip>
                                <Tooltip title="替换资源">
                                    <Icon
                                        onClick={toggleCoverUpload}
                                        style={{ marginRight: '0px' }}
                                        className="right"
                                        type="copy" />
                                </Tooltip>
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
                                <Tooltip title="新建函数">
                                    <Icon type="api" onClick={toggleCreateFn} />
                                </Tooltip>
                                <Tooltip title="新建文件夹">
                                    <Icon type="folder-add" onClick={toggleCreateFolder.bind(this, MENU_TYPE.COSTOMFUC)} />
                                </Tooltip>
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
                    <SubMenu title={menuItem.name} key={menuItem.id}> 
                        {menuContent} 
                    </SubMenu>
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
            <div className="g-taskOfflineSidebar task-sidebar">
                <Menu
                    key={ `${taskTreeData.id}` }
                    defaultSelectedKeys={ defaultOpen }
                    defaultOpenKeys={ defaultOpen }
                    mode="inline"
                >
                    {this.renderSubMenu()}
                </Menu>
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
