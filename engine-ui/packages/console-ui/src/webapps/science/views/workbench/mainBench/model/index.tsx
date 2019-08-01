import * as React from 'react';
import { Table, Dropdown, Menu, Input, Icon, message } from 'antd';

import ModelDetailModal from './detailModal';
import AlgorithmModal from './algorithmModal';
import UpdateModal from './updateModal';
import SelectVersionsModal from './selectVersionModal';

import api from '../../../../api/model'
import { MODEL_STATUS } from '../../../../consts'
import utils from 'utils';
import { generateValueDic } from 'funcs';

class ModelView extends React.Component<any, any> {
    _clock: any = null;
    state: any = {
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
        modelComponentsList: [],
        loading: false,
        table: {
            filters: {},
            sorter: {}
        },
        pagination: {
            total: 0,
            pageSize: 10,
            current: 1
        }
    }
    componentDidMount () {
        this.loadData();
        this.getModelComponents();
    }
    componentWillUnmount () {
        this.clearDataLoad();
    }
    clearDataLoad () {
        if (this._clock) {
            clearTimeout(this._clock);
            this._clock = null;
        }
    }
    getModelComponents = async () => {
        let res = await api.getModelComopnentsList();
        if (res && res.code == 1) {
            this.setState({
                modelComponentsList: res.data
            })
        }
    }
    loadData = async (disableLoading?: any) => {
        this.clearDataLoad();
        if (!disableLoading) {
            this.setState({
                loading: true
            })
        }
        const { pagination, searchName, table } = this.state;
        const { sorter, filters } = table;
        const { columnKey, order } = sorter;
        const params: any = {
            currentPage: pagination.current,
            pageSize: pagination.pageSize,
            modelName: searchName,
            statuss: filters.status,
            componentTypeList: filters.componentName
        }
        if (columnKey) {
            let dic:{ [propName: string]: string } = {
                fileSize: 'memorySort',
                modelName: 'nameSort',
                gmtModified: 'gmtModifySort'
            }
            params[dic[columnKey as string]] = utils.exchangeOrder(order);
        }
        let res = await api.getModelList(params);
        if (res && res.code == 1) {
            this.setState({
                modelList: res.data.data,
                pagination: {
                    ...pagination,
                    total: res.data.totalCount
                }
            })
            this._clock = setTimeout(() => { this.loadData(true) }, 5000);
        }
        this.setState({
            loading: false
        })
    }
    showAlgorithm = (data: any) => {
        this.setState({
            algorithmData: data,
            algorithmModalVisible: true
        })
    }
    showDetailModal = (data: any) => {
        this.setState({
            detailModalVisible: true,
            detailData: data
        })
    }
    showUpdateModal = (data: any) => {
        this.setState({
            updateModalVisible: true,
            updateData: data
        })
    }
    showSelectVersionModal = (data: any) => {
        this.setState({
            selectVersionModalVisible: true,
            versionData: data
        })
    }
    disableModel = async (data: any) => {
        let res = await api.disableModel({
            modelId: data.id
        });
        if (res && res.code == 1) {
            message.success('禁用成功');
            this.loadData();
        }
    }
    enableModel = async (data: any) => {
        let res = await api.openModel({
            modelId: data.id
        });
        if (res && res.code == 1) {
            message.success('打开成功');
            this.loadData();
        }
    }
    deleteModel = async (data: any) => {
        let res = await api.deleteModel({
            modelId: data.id
        });
        if (res && res.code == 1) {
            message.success('删除成功');
            this.loadData();
        }
    }
    initColumns () {
        const { modelComponentsList } = this.state;
        const dic = generateValueDic(MODEL_STATUS);
        return [{
            title: '模型名称',
            dataIndex: 'modelName',
            width: '180px',
            sorter: true
        }, {
            title: '算法名称',
            dataIndex: 'componentName',
            width: '180px',
            filters: modelComponentsList.map((m: any) => {
                return {
                    text: m.type,
                    value: m.key
                }
            })
        }, {
            title: '当前版本',
            dataIndex: 'version',
            render (value: any) {
                return `v${value}`
            }
        }, {
            title: '运行状态',
            dataIndex: 'status',
            filters: Object.entries(dic).map(([key, value]) => {
                return {
                    text: value.text,
                    value: key
                }
            }),
            render (t: any) {
                const item = dic[t];
                const className = item && item.className;
                const text = item && item.text;
                return <span className={className}>{text}</span>;
            }
        }, {
            title: '占用内存(MB)',
            dataIndex: 'fileSizeMb',
            sorter: true,
            render (t: any) {
                return parseFloat(t).toFixed(6);
            }
        }, {
            title: '来源任务',
            dataIndex: 'algorithmName',
            width: '180px'
        }, {
            title: '更新时间',
            dataIndex: 'gmtModified',
            width: '180px',
            sorter: true,
            render (t: any) {
                return utils.formatDateTime(t);
            }
        }, {
            title: '模型操作',
            dataIndex: 'deal',
            width: '260px',
            render: (t: any, record: any) => {
                const menuItems: any = [];
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
                    case MODEL_STATUS.NOT_RUN.value:
                    case MODEL_STATUS.FAILED.value:
                    case MODEL_STATUS.RUNNING.value: {
                        menuItems.push(disableItem);
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
    onTableChange = (pagination: any, filters: any, sorter: any) => {
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
                    <Input.Search onSearch={(v: any) => {
                        this.setState({
                            searchName: v
                        }, this.loadData)
                    }} style={{ width: '267px' }} placeholder='按模型名称搜索' />
                </div>
                <Table
                    rowKey='id'
                    loading={loading}
                    className="dt-ant-table border-table"
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
                    onOk={() => {
                        this.loadData();
                        this.setState({
                            selectVersionModalVisible: false,
                            versionData: null
                        })
                    }}
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
