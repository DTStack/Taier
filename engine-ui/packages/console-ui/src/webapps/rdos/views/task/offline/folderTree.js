import React from 'react';
import { connect } from 'react-redux';
import { cloneDeep } from 'lodash';
import { 
    Tree, TreeSelect, Input, 
    message, Modal, Badge 
} from 'antd';

import utils from 'utils';
import CtxMenu from 'widgets/ctx-menu';

import ajax from '../../../api';

import {
    workbenchActions as mapDispatchToProps
} from '../../../store/modules/offlineTask/offlineAction'

import { taskTypeIcon, resourceTypeIcon } from '../../../comm'
import { TASK_TYPE, MENU_TYPE } from '../../../comm/const'

const { TreeNode } = Tree;
const confirm = Modal.confirm;

const isRootFolder = (node) => {
    return node.level === 1;
}


class FolderTree extends React.Component {

    constructor(props) {
        super(props);
        this.handleChange = this.props.onChange;
    }

    onLoadData(type, treeNode) {
        const { loadTreeNode, ispicker } = this.props;
        const ctx = this;
        const { data } = treeNode.props;
        return new Promise((resolve) => {
            const cataType = type || data.catalogueType
            if(ispicker&&data.children&&data.children.length>0){
                resolve();
                return;
            }
            ctx._asyncLoadData = true;
            loadTreeNode(data.id, cataType);
            resolve();
        });
    }

    handleSelect(selectedKeys, e) {
        const { isLeaf, value, treeType, data } = e.node.props;
        const { openTab, tabs, currentTab } = this.props;

        if(!isLeaf) return;
        switch(treeType) {
            case MENU_TYPE.SCRIPT:
            case MENU_TYPE.TASK_DEV: {
                openTab({ 
                    id: value, tabs, currentTab, treeType, 
                    lockInfo: data.readWriteLockVO 
                });
                break;
            }

            case MENU_TYPE.RESOURCE: {
                this.handleResNodeSelected(value);
                break;
            }

            case MENU_TYPE.SYSFUC:
            case MENU_TYPE.COSTOMFUC: {
                this.handleFnNodeSelected(value);
                break;
            }

            default: break;
        }
    }

    handleResNodeSelected(id) {
        this.props.showResViewModal(id);
    }

    handleFnNodeSelected(id) {
        this.props.showFnViewModal(id);
    }

    /**
     * @description 生成右键菜单选项
     * @param {any} type 节点类型 file/folder
     * @param {any} treeType 树类型 TASK/FUC/RES
     * @param {any} data 节点data
     * @memberof FolderTree
     */
    generateCtxMenu(type, treeType, data) {
        let arr = [];
        let operations;

        switch(treeType) {
            case MENU_TYPE.TASK:
            case MENU_TYPE.TASK_DEV:
                if(type === 'file') {
                    operations = arr.concat([{
                        txt: '编辑',
                        cb: this.editTask.bind(this, data)
                    },{
                        txt: '删除',
                        cb: this.deleteTask.bind(this, data)
                    }])
                }
                else {
                    operations = arr.concat([{
                        txt: '新建任务',
                        cb: this.createTask.bind(this, data)
                    },{
                        txt: '新建文件夹',
                        cb: this.createFolder.bind(this, data, treeType)
                    }])
                    if (!isRootFolder(data)) {
                        operations = operations.concat([{
                            txt: '编辑',
                            cb: this.editFolder.bind(this, data, treeType)
                        },{
                            txt: '删除',
                            cb: this.deleteFolder.bind(this, data, treeType)
                        }])
                    }
                }
            break;

            case MENU_TYPE.FUNCTION:
            case MENU_TYPE.COSTOMFUC:
                if(type === 'file') {
                    operations = arr.concat([{
                        txt: '移动',
                        cb: this.moveFn.bind(this, data)
                    },{
                        txt: '删除',
                        cb: this.deleteFn.bind(this, data)
                    }])
                }
                else {
                    operations = arr.concat([{
                        txt: '新建函数',
                        cb: this.createFn.bind(this, data)
                    },{
                        txt: '新建文件夹',
                        cb: this.createFolder.bind(this, data, treeType)
                    }])

                    if (!isRootFolder(data)) {
                        operations = operations.concat([{
                            txt: '编辑',
                            cb: this.editFolder.bind(this, data, treeType)
                        },{
                            txt: '删除',
                            cb: this.deleteFolder.bind(this, data, treeType)
                        }])
                    }
                }
                break;
            case MENU_TYPE.RESOURCE:
                if(type === 'file') {
                    operations = arr.concat([
                    {
                        txt: '替换',
                        cb: this.coverResFile.bind(this, data)
                    },
                    {
                        txt: '删除',
                        cb: this.deleteResource.bind(this, data)
                    }])
                }
                else {
                    operations = arr.concat([{
                        txt: '上传资源',
                        cb: this.createResource.bind(this, data)
                    },{
                        txt: '新建文件夹',
                        cb: this.createFolder.bind(this, data, treeType)
                    }])

                    if (!isRootFolder(data)) {
                        operations = operations.concat([{
                            txt: '编辑',
                            cb: this.editFolder.bind(this, data, treeType)
                        }, {
                            txt: '删除',
                            cb: this.deleteFolder.bind(this, data, treeType)
                        }])
                    }
                }
                break;
            case MENU_TYPE.SYSFUC:
                operations = [];
                break;
            case MENU_TYPE.SCRIPT:
                if (type === 'file') {
                    operations = arr.concat([{
                        txt: '编辑',
                        cb: this.editScript.bind(this, data)
                    }, {
                        txt: '删除',
                        cb: this.deleteScript.bind(this, data)
                    }])
                }
                else {
                    operations = arr.concat([{
                        txt: '新建脚本',
                        cb: this.createScript.bind(this, data)
                    }, {
                        txt: '新建文件夹',
                        cb: this.createFolder.bind(this, data, treeType)
                    }])

                    if (!isRootFolder(data)) {
                        operations = operations.concat([{
                            txt: '编辑',
                            cb: this.editFolder.bind(this, data, treeType)
                        }, {
                            txt: '删除',
                            cb: this.deleteFolder.bind(this, data, treeType)
                        }])
                    }
                }
            break;
        }

        return operations;
    }

