import * as React from 'react';
import { Modal, Tree, Input, message as Message } from 'antd';
import { API } from '../../../../../api/apiMap';
import './style.scss';

const { TreeNode } = Tree;
const { Search } = Input;
export interface IProps {
    title?: string;
    visible: boolean;
    handleOk: () => void;
    handleCancel: () => void;
    id: string|number;
    entityId?: string | number;
}

interface IState {
    searchValue: string;
    expandedKeys: any[];
    autoExpandParent: boolean;
    dataList: any[];
    data: any[];
    selectedKeys: any[];
}
class MoveTreeNode extends React.PureComponent<IProps, IState> {
    state: IState = {
        searchValue: '',
        expandedKeys: [],
        autoExpandParent: true,
        dataList: [],
        data: [],
        selectedKeys: []
    };
    static defaultProps = {
        title: '移动标签'
    }
    componentDidMount () {
        this.getTagCate();
    }
    getTagCate = () => { // 查询标签层级目录
        const { entityId } = this.props;
        API.getTagCate({
            entityId
        }).then(res => { // 获取主键列表
            const { code, data } = res;
            if (code == 1) {
                let dataList = [];
                const generateList = data => {
                    for (let i = 0; i < data.length; i++) {
                        const node = data[i];
                        const { tagCateId, cateName } = node;
                        dataList.push({ tagCateId, cateName });
                        if (node.children) {
                            generateList(node.children);
                        }
                    }
                };
                generateList(data);
                this.setState({
                    data,
                    dataList
                });
            }
        })
    }
    getParentKey = (key, tree) => {
        let parentKey;
        for (let i = 0; i < tree.length; i++) {
            const node = tree[i];
            if (node.children) {
                if (node.children.some(item => item.tagCateId === key)) {
                    parentKey = node.tagCateId;
                } else if (this.getParentKey(key, node.children)) {
                    parentKey = this.getParentKey(key, node.children);
                }
            }
        }
        return parentKey;
    };
    onExpand = (expandedKeys: any) => {
        this.setState({
            expandedKeys,
            autoExpandParent: false
        });
    };
    onChange = (e: any) => {
        const { value } = e.target;
        const { dataList, data } = this.state;
        const expandedKeys = dataList
            .map(item => {
                if (item.cateName.indexOf(value) > -1) {
                    return this.getParentKey(item.tagCateId, data);
                }
                return null;
            })
            .filter((item, i, self) => item && self.indexOf(item) === i);
        this.setState({
            expandedKeys,
            searchValue: value,
            autoExpandParent: true
        });
    };
    onSelect = (selectedKeys) => {
        this.setState({
            selectedKeys
        })
    }
    moveTag = (targetTagCateId) => { // 标签引擎-新增/重命名标签层级
        const { entityId, id } = this.props;
        API.moveTag({
            entityId,
            targetTagCateId: targetTagCateId,
            tagId: id
        }).then(res => {
            const { code } = res;
            if (code == 1) {
                Message.success('移动成功！');
                this.props.handleOk();
                this.resetData();
            }
        })
    }
    handleCancel = () => {
        this.props.handleCancel();
        this.resetData();
    };
    handleOk = () => {
        const { selectedKeys } = this.state;
        this.moveTag(selectedKeys[0]);
        this.props.handleOk();
    };
    resetData = () => {
        this.setState({
            searchValue: '',
            expandedKeys: [],
            autoExpandParent: true,
            dataList: [],
            data: []
        })
    }
    renderNode = (data: any[]) => {
        const { searchValue } = this.state;
        const { id } = this.props;
        return data.map((item: any) => {
            const index = item.cateName.indexOf(searchValue);
            const beforeStr = item.cateName.substr(0, index);
            const afterStr = item.cateName.substr(index + searchValue.length);
            const title =
                index > -1 ? (
                    <span>
                        {beforeStr}
                        <span style={{ color: '#f50' }}>{searchValue}</span>
                        {afterStr}
                    </span>
                ) : (
                    <span>{item.cateName}</span>
                );
            if (item.children && item.children.length) {
                return (
                    <TreeNode disabled={item.canMove || id == item.tagCateId} key={!item.tagCateId} title={title}>
                        {
                            this.renderNode(item.children)
                        }
                    </TreeNode>
                );
            }
            return <TreeNode key={item.tagCateId} disabled={!item.canMove || id == item.tagCateId} title={title} />;
        });
    }
    render () {
        const { searchValue, expandedKeys, autoExpandParent, data, selectedKeys} = this.state;
        const { visible, title } = this.props;
        return (
            <Modal
                visible={visible}
                title={title}
                onOk={this.handleOk}
                onCancel={this.handleCancel}
            >
                <div>
                    <Search style={{ marginBottom: 8 }} value={searchValue} placeholder="搜索目录" onChange={this.onChange} />
                    <Tree
                        className="draggable-tree"
                        onExpand={this.onExpand}
                        selectedKeys={selectedKeys}
                        expandedKeys={expandedKeys}
                        onSelect={this.onSelect}
                        autoExpandParent={autoExpandParent}
                    >
                        {
                            this.renderNode(data)
                        }
                    </Tree>
                </div>
            </Modal>
        );
    }
}
export default MoveTreeNode;
