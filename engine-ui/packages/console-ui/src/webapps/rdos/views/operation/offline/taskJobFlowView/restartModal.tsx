import moment from 'moment'
import * as React from 'react'

import {
    Modal, Row, message, Col, Tree, Checkbox
} from 'antd'

import Api from '../../../../api'
import { TaskType, TaskStatus } from '../../../../components/status'
import { TASK_STATUS } from '../../../../comm/const'
import utils from '../../../../../../utils'

const TreeNode = Tree.TreeNode

class RestartModal extends React.Component<any, any> {
    state: any = {
        treeData: [],
        expandedKeys: [],
        currentNode: '',
        checkedKeys: [],
        isAllChecked: false
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const node = nextProps.restartNode
        const visible = nextProps.visible
        if (visible && node) {
            this.setState({
                currentNode: node,
                checkedKeys: [],
                treeData: [node]
            }, () => {
                this.loadTaskTree({
                    taskId: node.batchTask.id,
                    jobKey: node.jobKey,
                    isOnlyNextChild: false
                })
                // this.cacheAllSelected = this.getAllSelectData(this.state.treeData); // 获取新的全部数据作为全选缓存
            })
        }
    }

    restartChildNodes = () => {
        const { restartNode } = this.props
        const checked = this.state.checkedKeys

        if (checked.length === 0) {
            message.error('请选择要重跑的任务!')
            return;
        }

        const reqParams: any = {
            jobId: restartNode.id,
            subJobIds: checked,
            justRunChild: true,
            setSuccess: false
        }

        Api.restartAndResume(reqParams).then((res: any) => {
            if (res.code === 1) {
                message.success('已经成功开始重跑!')
                this.onCancelModal();
            }
        })
    }

    loadTaskTree = (params: any) => {
        const ctx = this
        const parent = this.state.currentNode
        Api.getRestartJobs(params).then((res: any) => {
            if (res.code === 1) {
                const children = res.data || []
                if (children.length > 0) {
                    ctx.insertChildren(parent, children)
                }
            }
        })
    }

    insertChildren = (node: any, children: any) => {
        const treeData = Object.assign(this.state.treeData)

        const loop = (data: any) => {
            for (let i = 0; i < data.length; i++) {
                if (data[i].id === node.id) {
                    data[i].childs = children;
                    break;
                }
                if (data[i].childs) {
                    loop(data[i].childs)
                }
            }
        }
        loop(treeData)

        this.setState({ treeData })
    }

    onCancelModal = () => {
        this.setState({
            checkedKeys: [],
            isAllChecked: false
        })
        this.props.onCancel();
        this.cacheAllSelected = []
        console.log('unmount', this.cacheAllSelected)
    }

    onCheck = (checkedKeys: any, info: any) => {
        // const allSelectedData = this.getAllSelectData(this.state.treeData);
        const allSelectedData = this.cacheAllSelected; // 获取全选的缓存
        const hasCheckedVal = checkedKeys.checked;
        this.setState({
            checkedKeys: hasCheckedVal,
            isAllChecked: utils.isEqualArr(allSelectedData, hasCheckedVal)
        })
    }

    disabledDate = (current: any) => {
        return current && current.valueOf() > moment().subtract(1, 'days').valueOf();
    }

    asyncTree = (treeNode: any) => {
        const ctx = this
        const node = treeNode.props.data
        return new Promise((resolve: any) => {
            if (!node.childs || node.childs.length === 0) {
                ctx.loadTaskTree({
                    taskId: node.batchTask.id,
                    jobKey: node.jobKey,
                    isOnlyNextChild: true
                })
            }
            resolve();
        })
    }

    // 获取所有项的id值
    getAllSelectData = (data?: any[]) => {
        const tempArr: any[] = [];
        const _this = this;
        function getId (data: any) {
            if (!data) return;
            if (data.length > 0) {
                data.map((item: any) => {
                    console.log(item)
                    const id = `${item.batchTask ? item.id : item.jobId}`;
                    const status = item.jobStatus || item.status; // jobStatus 为从接口获取，status表默认节点
                    // 禁止重跑并恢复调度
                    if (item.childs) {
                        const canRestart = _this.canRestartFunc(status);
                        if (canRestart) {
                            tempArr.push(id);
                        }
                        getId(item.childs);
                    } else {
                        tempArr.push(id);
                    }
                });
            }
        }
        getId(data);
        console.log(tempArr)
        return tempArr;
    }

