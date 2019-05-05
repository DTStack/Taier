import React from 'react';

import { Modal, Table, Popconfirm, message } from 'antd';
import HelpDoc from '../../../../components/helpDoc';

import api from '../../../../api/model'
import utils from 'utils';

class ModelUpdateModal extends React.Component {
    state = {
        updateList: [],
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
    async loadParams (record) {
        const { data } = this.props;
        const res = await api.loadModel({
            id: data.id,
            modelId: record.id
        });
        if (res && res.code == 1) {
            message.success('操作成功');
            this.loadData();
        }
    }
    initColumns () {
        return [{
            title: '运行时间',
            dataIndex: 'runTime',
            width: '150px',
            render (t) {
                return utils.formatDateTime(t);
            }
        }, {
            title: <span>模型数据存储路径 <HelpDoc className='u-helpdox--table' doc='modelSavePath' /></span>,
            dataIndex: 'savePath'
        }, {
            title: <span>操作 <HelpDoc className='u-helpdox--table' doc='updateModelDeal' /></span>,
            dataIndex: 'deal',
            width: '100px',
            render: (t, record) => {
                if (record.id == this.props.data.id) {
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
                    className='m-table'
                    columns={this.initColumns()}
                    dataSource={updateList}
                    loading={loading}
                    onChange={this.onTableChange}
                    scroll={{ y: '550px' }}
                    pagination={false}
                />
            </Modal>
        )
    }
}
export default ModelUpdateModal;