    moveFn(data) {
        this.props.toggleMoveFn(data);
    }

    deleteFn(data) {
        const ctx = this
        confirm({
            title: '确认要删除此函数吗?',
            content: '删除的函数无法找回！',
            onOk() {
                ctx.props.delOfflineFn({
                    functionId: data.id
                }, data.parentId);
            },
            onCancel() {}
        });
    }

    createFn(data) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleCreateFn();
    }

    coverResFile(data) {
        this.props.setModalDefault(data);
        this.props.toggleCoverUpload();
    }

    deleteResource(data) {
        const ctx = this
        confirm({
            title: '确认要删除此资源吗?',
            content: '删除的资源无法找回！',
            onOk() {
                ctx.props.delOfflineRes({
                    resourceId: data.id
                }, data.parentId);
            },
            onCancel() {}
        });
    }

    createResource(data) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleUpload();
    }

    editTask(data) {
        ajax.getOfflineTaskByID({
            id: data.id,
            lockVersion: data.readWriteLockVO.version
        })
        .then(res => {
            if(res.code === 1) {
                this.props.setModalDefault(res.data);
                this.props.toggleCreateTask();
            }
        })
    }

    deleteTask(data) {
        const ctx = this
        confirm({
            title: '确认要删除此任务吗?',
            content: '删除的任务无法找回！',
            onOk() {
                ctx.props.delOfflineTask({
                    taskId: data.id
                }, data.parentId);
            },
            onCancel() {}
        });
    }

    editFolder(data, type) {
        this.props.setModalDefault(data);
        this.props.toggleCreateFolder(type);
    }

    createTask(data) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleCreateTask();
    }

    /**
     * @description 在特定节点下新建文件夹
     * @param {any} data 节点对象
     * @param {any} type 节点类型
     * @memberof FolderTree
     */
    createFolder(data, type) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleCreateFolder(type);
    }

    deleteFolder(data, type) {
        const ctx = this
        confirm({
            title: '确认要删除此文件夹吗?',
            content: '删除的文件夹无法恢复!',
            onOk() {
                ctx.props.delOfflineFolder({
                    id: data.id
                }, data.parentId, type);
            },
            onCancel() {}
        });
    }

    editScript(data) {
        ajax.getScriptById({
            id: data.id
        })
        .then(res => {
            if (res.code === 1) {
                this.props.setModalDefault(res.data);
                this.props.toggleCreateScript();
            }
        })
    }

    createScript(data) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleCreateScript();
    }

    deleteScript(data) {
        const ctx = this
        confirm({
            title: '确认要删除此脚本吗?',
            content: '删除的脚本无法找回！',
            onOk() {
                ctx.props.delOfflineScript({
                    scriptId: data.id
                }, data.parentId);
            },
            onCancel() { }
        });
    }

    renderStatusBadge = (menuType, file) => {
        if (
            (menuType === MENU_TYPE.TASK_DEV || menuType === MENU_TYPE.SCRIPT) 
            && file.type === 'file'
        ) {
            let status = 'success'
            const lockStatus = file.readWriteLockVO && file.readWriteLockVO.getLock;
            if (!lockStatus) {
                status = 'default'
            } else {
                status = 'success'
            }
            return <Badge status={status} />
        }
        return null;
    }

    renderFileInfo = (menuType, file) => {
        if (
            (menuType === MENU_TYPE.TASK_DEV || menuType === MENU_TYPE.SCRIPT) 
            && file.type === 'file'
        ) {
            const lockInfo = file.readWriteLockVO;
            return ` ${lockInfo.lastKeepLockUserName} 锁定于 ${utils.formatDateTime(lockInfo.gmtModified)}`;
        }
        return file.createUser;
    }

    genetateTreeNode() {

        const { treeData, type, ispicker, isFilepicker, acceptRes } = this.props;
        const treeType = type;

        const loop = (data) => {
            const { createUser, id, name, type, taskType, resourceType } = data;

            // 过滤不匹配资源类型，
            if (isFilepicker && type === 'file' && acceptRes !== undefined ) {
                if (acceptRes !== resourceType) return null;
            }

            // 目录选择过滤掉具体文件
            if (ispicker && !isFilepicker && data.children !== null) {
                // potential mutate store directly
                data.children = data.children.filter(o => {
                    return o.type === 'folder';
                });
            }

            return <TreeNode
                title={
                    ispicker?
                    <span className={type === 'file' ? 'task-item' : 'folder-item'}>
                        { name }
                        <i style={{color: 'rgb(217, 217, 217)', fontSize: '12px'}}>
                            {createUser}
                        </i>
                    </span> :
                    <CtxMenu
                        id={ id }
                        key={ `${taskType}-ctxmenu-${id}` }
                        operations={ this.generateCtxMenu(type, treeType, data) } >
                        <span title={name} className={type === 'file' ? 'task-item' : 'folder-item'}>
                            { this.renderStatusBadge(treeType, data) }
                            { name } 
                            <i style={{color: 'rgb(217, 217, 217)', fontSize: '12px'}}>
                                { this.renderFileInfo(treeType, data) }
                            </i>
                        </span>
                    </CtxMenu>
                }
                key={`${treeType}-${id}`}
                value={ id }
                name={name}
                isLeaf={type === 'file'}
                disabled={id === '0'}
                data={data}
                treeType={treeType}
                className={taskTypeIcon(taskType)||resourceTypeIcon(resourceType)}
            >
                { data.children && data.children.map(o => loop(o)) }
            </TreeNode>
        };
        const clone = cloneDeep(treeData);

        return loop(clone);
    }

    render() {
        const { type, placeholder, currentTab, onExpand, expandedKeys } = this.props;
        console.log('expandedKeys:', expandedKeys)

        return (
            <div>
                {this.props.ispicker ?
                <div ref={(ins) => this.selEle = ins } className='org-tree-select-wrap'>
                    <TreeSelect
                        size="large"
                        key={type}
                        dropdownStyle={{ maxHeight: 400, overflow: 'auto', top: '32px', left: 0 }}
                        showSearch={ !this.props.isFilepicker }
                        showIcon={ true }
                        loadData={ this.onLoadData.bind(this, type) }
                        onChange={ this.handleChange }
                        defaultValue={ this.props.defaultNode }
                        getPopupContainer={() => this.selEle }
                        placeholder={placeholder}
                        treeNodeFilterProp="name"
                    >
                        { this.genetateTreeNode() }
                    </TreeSelect>
                </div> :
                <Tree 
                    showIcon={ true }
                    placeholder={placeholder}
                    selectedKeys={[`${type}-${currentTab}`]}
                    loadData={ this.onLoadData.bind(this, type) }
                    // expandedKeys={ expandedKeys }
                    // onExpand={ onExpand }
                    defaultExpandAll
                    onSelect={ this.handleSelect.bind(this) }
                    // autoExpandParent={false}
                >
                    { this.genetateTreeNode() }
                </Tree>
                }
            </div>
        )
    }
}

const FolderTreeContainer = connect((state, ownProps) => {
        const { workbench } = state.offlineTask;
        const { tabs, currentTab } = workbench;

        return { tabs, currentTab }
}, mapDispatchToProps )(FolderTree);

export default FolderTreeContainer;