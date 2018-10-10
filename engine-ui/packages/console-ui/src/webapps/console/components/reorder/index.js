/*
* @Author: 12574
* @Date:   2018-09-19 19:24:01
* @Last Modified by:   12574
* @Last Modified time: 2018-09-30 17:15:36
*/

// 顺序调整
import React, { Component } from 'react';
import { Modal, Input, Form, message } from "antd";
import { formItemLayout } from "../../consts";
import Api from "../../api/console";
class Reorder extends Component {
    state = {
        jobIndex: undefined
    }
    // 请求顺序调整接口
    changeJobPriority() {
        const { priorityResource } = this.props;
        const { jobIndex } = this.state;
        // 获取集群
        var groupName, clusterName, computeTypeInt;
        const arr = (priorityResource.groupName || "").split("_");
        if (arr.length == 1) {
            clusterName = priorityResource.groupName
        } else {
            for (var i = 0; i <= arr.length; i++) {
                clusterName = arr[0];
                groupName = arr[1];
            }
        }

        Api.changeJobPriority({
            engineType: priorityResource.engineType,
            groupName: priorityResource.groupName,
            jobId: priorityResource.taskId,
            jobIndex: jobIndex
        }).then((res) => {
            if (res.code == 1) {
                message.success("修改成功");
                this.props.autoRefresh();
                this.props.onCancel();
            }
        })
    }
    changeJobIndex(e) {
        this.setState({
            jobIndex: e.target.value
        })
    }
    confirmChangeJobPriority() {
        this.changeJobPriority();

    }
    render() {
        return (
            <Modal
                title="执行顺序"
                visible={this.props.visible}
                onCancel={this.props.onCancel}
                onOk={this.confirmChangeJobPriority.bind(this)}
            >
                <Form.Item
                    label="执行顺序"
                    {...formItemLayout}
                >
                    <Input
                        style={{ width: "100%" }}
                        placeholder="不超过当前列表中的任务数"
                        onChange={this.changeJobIndex.bind(this)}
                        value={this.state.jobIndex}
                    />
                </Form.Item>
            </Modal>
        )
    }
}
export default Reorder;