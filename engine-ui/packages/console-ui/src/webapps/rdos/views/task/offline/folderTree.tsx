import * as React from 'react';
import { connect } from 'react-redux';
import { cloneDeep } from 'lodash';
import {
    Tree, TreeSelect,
    Modal, Badge
} from 'antd';

import utils from 'utils';
import CtxMenu from 'widgets/ctx-menu';

import ajax from '../../../api';

import {
    workbenchActions as mapDispatchToProps
} from '../../../store/modules/offlineTask/offlineAction'

import { taskTypeIcon, resourceTypeIcon } from '../../../comm'
import { MENU_TYPE, TASK_TYPE } from '../../../comm/const'

const { TreeNode } = Tree;
const confirm = Modal.confirm;

const isRootFolder = (node: any) => {
    return node.level === 1;
}

class FolderTree extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
    }
    selEle: any;
    onLoadData (type: any, treeNode: any) {
        const { loadTreeNode, ispicker } = this.props;
        const { data } = treeNode.props;
        return new Promise((resolve: any) => {
            const cataType = type || data.catalogueType
            if (ispicker && data.children && data.children.length > 0) {
                resolve();
                return;
            }
            loadTreeNode(data.id, cataType, {
                taskType: data.taskType,
                parentId: data.parentId
            });
            resolve();
        });
    }

    handleSelect (selectedKeys: any, e: any) {
        const { isLeaf, value, treeType, data, eventKey } = e.node.props;
        let { openTab, tabs, currentTab, expandedKeys, onExpand, type } = this.props;
        const isWorkflow = data.taskType === TASK_TYPE.WORKFLOW;
        if (!isLeaf && !isWorkflow) {
            const eventKeyIndex = expandedKeys.indexOf(eventKey);
            if (eventKeyIndex > -1) {
                this.onLoadData(type, e.node)
                expandedKeys.splice(eventKeyIndex, 1);
                onExpand(expandedKeys, { expanded: false })
            } else {
                this.onLoadData(type, e.node)
                expandedKeys.push(eventKey)
                onExpand(expandedKeys, { expanded: true })
            }
        } else {
            switch (treeType) {
                case MENU_TYPE.SCRIPT:
                case MENU_TYPE.TASK_DEV: {
                    openTab({
                        id: value,
                        tabs,
                        currentTab,
                        treeType,
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
    }

    handleResNodeSelected (id: any) {
        this.props.showResViewModal(id);
    }

    handleFnNodeSelected (id: any) {
        this.props.showFnViewModal(id);
    }

    /**
     * @description 生成右键菜单选项
     * @param {any} type 节点类型 file/folder
     * @param {any} treeType 树类型 TASK/FUC/RES
     * @param {any} data 节点data
     * @memberof FolderTree
     */
    generateCtxMenu (type: any, treeType: any, data: any) {
        let arr: any = [];
        let operations: any;

        switch (treeType) {
            case MENU_TYPE.TASK:
            case MENU_TYPE.TASK_DEV: {
                const isWorkflowNode = data && data.isSubTask === 1; // 工作流子节点
                const isWorkflow = data && data.taskType === TASK_TYPE.WORKFLOW; // 工作流
                const isScienceTask = data && (data.taskType == TASK_TYPE.NOTEBOOK || data.taskType == TASK_TYPE.EXPERIMENT)
                const isLocked = data && data.readWriteLockVO && !data.readWriteLockVO.getLock; // 任务是否上锁

                if (isWorkflowNode) {
                    return [{
                        txt: '克隆至工作流',
                        cb: this.cloneToWorkflow.bind(this, data)
                    }];
                }

                if ((type === 'file' || isWorkflow)) {
                    if (isWorkflow) {
                        if (isLocked) {
                            operations = [];
                        } else {
                            operations = arr.concat([{
                                txt: '编辑',
                                cb: this.editTask.bind(this, data)
                            }, {
                                txt: '克隆',
                                cb: this.cloneTask.bind(this, data)
                            }, {
                                txt: '删除',
                                cb: this.deleteTask.bind(this, data)
                            }])
                        }
                    } else {
                        if (isLocked || isScienceTask) {
                            operations = [];
                        } else {
                            operations = arr.concat([{
                                txt: '编辑',
                                cb: this.editTask.bind(this, data)
                            }, {
                                txt: '克隆',
                                cb: this.cloneTask.bind(this, data)
                            }, {
                                txt: '克隆至工作流',
                                cb: this.cloneToWorkflow.bind(this, data)
                            }, {
                                txt: '删除',
                                cb: this.deleteTask.bind(this, data)
                            }])
                        }
                    }
                } else {
                    data.type = type;
                    operations = arr.concat([{
                        txt: '新建任务',
                        cb: this.createTask.bind(this, data)
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

            case MENU_TYPE.FUNCTION:
            case MENU_TYPE.COSTOMFUC:
                if (type === 'file') {
                    operations = arr.concat([{
                        txt: '移动',
                        cb: this.moveFn.bind(this, data)
                    }, {
                        txt: '删除',
                        cb: this.deleteFn.bind(this, data)
                    }])
                } else {
                    operations = arr.concat([{
                        txt: '新建函数',
                        cb: this.createFn.bind(this, data)
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
            case MENU_TYPE.RESOURCE:
                if (type === 'file') {
                    operations = arr.concat([
                        {
                            txt: '删除',
                            cb: this.deleteResource.bind(this, data)
                        }])
                } else {
                    operations = arr.concat([{
                        txt: '上传资源',
                        cb: this.createResource.bind(this, data)
                    }, {
                        txt: '替换资源',
                        cb: this.coverResFile.bind(this, data)
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
            case MENU_TYPE.SYSFUC:
            case MENU_TYPE.LIBRASYSFUN:
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
                } else {
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

    moveFn (data: any) {
        this.props.toggleMoveFn(data);
    }

    deleteFn (data: any) {
        const ctx = this
        confirm({
            title: '确认要删除此函数吗?',
            content: '删除的函数无法找回！',
            onOk () {
                ctx.props.delOfflineFn({
                    functionId: data.id
                }, data.parentId);
            },
            onCancel () { }
        });
    }

    createFn (data: any) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleCreateFn();
    }

    coverResFile (data: any) {
        this.props.setModalDefault(data);
        this.props.toggleCoverUpload();
    }

    deleteResource (data: any) {
        const ctx = this
        confirm({
            title: '确认要删除此资源吗?',
            content: '删除的资源无法找回！',
            onOk () {
                ctx.props.delOfflineRes({
                    resourceId: data.id
                }, data.parentId);
            },
            onCancel () { }
        });
    }

    createResource (data: any) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleUpload();
    }

    editTask (data: any) {
        ajax.getOfflineTaskByID({
            id: data.id,
            lockVersion: data.readWriteLockVO.version
        }).then((res: any) => {
            if (res.code === 1) {
                // 给待编辑的数据做特殊处理
                res.data.parentId = res.data.nodePid;
                res.data.type = 'folder';
                this.props.setModalDefault(res.data);
                this.props.toggleCreateTask();
            }
        })
    }

    // 克隆任务
    cloneTask (data: any) {
        this.setState({
            visible: true
        })
        ajax.getOfflineTaskByID({
            id: data.id,
            lockVersion: data.readWriteLockVO.version
        })
            .then((res: any) => {
                if (res.code === 1) {
                    this.props.setModalDefault(res.data);
                    this.props.toggleCloneTask();
                }
            })
    }
    // 克隆任务至工作流
    cloneToWorkflow (data: any) {
        this.props.getWorkFlowList({ taskType: TASK_TYPE.WORKFLOW })
        ajax.getOfflineTaskByID({
            id: data.id,
            lockVersion: data.readWriteLockVO.version
        })
            .then((res: any) => {
                if (res.code === 1) {
                    this.props.setModalDefault(res.data);
                    this.props.toggleCloneToWorkflow();
                }
            })
    }
    deleteTask (data: any) {
        const ctx = this
        confirm({
            title: '确认要删除此任务吗?',
            content: '删除的任务无法找回！',
            onOk () {
                ctx.props.delOfflineTask({
                    taskId: data.id
                }, data.parentId);
            },
            onCancel () { }
        });
    }

    editFolder (data: any, type: any) {
        this.props.setModalDefault(data);
        this.props.toggleCreateFolder(type);
    }

    createTask (data: any) {
        this.props.setModalDefault({
            parentId: data.id,
            type: data.type
        });
        this.props.toggleCreateTask();
    }

    /**
     * @description 在特定节点下新建文件夹
     * @param {any} data 节点对象
     * @param {any} type 节点类型
     * @memberof FolderTree
     */
    createFolder (data: any, type: any) {
        this.props.setModalDefault({
            parentId: data.id,
            type: data.type
        });
        this.props.toggleCreateFolder(type);
    }

    deleteFolder (data: any, type: any) {
        const ctx = this
        confirm({
            title: '确认要删除此文件夹吗?',
            content: '删除的文件夹无法恢复!',
            onOk () {
                ctx.props.delOfflineFolder({
                    id: data.id
                }, data.parentId, type);
            },
            onCancel () { }
        });
    }

    editScript (data: any) {
        ajax.getScriptById({
            id: data.id
        })
            .then((res: any) => {
                if (res.code === 1) {
                    this.props.setModalDefault(res.data);
                    this.props.setModalKey(Math.random())
                    this.props.toggleCreateScript();
                }
            })
    }

    createScript (data: any) {
        this.props.setModalDefault({
            parentId: data.id
        });
        this.props.toggleCreateScript();
    }

    deleteScript (data: any) {
        const ctx = this
        confirm({
            title: '确认要删除此脚本吗?',
            content: '删除的脚本无法找回！',
            onOk () {
                ctx.props.delOfflineScript({
                    scriptId: data.id
                }, data.parentId);
            },
            onCancel () { }
        });
    }

    renderStatusBadge = (menuType: any, file: any) => {
        if (
            (menuType === MENU_TYPE.TASK_DEV || menuType === MENU_TYPE.SCRIPT) &&
            file.type === 'file' && file.taskType !== TASK_TYPE.WORKFLOW
        ) {
            let status: any = 'success'
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

    renderFileInfo = (menuType: any, file: any) => {
        if (
            (menuType === MENU_TYPE.TASK_DEV || menuType === MENU_TYPE.SCRIPT) &&
            file.type === 'file'
        ) {
            const lockInfo = file.readWriteLockVO;
            return ` ${lockInfo.lastKeepLockUserName} 锁定于 ${utils.formatDateTime(lockInfo.gmtModified)}`;
        }
        return file.createUser;
    }

    onRightClick = (e: any) => {
        console.log(e);
        const { saveEngineType } = this.props;
        const engineType = e.node.props.data.engineType;
        saveEngineType(engineType)
    }

    genetateTreeNode () {
        const { treeData, type, ispicker, isFilepicker, acceptRes } = this.props;
        const treeType = type;

        const loop = (data: any) => {
            const { createUser, id, name, type, taskType, resourceType } = data;

            // 过滤不匹配资源类型，
            if (isFilepicker && type === 'file' && acceptRes !== undefined) {
                if (acceptRes !== resourceType) return null;
            }
            // 目录选择过滤掉具体文件
            if (ispicker && !isFilepicker && data.children) {
                // potential mutate store directly
                data.children = data.children.filter((o: any) => {
                    return o.type === 'folder';
                });
            }

            let claName = type === 'file' ? 'file-item' : 'folder-item';
            // ! Key 用于左侧任务位置定位
            const key = `${treeType}-${type}-${id}`;

            return <TreeNode
                title={
                    ispicker
                        ? <span className={claName}>
                            { name }&nbsp;
                            <i className="item-tooltip">
                                <span style={{ color: '#ccc' }}>{createUser}</span>
                            </i>
                        </span>
                        : <CtxMenu
                            id={ id }
                            key={ `${taskType}-ctxmenu-${id}` }
                            operations={ this.generateCtxMenu(type, treeType, data) }
                            ctxMenuWrapperClsName= {this.ctxMenuWrapperClsName}
                        >
                            <span
                                id={`JS_${id}${type === 'folder' ? '_folder' : ''}`}
                                title={name}
                                className={claName}>
                                { this.renderStatusBadge(treeType, data) }
                                { name }&nbsp;
                                <i className="item-tooltip">
                                    <span style={{ color: '#ccc' }}>{this.renderFileInfo(treeType, data)}</span>
                                </i>
                            </span>
                        </CtxMenu>
                }
                value={id}
                name={name}
                disabled={id === '0'}
                data={data}
                treeType={treeType}
                className={taskTypeIcon(taskType, data) || resourceTypeIcon(resourceType)}
                isLeaf={type === 'file'}
                key={key}
            >
                {data.children && data.children.map((o: any) => loop(o))}
            </TreeNode>
        };
        const clone = cloneDeep(treeData);

        return loop(clone);
    }

    ctxMenuWrapperClsName: string = 'ctx-menu-wrapper';
    render () {
        const {
            type, placeholder, currentTab, multiple, key,
            onExpand, expandedKeys, onChange, couldEdit, allowClear
        } = this.props;
        return (
            <div>
                {this.props.ispicker
                    ? <div ref={(ins: any) => this.selEle = ins} className='org-tree-select-wrap'>
                        <TreeSelect
                            className={this.ctxMenuWrapperClsName}
                            allowClear={allowClear}
                            disabled={typeof couldEdit == 'boolean' && !couldEdit}
                            size="large"
                            key={key || type}
                            multiple={multiple}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto', top: '32px', left: 0 }}
                            showSearch={!this.props.isFilepicker}
                            {...{ showIcon: true }}
                            loadData={this.onLoadData.bind(this, type)}
                            onChange={onChange}
                            defaultValue={this.props.defaultNode}
                            {...{ getContainer: () => this.selEle }}
                            placeholder={placeholder}
                            treeNodeFilterProp="name"
                            filterTreeNode={(inputValue: any, treeNode: any) => {
                                return treeNode.props.name && treeNode.props.name.toUpperCase().indexOf(inputValue.toUpperCase()) !== -1;
                            }}
                        >
                            {this.genetateTreeNode()}
                        </TreeSelect>
                    </div>
                    : <Tree
                        className={this.ctxMenuWrapperClsName}
                        onRightClick={this.onRightClick}
                        showIcon={true}
                        {...{ placeholder: placeholder }}
                        selectedKeys={[`${type}-file-${currentTab}`]}
                        loadData={this.onLoadData.bind(this, type)}
                        expandedKeys={expandedKeys}
                        onExpand={onExpand}
                        autoExpandParent={false}
                        onSelect={this.handleSelect.bind(this)}
                    >
                        {this.genetateTreeNode()}
                    </Tree>
                }
            </div>
        )
    }
}

const FolderTreeContainer = connect((state: any, ownProps: any) => {
    const { workbench } = state.offlineTask;
    const { tabs, currentTab } = workbench;

    return { tabs, currentTab }
}, mapDispatchToProps)(FolderTree);

export default FolderTreeContainer;
