import React, { Component } from 'react';
import { Row, Button, Modal, message, Icon } from 'antd';

import DataMapForm from './form';
// import Columns from './column';
import API from '../../../../api';
import { CATALOGUE_TYPE, dataMapStatus } from '../../../../consts';

const confirm = Modal.confirm;

class DataMap extends Component {
    state = {
        tableData: undefined,
        loading: false,
        status: undefined
    }

    // 查询语句
    _selectSQL = undefined;

    componentDidMount () {
        const data = this.props.data;
        this.loadTable({
            id: data.tableId,
            databaseId: data.databaseId
        })
    }
    componentWillUnMount () {
        this._checkStatus && clearInterval(this._checkStatus);
    }
    loadTable = async (params) => {
        const result = await API.getTableById(params);
        this.setState({
            loading: true
        })
        if (result.code === 1) {
            this.setState({
                tableData: result.data,
                loading: false
            })
        }
    }

    reloadDataMapCatalogue = () => {
        const { loadCatalogue, data } = this.props;
        const params = {
            id: data.tableId,
            databaseId: data.databaseId
        };
        // 重新加载DataMap
        loadCatalogue(params, CATALOGUE_TYPE.TABLE);
    }

    // 检查dataMap状态
    initStatusSuccess = () => {
        console.log('DataMap初始化完成')
        this.reloadDataMapCatalogue();
        this._checkStatus && clearInterval(this._checkStatus);
        message.success('DataMap创建成功！');
    }
    renderStatus = (status) => {
        switch (status) {
            case dataMapStatus.INITIALIZE: {
                return (
                    <div style={{ marginBottom: '10px', fontSize: '14px' }}>
                        <Icon type="loading" style={{ fontSize: 14, color: '#2491F7', paddingLeft: 16 }} />
                        <span style={{ color: 'balck', paddingLeft: '8px' }}>DataMap正在初始化</span>
                    </div>
                )
            }
            default: {
                return null;
            }
        }
    }
    onCreate = () => {
        const form = this.formInstance.props.form;
        this.setState({
            loading: true
        });
        form.validateFields(async (err, values) => {
            if (!err) {
                values.configJSON.selectSql = this._selectSQL;
                if (values.configJSON.columns) {
                    values.configJSON.columns = values.configJSON.columns.join(',');
                }
                await API.createDataMap(values).then(res => {
                    if (res.code === 1) {
                        this.props.onGetDataMap({
                            id: res.data.id
                        });
                        if (res.data.status === dataMapStatus.INITIALIZE) {
                            this.setState({
                                status: res.data.status
                            })
                            console.log('INITIALIZE为0')
                            this._checkStatus = setInterval(() => {
                                API.checkDataMapStatus({ dataMapId: res.data.id }).then(res => {
                                    if (res.code === 1) {
                                        this.setState({
                                            status: res.data
                                        })
                                        if (res.data === 1) {
                                            this.initStatusSuccess()
                                        } else if (res.data === 2) {
                                            this._checkStatus && clearInterval(this._checkStatus);
                                            message.error('dataMap创建失败！')
                                        }
                                    }
                                })
                            }, 1000)
                        }
                    }
                });
            }
            this.setState({ loading: false })
        });
    }

    onQueryTextChange = (value) => {
        this._selectSQL = value;
        const form = this.formInstance.props.form;
        form.setFieldsValue({
            'configJSON.selectSql': value
        });
    }

    onRemove = () => {
        const { onRemoveDataMap, data } = this.props;
        confirm({
            title: '警告',
            content: '删除DataMap后无法恢复，确认将其删除？',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk () {
                onRemoveDataMap({
                    databaseId: data.databaseId,
                    tableId: data.tableId,
                    id: data.id
                });
            },
            onCancel () {
                console.log('Cancel');
            }
        });
    }

    render () {
        const { isCreate, data, onGenerateCreateSQL } = this.props;
        const { tableData, loading, status } = this.state;
        return (
            <div className="pane-wrapper" style={{ padding: '24px 20px 50px 20px' }}>
                {this.renderStatus(status)}
                <DataMapForm
                    data={data}
                    isCreate={isCreate}
                    tableData={tableData}
                    onGenerateCreateSQL={onGenerateCreateSQL}
                    onQueryTextChange={this.onQueryTextChange}
                    wrappedComponentRef={(e) => { this.formInstance = e }}
                />
                <Row style={{ paddingLeft: 130 }}>
                    {
                        isCreate
                            ? <Button
                                disabled={loading}
                                style={{ width: 90, height: 30 }} type="primary"
                                onClick={this.onCreate}
                            >
                                创建
                            </Button>
                            : <Button
                                disabled={loading}
                                style={{ width: 90, height: 30, color: 'red' }}
                                onClick={this.onRemove}
                            >
                                删除
                            </Button>
                    }
                </Row>
            </div>
        )
    }
}
export default DataMap;
