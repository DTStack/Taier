import * as React from 'react';

import { Modal, Table, Popconfirm, message } from 'antd';
import HelpDoc from '../../../../components/helpDoc';

import api from '../../../../api/model'
import utils from 'utils';
import { MODEL_STATUS } from '../../../../consts';

class ModelUpdateModal extends React.Component<any, {
    updateList: any[],
    loading: Boolean
}> {
    state = {
        updateList: [] as any[],
        loading: false
    }
    componentDidMount () {
        this.loadData();
    }
    loadData = async () => {
        const { data } = this.props;
        if (!data) {
            return;
        }
        this.setState({
            loading: true
        })
        let res = await api.getModelParamsList({
            modelId: data.id
        });
        if (res && res.code == 1) {
            this.setState({
                updateList: res.data
            })
        }
        this.setState({
            loading: false
        })
    }
    async loadParams (record: any) {
        const { data } = this.props;
        const res = await api.loadModel({
            modelId: data.id,
            modelFile: record.modelFile
        });
        if (res && res.code == 1) {
            message.success('操作成功');
            this.loadData();
        }
    }
    initColumns () {
        return [{
            title: '运行时间',
            dataIndex: 'time',
            width: '150px',
            render (t: any) {
                return utils.formatDateTime(t);
            }
        }, {
            title: <span>模型数据存储路径 <HelpDoc className='u-helpdox--table' doc='modelSavePath' /></span>,
            dataIndex: 'modelFile'
        }, {
            title: <span>操作 <HelpDoc className='u-helpdox--table' doc='updateModelDeal' /></span>,
            dataIndex: 'deal',
            width: '100px',
            render: (t: any, record: any) => {
                if (record.status == MODEL_STATUS.RUNNING.value) {
                    return 'LOADED';
                }
                return <Popconfirm title="确定加载此组参数进行模型使用?" onConfirm={() => {
                    this.loadParams(record)
                }} okText="确定" cancelText="取消">
                    <a>LOAD</a>
                </Popconfirm>
            }
        }]
    }
    render () {
        const { updateList, loading } = this.state;
        const { visible, onCancel } = this.props;
        return (
            <Modal
                width={700}
                visible={visible}
                onCancel={onCancel}
                footer={null}
                title='更新模型'
            >
                <Table
                    className='m-table border-table'
                    columns={this.initColumns()}
                    dataSource={updateList}
                    loading={loading}
                    scroll={{ y: 550 }}
                    pagination={false}
                    {...{
                        rowkey: 'time'
                    }}
                />
            </Modal>
        )
    }
}
export default ModelUpdateModal;
