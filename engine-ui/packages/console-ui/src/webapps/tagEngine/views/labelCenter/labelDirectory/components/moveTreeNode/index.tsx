import * as React from 'react';
import { Modal, Tree, Input } from 'antd';
import './style.scss';

const { TreeNode } = Tree;
const { Search } = Input;
export interface IProps {
    title?: string;
    visible: boolean;
    handleOk: () => void;
    handleCancel: () => void;
    id?: string | number;
}

interface IState {
    searchValue: string;
    expandedKeys: any[];
    autoExpandParent: boolean;
}
const x = 3;
const y = 2;
const z = 1;
const gData: any[] = [];

const generateData = (_level, _preKey, _tns) => {
    const preKey = _preKey || '0';
    const tns = _tns || gData;

    const children = [];
    for (let i = 0; i < x; i++) {
        const key = `${preKey}-${i}`;
        tns.push({ title: key, key });
        if (i < y) {
            children.push(key);
        }
    }
    if (_level < 0) {
        return tns;
    }
    const level = _level - 1;
    children.forEach((key, index) => {
        tns[index].children = [];
        return generateData(level, key, tns[index].children);
    });
};
generateData(z);

const dataList = [];
const generateList = data => {
    for (let i = 0; i < data.length; i++) {
        const node = data[i];
        const { key } = node;
        dataList.push({ key, title: key });
        if (node.children) {
            generateList(node.children);
        }
    }
};
generateList(gData);

const getParentKey = (key, tree) => {
    let parentKey;
    for (let i = 0; i < tree.length; i++) {
        const node = tree[i];
        if (node.children) {
            if (node.children.some(item => item.key === key)) {
                parentKey = node.key;
            } else if (getParentKey(key, node.children)) {
                parentKey = getParentKey(key, node.children);
            }
        }
    }
    return parentKey;
};

class MoveTreeNode extends React.PureComponent<IProps, IState> {
    state: IState = {
        searchValue: '',
        expandedKeys: [],
        autoExpandParent: true
    };
    static defaultProps = {
        title: '选择目录'
    }
    onExpand = (expandedKeys: any) => {
        this.setState({
            expandedKeys,
            autoExpandParent: false
        });
    };
    onChange = (e: any) => {
        const { value } = e.target;
        const expandedKeys = dataList
            .map(item => {
                if (item.title.indexOf(value) > -1) {
                    return getParentKey(item.key, gData);
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
    onDragEnter = (info: any) => {
        console.log(info);
        // expandedKeys 需要受控时设置
        // this.setState({
        //   expandedKeys: info.expandedKeys,
        // });
    }
    onDrop = (info: any) => {
        const dropKey = info.node.props.eventKey;
        const dragKey = info.dragNode.props.eventKey;
        const dropPos = info.node.props.pos.split('-');
        const dropPosition = info.dropPosition - Number(dropPos[dropPos.length - 1]);
        // const dragNodesKeys = info.dragNodesKeys;
        const loop = (data, key, callback) => {
            data.forEach((item, index, arr) => {
                if (item.key === key) {
                    return callback(item, index, arr);
                }
                if (item.children) {
                    return loop(item.children, key, callback);
                }
            });
        };
        const data = [...this.state.gData];
        let dragObj;
        loop(data, dragKey, (item, index, arr) => {
            arr.splice(index, 1);
            dragObj = item;
        });
        if (info.dropToGap) {
            let ar;
            let i;
            loop(data, dropKey, (item, index, arr) => {
                ar = arr;
                i = index;
            });
            if (dropPosition === -1) {
                ar.splice(i, 0, dragObj);
            } else {
                // drag node and drop node in the same level
                // and drop to the last node
                if (dragKey.length === dropKey.length && ar.length - 1 === i) {
                    i += 2;
                }
                ar.splice(i - 1, 0, dragObj);
            }
        } else {
            loop(data, dropKey, (item) => {
                item.children = item.children || [];
                // where to insert 示例添加到尾部，可以是随意位置
                item.children.push(dragObj);
            });
        }
        this.setState({
            gData: data
        });
    }
    handleCancel = () => {
        this.props.handleCancel();
    };
    handleOk = () => {
        this.props.handleOk();
    };
    renderNode = (data: any[]) => {
        const { searchValue } = this.state;
        return data.map((item: any) => {
            const index = item.title.indexOf(searchValue);
            const beforeStr = item.title.substr(0, index);
            const afterStr = item.title.substr(index + searchValue.length);
            const title =
                index > -1 ? (
                    <span>
                        {beforeStr}
                        <span style={{ color: '#f50' }}>{searchValue}</span>
                        {afterStr}
                    </span>
                ) : (
                    <span>{item.title}</span>
                );
            if (item.children && item.children.length) {
                return (
                    <TreeNode key={item.key} title={title}>
                        {
                            this.renderNode(item.children)
                        }
                    </TreeNode>
                );
            }
            return <TreeNode key={item.key} title={title} />;
        });
    }
    render () {
        const { searchValue, expandedKeys, autoExpandParent } = this.state;
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
                        expandedKeys={expandedKeys}
                        autoExpandParent={autoExpandParent}
                        draggable
                        onDragEnter={this.onDragEnter}
                        onDrop={this.onDrop}
                    >
                        {
                            this.renderNode(gData)
                        }
                    </Tree>
                </div>
            </Modal>
        );
    }
}
export default MoveTreeNode;
