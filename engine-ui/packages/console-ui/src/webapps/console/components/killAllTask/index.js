import React, { Component } from 'react';
import { Modal, message } from 'antd';
import Api from '../../api/console';

class killAllTask extends Component {
    // 请求杀任务接口
    killTask () {
        const { killResource = [], total, queueSize, groupName } = this.props;
        // 获取集群
        let queueName, clusterName, computeTypeInt;
        const arr = killResource.groupName && killResource.groupName.split('_');
        const jobIdList = killResource.map(o => o.taskId);
        if (arr.length == 1) {
            clusterName = killResource.groupName
        } else {
            for (var i = 0; i <= arr.length; i++) {
                clusterName = arr[0];
                queueName = arr[1];
            }
        }

        if (killResource.computeType == 'BATCH') {
            computeTypeInt = 1
        } else {
            computeTypeInt = 0
        }
        Api.killAllTask({
            computeTypeInt: computeTypeInt,
            engineType: killResource.engineType,
            jobIdList,
            queueName: queueName,
            clusterName: clusterName,
            total, // 是否全部杀死
            queueSize,
            groupName
        }).then((res) => {
            if (res.code == 1) {
                this.props.killSuccess(jobIdList);
                message.success('操作成功');
                this.props.autoRefresh();
                // 异步,成功之后才能关闭
                this.props.onCancel();
            } else {
                message.success('操作失败');
            }
        })
    }
    confirmKilltask () {
        const { killResource } = this.props;
        if (!killResource || killResource.length <= 0) {
            message.error('当前列表为空');
            return false;
        }
        this.killTask();
    }
    render () {
        const { total } = this.props;
        const title = total ? `杀死全部任务` : `杀死选中任务`;
        const htmlText = total
            ? <p style={{ color: 'red' }}>本操作将杀死列表（跨分页）中的全部任务，不仅是当前页</p>
            : <p style={{ color: 'red' }}>本操作将杀死列表（非跨分页）中的选中任务</p>;
        return (
            <Modal
                title={title}
                visible={this.props.visible}
                okText={title}
                okType="danger"
                onCancel={this.props.onCancel}
                onOk={this.confirmKilltask.bind(this)}
            >
                {htmlText}
            </Modal>
        )
    }
}
export default killAllTask;