    cacheAllSelected: any[] = [];

    handleAllSelect = (data: any[]) => {
        console.log(this.cacheAllSelected)
        if (this.cacheAllSelected.length === 0) {
            this.cacheAllSelected = this.getAllSelectData(data);
        }
        // this.cacheAllSelected.length === 0 && (this.cacheAllSelected = this.getAllSelectData(data));
        // 取消全选置空， 不能使用全选的缓存数据
        console.log(this.cacheAllSelected, data)
        this.setState({ checkedKeys: data.length === 0 ? [] : this.cacheAllSelected });
    }

    handleCheckboxChange = () => {
        const { treeData, isAllChecked } = this.state;
        this.setState({
            isAllChecked: !isAllChecked
        }, () => {
            // 根据是否点击全选来决定传递处理参数
            this.handleAllSelect(this.state.isAllChecked ? treeData : [])
        });
    }

    canRestartFunc = (status: any): boolean => {
        switch (status) {
            case TASK_STATUS.WAIT_SUBMIT: // 未运行
            case TASK_STATUS.FINISHED: // 已完成
            case TASK_STATUS.RUN_FAILED: // 运行失败
            case TASK_STATUS.SUBMIT_FAILED: // 提交失败
            case TASK_STATUS.SET_SUCCESS: // 手动设置成功
            case TASK_STATUS.PARENT_FAILD: // 上游失败
            case TASK_STATUS.KILLED: // 已停止
            case TASK_STATUS.STOPED: // 已取消
                return true
            default:
                return false;
        }
    }

    getTreeNodes = (data: any[], currentNode: any) => {
        if (data && data.length > 0) {
            const nodes = data.map((item: any) => {
                const id = `${item.batchTask ? item.id : item.jobId}`;

                const name = item.taskName || (item.batchTask && item.batchTask.name);
                const status = item.jobStatus || item.status; // jobStatus 为从接口获取，status表默认节点
                const taskType = item.taskType || (item.batchTask && item.batchTask.taskType);
                const titleFix = { title: name };
                // 禁止重跑并恢复调度
                const canRestart = this.canRestartFunc(status);

                const content = <Row>
                    <Col span={6} className="ellipsis" {...titleFix}>{name}</Col>
                    <Col span={8}>{item.cycTime}</Col>
                    <Col span={4}><TaskStatus value={status} /></Col>
                    <Col span={6}><TaskType value={taskType} /></Col>
                </Row>

                if (item.childs) {
                    return (<TreeNode
                        data={item}
                        disableCheckbox={!canRestart}
                        value={id}
                        title={content}
                        key={id}>
                        {this.getTreeNodes(item.childs, currentNode)}
                    </TreeNode>);
                }
                return (<TreeNode
                    data={item}
                    name={name}
                    value={id}
                    title={content}
                    key={id}
                />)
            });
            return nodes;
        }
        return []
    }

    componentWillUnmount () {
        this.cacheAllSelected = []
        console.log('unmount', this.cacheAllSelected)
    }

    render () {
        const { visible, restartNode } = this.props
        const { treeData, isAllChecked, checkedKeys } = this.state
        const treeNodes = this.getTreeNodes(treeData, restartNode)

        return (
            <Modal
                title="重跑下游并恢复调度"
                okText="确认执行"
                visible={visible}
                onOk={this.restartChildNodes}
                onCancel={this.onCancelModal}
                maskClosable={true}
            >
                <Row>
                    <Col span={12}>请选择要重跑的任务: &nbsp;&nbsp;
                        <Checkbox onChange={this.handleCheckboxChange} checked={isAllChecked}>全选</Checkbox>
                    </Col>
                    <Col span={12} className="txt-right">业务日期：{restartNode ? restartNode.businessDate : ''}</Col>
                </Row>
                <Row className="section patch-data">
                    <Row className="patch-header">
                        <Col span={8}>任务名称</Col>
                        <Col span={6}>执行时间</Col>
                        <Col span={4}>任务状态</Col>
                        <Col span={6}>任务类型</Col>
                    </Row>
                    <Tree
                        checkable
                        checkStrictly
                        onCheck={this.onCheck}
                        checkedKeys={checkedKeys}
                        loadData={this.asyncTree}
                    >
                        {treeNodes}
                    </Tree>
                </Row>
            </Modal>
        )
    }
}
export default RestartModal
