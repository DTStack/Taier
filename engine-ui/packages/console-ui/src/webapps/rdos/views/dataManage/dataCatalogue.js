import React from 'react';
import { cloneDeep } from 'lodash';
import { message } from 'antd';
import { hashHistory } from 'react-router'

import API from '../../api/dataManage';
import CatalogueTree from './catalogTree';

function appendTreeNode (treeNode, append, target) {
    const targetId = target.nodeId

    for (let i = 0; i < treeNode.length; i += 1) {
        const node = treeNode[i]
        if (node.nodeId === targetId) {
            node.children.unshift(append)
            return
        }
        if (node.children.length > 0) {
            appendTreeNode(node.children, append, target)
        }
    }
}

function removeTreeNode (treeNodes, target) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        const node = treeNodes[i]
        if (node.nodeId === target.nodeId) {
            treeNodes.splice(i, 1) // remove节点
            break;
        }
        if (node.children) {
            removeTreeNode(node.children, target)
        }
    }
}

function replaceTreeNode (treeNodes, source, target) {
    for (let i = 0; i < treeNodes.length; i += 1) {
        const node = treeNodes[i]
        if (node.nodeId === source.nodeId) {
            // 后端不返回子节点情况下兼容性处理
            if (!target.children || target.children.length == 0) {
                target.children = treeNodes[i].children;
            }
            treeNodes[i] = target // 替换节点
            break;
        }
        if (node.children) {
            replaceTreeNode(node.children, source, target)
        }
    }
}

class DataCatalogue extends React.Component {
    state = {
        treeData: []
    }

    componentDidMount () {
        this.loadCatalogue()
    }

    loadCatalogue = () => {
        API.getDataCatalogues().then(res => {
            this.setState({
                treeData: res.data && [res.data]
            })
        })
    }

    onTreeNodeEdit = (node, type, callback) => {
        const self = this
        const treeData = cloneDeep(self.state.treeData)

        switch (type) {
            case 'initAdd': {
                const tmpId = Date.now()
                appendTreeNode(treeData, {
                    nodeId: tmpId,
                    parentId: node.nodeId,
                    children: [],
                    isNew: true,
                    bindData: {
                        id: tmpId,
                        name: `新建类目${node.children.length}`,
                        type: 'folder',
                        level: node.bindData.level + 1,
                        parentId: node.nodeId
                    }
                }, node)
                self.setState({
                    treeData
                })
                break;
            }
            case 'add': {
                API.addDataCatalogue({
                    nodeName: node.nodeName,
                    nodePid: node.nodePid
                }).then(res => {
                    const source = { nodeId: node.tmpId }
                    if (res.code === 1) {
                        message.success('数据类目增加成功！')
                        replaceTreeNode(treeData, source, res.data)
                    } else {
                        removeTreeNode(treeData, source)
                    }
                    self.setState({
                        treeData
                    })
                    if (callback) callback(res)
                })
                break;
            }
            case 'delete': {
                const succCall = res => {
                    if (res.code === 1) {
                        message.success('数据类目删除成功！')
                        removeTreeNode(treeData, node)
                        self.setState({
                            treeData
                        })
                    }
                }
                if (node.bindData.type !== 'table') {
                    API.delDataCatalogue({ id: node.nodeId }).then(succCall)
                } else {
                    API.delTableInCatalogue({
                        id: node.bindData.id
                    }).then(succCall)
                }
                break;
            }
            case 'edit': {
                API.updateDataCatalogue(node).then((res) => {
                    if (res.code === 1) {
                        const source = { nodeId: node.id }

                        replaceTreeNode(treeData, source, res.data)
                        message.success('数据类目更新成功！')
                    }
                    self.setState({
                        treeData
                    })
                    if (callback) callback(res)
                })
                break;
            }
            default: break;
        }
    }

    onSelect = (value, target) => {
        const item = target.node.props.data
        if (item.bindData.type === 'table') {
            hashHistory.push(`/data-manage/table/view/${item.bindData.id}`)
        }
    }

    render () {
        return (
            <div>
                <h1 className="box-title">
                    数据类目
                    <span className="box-sub-title">&nbsp;
                        构建数据类目体系，便于数据维护
                        数据类目体系是树形结构的，
                        每张表可以关联到唯一的一个叶子节点，
                        便于您进行数据检索与数据维护。
                    </span>
                </h1>
                <div className="box-2">
                    <CatalogueTree
                        treeData={this.state.treeData}
                        onTreeNodeEdit={this.onTreeNodeEdit}
                        onSelect={this.onSelect}
                    />
                </div>
            </div>
        )
    }
}

export default DataCatalogue
