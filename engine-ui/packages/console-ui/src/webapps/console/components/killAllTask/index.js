import React, { Component } from 'react';
import { Modal, message } from 'antd';
import Api from '../../api/console';

class killAllTask extends Component {
    // 请求杀任务接口
    killTask () {
        const { killResource } = this.props;
        // console.log(killResource.jobName);
        // 获取集群
        var queueName, clusterName, computeTypeInt;
        const arr = killResource.groupName.split('_');
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
        Api.killTask({
            computeTypeInt: computeTypeInt,
            engineType: killResource.engineType,
            jobId: killResource.taskId,
            queueName: queueName,
            node: this.props.node,
            clusterName: clusterName
        }).then((res) => {
            if (res.code == 1) {
                this.props.killSuccess(killResource.taskId);
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
        this.killTask();
    }
    render () {
        return (
            <Modal
                title="杀死全部任务"
                visible={this.props.visible}
                okText="杀死全部任务"
                okType="danger"
                onCancel={this.props.onCancel}
                onOk={this.confirmKilltask.bind(this)}
            >
                <p style={{ color: 'red' }}>本操作将杀死列表（跨分页）中的全部任务，不仅是当前页</p>
            </Modal>
        )
    }
}
export default killAllTask;
