import * as React from 'react';
import { Modal, message } from 'antd';
import Api from '../../api/console';

class KillAllTask extends React.Component<any, any> {
    state: any = {
        confirmLoading: false
    }
    // 请求杀任务接口
    killTask () {
        const { killResource = [] } = this.props;
        let params = this.initParams();
        Api.killAllTask(params).then((res: any) => {
            if (res.code == 1) {
                const delayTime = params.totalModel !== undefined ? 1000 : 0;
                // 杀死全部任务为异步有延迟，需要延迟执行刷新数据操作
                setTimeout(() => {
                    message.success('操作成功');
                    this.props.killSuccess(killResource);
                    this.props.autoRefresh();
                    this.props.onCancel();
                    this.setState({
                        confirmLoading: false
                    })
                }, delayTime)
            } else {
                this.setState({
                    confirmLoading: false
                })
            }
        })
    }
    initParams () {
        const { killResource = [], node, totalModel, totalSize, engineType, groupName, jobName, computeType, clusterName } = this.props;
        let params: any;
        if (totalModel !== undefined) {
            // 杀死全部任务
            params = {
                totalModel,
                node,
                totalSize
            };
            if (totalModel === 0) {
                // 按照group筛选杀死全部任务
                params = {
                    ...params,
                    engineType,
                    groupName,
                    clusterName
                };
            } else {
                // 按照任务筛选杀死全部任务
                params = {
                    ...params,
                    jobName,
                    computeType
                };
            }
        } else {
            // 杀死选中的任务
            params = {
                jobIdList: killResource,
                node
            };
        }
        console.log('params:', params)
        return params;
    }

    confirmKilltask () {
        this.setState({
            confirmLoading: true
        })
        this.killTask();
    }
    render () {
        const { totalModel } = this.props;
        const title = totalModel !== undefined ? `杀死全部任务` : `杀死选中任务`;
        const htmlText = totalModel !== undefined
            ? <p style={{ color: 'red' }}>本操作将杀死列表（跨分页）中的全部任务，不仅是当前页</p>
            : <p style={{ color: 'red' }}>本操作将杀死列表（非跨分页）中的选中任务</p>;
        return (
            <Modal
                title={title}
                visible={this.props.visible}
                okText={title}
                okType="danger"
                confirmLoading={this.state.confirmLoading}
                onCancel={this.props.onCancel}
                onOk={this.confirmKilltask.bind(this)}
            >
                {htmlText}
            </Modal>
        )
    }
}
export default KillAllTask;
