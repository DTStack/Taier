import React from 'react';
import { Table, Dropdown, Menu, Input, Icon } from 'antd';

import ModelDetailModal from './detailModal';

class ModelView extends React.Component {
    state = {
        detailModalVisible: false,
        detailData: null
    }
    initColumns () {
        return [{
            title: '模型名称',
            dataIndex: 'modelName',
            width: '150px'
        }, {
            title: '算法名称',
            dataIndex: 'codeName',
            width: '150px'
        }, {
            title: '当前版本',
            dataIndex: 'version'
        }, {
            title: '运行状态',
            dataIndex: 'status'
        }, {
            title: '占用内存(MB)',
            dataIndex: 'memory'
        }, {
            title: '更新时间',
            dataIndex: 'updateDate',
            width: '150px'
        }, {
            title: '模型操作',
            dataIndex: 'deal',
            width: '240px',
            render: (t, record) => {
                return <React.Fragment>
                    <a onClick={this.showDetailModal.bind(this, record)}>模型属性</a>
                    <span className="ant-divider" />
                    <a>更新模型</a>
                    <span className="ant-divider" />
                    <a>切换版本</a>
                    <span className="ant-divider" />
                    <Dropdown
                        overlay={(
                            <Menu>
                                <Menu.Item>
                                    <a>禁用</a>
                                    <a>重试</a>
                                </Menu.Item>
                            </Menu>
                        )}
                    >
                        <a>更多 <Icon type="down" /></a>
                    </Dropdown>
                </React.Fragment>
            }
        }]
    }
    showDetailModal = (data) => {
        this.setState({
            detailModalVisible: true,
            detailData: data
        })
    }
    render () {
        const { detailModalVisible, detailData } = this.state;
        return (
            <div className='c-model-view'>
                <header className='c-model-view__header'>已部署模型</header>
                <div className='c-model-view__table__header'>
                    <Input.Search style={{ width: '267px' }} placeholder='按模型名称搜索' />
                </div>
                <Table
                    className="m-table border-table"
                    columns={this.initColumns()}
                    dataSource={[{}]}
                />
                <ModelDetailModal
                    visible={detailModalVisible}
                    key={detailData && detailData.id}
                    data={detailData}
                    onCancel={() => {
                        this.setState({
                            detailModalVisible: false,
                            detailData: null
                        })
                    }}
                />
            </div>
        )
    }
}
export default ModelView;
