import * as React from 'react'

import { Tree, TreeSelect, Badge } from 'antd'

import utils from 'utils'
import { MENU_TYPE } from '../../../comm/const'
import { taskTypeIcon } from '../../../comm'

const TreeNode = Tree.TreeNode

class FolderTree extends React.Component<any, any> {
    selEle: any;
    renderFileInfo = (menuType: any, file: any) => {
        if (
            (menuType === MENU_TYPE.TASK || menuType === MENU_TYPE.SCRIPT) &&
            file.type === 'file'
        ) {
            const lockInfo = file.readWriteLockVO || {};
            return ` ${lockInfo.lastKeepLockUserName} 锁定于 ${utils.formatDateTime(lockInfo.gmtModified)}`;
        }
        return file.createUser;
    }

    renderStatusBadge = (menuType: any, file: any) => {
        if (
            (menuType === MENU_TYPE.TASK || menuType === MENU_TYPE.SCRIPT) &&
            file.type === 'file'
        ) {
            let status = "success"
            const lockStatus = file.readWriteLockVO && file.readWriteLockVO.getLock;
            if (!lockStatus) {
                status = "default"
            } else {
                status = "success"
            }
            return <Badge
                    status={status as any}
                />
        }
        return null;
    }

    renderTreeNodes = () => {
        const { treeType, treeData, isFolderPicker } = this.props

        const loopTree = (tree: any) => {
            return tree && tree.map((item: any) => {
                const key = item.id + ':' + item.type;
                const isLeaf = item.type === 'file';
                const taskType = item.taskType;

                if (isFolderPicker && isLeaf) {
                    return null;
                }

                let claTitle = ''
                if (item.type === 'file') {
                    switch (treeType) {
                        case MENU_TYPE.TASK:
                            claTitle = 'task-item'
                            break;
                        case MENU_TYPE.RESOURCE:
                            claTitle = 'resource-item'
                            break;
                        case MENU_TYPE.COSTOMFUC:
                            claTitle = 'function-item'
                            break;
                        default:
                            claTitle = 'file-item'
                    }
                } else {
                    switch (treeType) {
                        case MENU_TYPE.TASK:
                            claTitle = 'task-folder-item'
                            break;
                        case MENU_TYPE.RESOURCE:
                            claTitle = 'resource-folder-item'
                            break;
                        case MENU_TYPE.COSTOMFUC:
                        case MENU_TYPE.FUNCTION:
                            claTitle = 'function-folder-item'
                            break;
                        default:
                            claTitle = 'folder-item'
                    }
                }

                const title = (
                    <span
                        title={item.name}
                        id={`JS_${item.id}`}
                        style={{ padding: '8px 0px' }}
                        className={claTitle}>
                        {this.renderStatusBadge(treeType, item)}
                        {item.name}
                        <i className="item-tooltip"><span style={{ color: '#ccc' }}>{this.renderFileInfo(treeType, item)}</span></i>
                    </span>
                );

                return (
                    <TreeNode
                        title={title}
                        name={item.name}
                        key={key}
                        value={item.id}
                        treeType={treeType}
                        isLeaf={isLeaf}
                        disabled={item.id === '0'}
                        data={item}
                        className={taskTypeIcon(taskType)}
                    >
                        {item.children && loopTree(item.children)}
                    </TreeNode >
                )
            })
        }

        return loopTree(treeData)
    }

    render () {
        let treeContent:any = ''
        const {
            onRightClick, onSelect, onChange, multiple, id,
            loadData, isPicker, placeholder, disabled, value,
            expandedKeys, onExpand, selectedKeys
        } = this.props;
        if (isPicker) {
            treeContent = (
                <div ref={(ins: any) => this.selEle = ins} className='org-tree-select-wrap'>
                    <TreeSelect
                        showSearch
                        allowClear
                        key={id}
                        value={value}
                        loadData={loadData}
                        onChange={onChange}
                        onSelect={onSelect}
                        disabled={disabled}
                        multiple={multiple}
                        size="large"
                        treeNodeFilterProp="name"
                        filterTreeNode={(inputValue: any, treeNode: any) => {
                            return treeNode.props.name.toUpperCase().indexOf(inputValue.toUpperCase()) !== -1;
                        }}
                        getPopupContainer={() => this.selEle}
                        placeholder={placeholder || '请选择存储位置'}
                        dropdownStyle={{ maxHeight: 400, overflow: 'auto', top: '32px', left: 0 }}
                    >
                        {this.renderTreeNodes()}
                    </TreeSelect>
                </div>
            )
        } else {
            const fix = {
                disabled,
                onChange
            }
            treeContent = (
                <Tree
                    showIcon
                    // disabled={disabled}
                    onRightClick={onRightClick}
                    onSelect={onSelect}
                    // onChange={onChange}
                    loadData={loadData}
                    selectedKeys={selectedKeys}
                    expandedKeys={expandedKeys}
                    onExpand={onExpand}
                    autoExpandParent={false}
                    {...fix}
                >
                    {this.renderTreeNodes()}
                </Tree>
            )
        }

        return <div style={{ position: 'relative', display: 'block' }} >
            {treeContent}
        </div>
    }
}

export default FolderTree
