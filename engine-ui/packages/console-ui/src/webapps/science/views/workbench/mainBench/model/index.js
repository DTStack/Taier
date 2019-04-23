import React from 'react';
import { Table, Dropdown, Menu, Input, Icon, Tag } from 'antd';

import ModelDetailModal from './detailModal';
import AlgorithmModal from './algorithmModal';
import UpdateModal from './updateModal';
import SelectVersionsModal from './selectVersionModal';

import api from '../../../../api/model'
import { MODEL_STATUS } from '../../../../consts'
import utils from 'utils';
import { generateValueDic } from 'funcs';

class ModelView extends React.Component {
    state = {
        detailModalVisible: false,
        detailData: null,
        algorithmModalVisible: false,
        algorithmData: null,
        updateModalVisible: false,
        updateData: null,
        selectVersionModalVisible: false,
        versionData: null,
        searchName: null,
        modelList: [],
        loading: false,
        table: {
            filters: null,
            sorter: null
        },
        pagination: {
            total: 0,
            pageSize: 10,
            current: 1
        }
    }
    componentDidMount () {
        this.loadData();
    }
    loadData = async () => {
        this.setState({
            loading: true
        })
        const { pagination, searchName } = this.state;
        let res = await api.getModelList({
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            searchName: searchName
        });
        if (res && res.code == 1) {
            this.setState({
                modelList: res.data.data,
                pagination: {
                    ...pagination,
                    total: res.data.totalCount
                }
            })
        }
        this.setState({
            loading: false
        })
    }
    showAlgorithm = (data) => {
        this.setState({
            algorithmData: data,
            algorithmModalVisible: true
        })
    }
    showDetailModal = (data) => {
        this.setState({
            detailModalVisible: true,
            detailData: data
        })
    }
    showUpdateModal = (data) => {
        this.setState({
            updateModalVisible: true,
            updateData: data
        })
    }
    showSelectVersionModal = (data) => {
        this.setState({
            selectVersionModalVisible: true,
            versionData: data
        })
    }
    disableModel = (data) => {
        console.log(data)
    }
    enableModel = (data) => {
        console.log(data)
    }
    deleteModel = (data) => {
        console.log(data)
    }
    initColumns () {
        const dic = generateValueDic(MODEL_STATUS);
        return [{
            title: '模型名称',
            dataIndex: 'modelName',
            width: '150px'
        }, {
            title: '算法名称',
            dataIndex: 'codeName',
            width: '150px',
            filters: [{
                text: '算法1',
                value: 0
            }],
            render: (codeName) => {
                return codeName.map((m) => {
                    return <Tag onClick={this.showAlgorithm.bind(null, m)} className='u-table__tag__margin' key={m.name} color='pink'>{m.name}</Tag>;
                })
            }
        }, {
            title: '当前版本',
            dataIndex: 'version'
        }, {
            title: '运行状态',
            dataIndex: 'status',
            filters: Object.entries(dic).map(([key, value]) => {
                return {
                    text: value.text,
                    value: key
                }
            }),
            render (t) {
                return <span className={dic[t].className}>{dic[t].text}</span>;
            }
        }, {
            title: '占用内存(MB)',
            dataIndex: 'memory',
            sorter: true
        }, {
            title: '更新时间',
            dataIndex: 'updateDate',
            width: '150px',
            sorter: true,
            render (t) {
                return utils.formatDateTime(t);
            }
        }, {
            title: '模型操作',
            dataIndex: 'deal',
            width: '240px',
            render: (t, record) => {
                const menuItems = [];
                const disableItem = (
                    <Menu.Item key='disabled'>
                        <a onClick={this.disableModel.bind(null, record)}>禁用</a>
                    </Menu.Item>
                );
                const runItem = (
                    <Menu.Item key='running'>
                        <a onClick={this.enableModel.bind(null, record)}>启用</a>
                    </Menu.Item>
                );
                const deleteItem = (
                    <Menu.Item key='delete'>
                        <a onClick={this.deleteModel.bind(null, record)}>删除</a>
                    </Menu.Item>
                );
                switch (record.status) {
                    case MODEL_STATUS.RUNNING.value: {
                        menuItems.push(disableItem);
                        break;
                    }
                    case MODEL_STATUS.FAILED.value: {
                        menuItems.push(disableItem)
                        break;
                    }
                    case MODEL_STATUS.DISABLED.value: {
                        menuItems.push(runItem)
                        menuItems.push(deleteItem)
                        break;
                    }
                }
                return <React.Fragment>
                    <a onClick={this.showDetailModal.bind(this, record)}>模型属性</a>
                    <span className="ant-divider" />
                    <a onClick={this.showUpdateModal.bind(this, record)}>更新模型</a>
                    <span className="ant-divider" />
                    <a onClick={this.showSelectVersionModal.bind(this, record)}>切换版本</a>
                    <span className="ant-divider" />
                    <Dropdown
                        overlay={(
                            <Menu>
                                {menuItems}
                            </Menu>
                        )}
                    >
                        <a>更多 <Icon type="down" /></a>
                    </Dropdown>
                </React.Fragment>
            }
        }]
    }
    onTableChange = (pagination, filters, sorter) => {
        this.setState({
            pagination: {
                ...this.state.pagination,
                ...pagination
            },
            table: {
                ...this.state.table,
                filters,
                sorter
            }
        }, this.loadData)
    }
    render () {
        const {
            detailModalVisible,
            algorithmModalVisible,
            updateModalVisible,
            selectVersionModalVisible,
            algorithmData,
            detailData,
            updateData,
            versionData,
            pagination,
            modelList,
            loading
        } = this.state;
        return (
            <div className='c-model-view'>
                <header className='c-model-view__header'>已部署模型</header>
                <div className='c-model-view__table__header'>
                    <Input.Search onSearch={(v) => {
                        this.setState({
                            searchName: v
                        }, this.loadData)
                    }} style={{ width: '267px' }} placeholder='按模型名称搜索' />
                </div>
                <Table
                    rowKey='id'
                    loading={loading}
                    className="m-table border-table"
                    columns={this.initColumns()}
                    dataSource={modelList}
                    pagination={pagination}
                    onChange={this.onTableChange}
                />
                <ModelDetailModal
                    visible={detailModalVisible}
                    key={'model' + (detailData && detailData.id)}
                    data={detailData}
                    onCancel={() => {
                        this.setState({
                            detailModalVisible: false,
                            detailData: null
                        })
                    }}
                />
                <AlgorithmModal
                    visible={algorithmModalVisible}
                    data={algorithmData}
                    onCancel={() => {
                        this.setState({
                            algorithmModalVisible: false,
                            algorithmData: null
                        })
                    }}
                />
                <UpdateModal
                    visible={updateModalVisible}
                    data={updateData}
                    key={'update' + (updateData && updateData.id)}
                    onCancel={() => {
                        this.setState({
                            updateModalVisible: false,
                            updateData: null
                        })
                    }}
                />
                <SelectVersionsModal
                    visible={selectVersionModalVisible}
                    data={versionData}
                    key={'version' + (versionData && versionData.id)}
                    onCancel={() => {
                        this.setState({
                            selectVersionModalVisible: false,
                            versionData: null
                        })
                    }}
                />
            </div>
        )
    }
}
export default ModelView;
