import React, { Component } from 'react';
import { Row, Button, Modal, message } from 'antd';
import DataMapForm from './form';
import Columns from './column';
import API from '../../../../api';
import { CATALOGUE_TYPE, dataMapStatus } from '../../../../consts';
import '../../../../styles/views/dataMap.scss';
import SplitPane from 'react-split-pane';
const confirm = Modal.confirm;

class DataMap extends Component {
    state = {
        tableData: undefined,
        loading: false,
        createLoading: false,
        tableColumnsLoading: false,
        tableColumns: undefined
    }

    // 查询语句
    _selectSQL = undefined;

    componentDidMount () {
        const data = this.props.data;
        this.loadTable({
            id: data.tableId,
            databaseId: data.databaseId
        });
        this.getTableColumns({
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
    // 获取表列名
    getTableColumns = async (params) => {
        const result = await API.getTableColumns(params);
        this.setState({
            tableColumnsLoading: true
        })
        if (result.code === 1) {
            this.setState({
                tableColumns: result.data,
                tableColumnsLoading: false
            })
        }
        console.log(result.data);
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
    onCreate = () => {
        const form = this.formInstance.props.form;
        this.setState({
            loading: true,
            createLoading: true
        });
        form.validateFields(async (err, values) => {
            if (!err) {
                values.configJSON.selectSql = this._selectSQL;
                if (values.configJSON.columns) {
                    values.configJSON.columns = values.configJSON.columns.join(',');
                }
                await API.createDataMap(values).then(res => {
                    if (res.code === 1) {
                        if (res.data.status === dataMapStatus.INITIALIZE) {
                            this.setState({
                                createLoading: true
                            })
                            console.log('INITIALIZE为0')
                            this._checkStatus = setInterval(() => {
                                API.checkDataMapStatus({ dataMapId: res.data.id }).then(res => {
                                    if (res.code === 1) {
                                        this.setState({
                                            createLoading: true
                                        })
                                        if (res.data === 1) {
                                            this.setState({
                                                createLoading: false
                                            })
                                            this.props.onGetDataMap({
                                                id: res.data.id
                                            });
                                            this.initStatusSuccess()
                                        } else if (res.data === 2) {
                                            this.setState({
                                                createLoading: false
                                            })
                                            this._checkStatus && clearInterval(this._checkStatus);
                                            message.error('dataMap创建失败！')
                                        }
                                    }
                                })
                            }, 1000)
                        } else if (res.data.status === dataMapStatus.NORMAL) {
                            this.props.onGetDataMap({
                                id: res.data.id
                            });
                            this.initStatusSuccess()
                        }
                    } else {
                        this.setState({
                            createLoading: false
                        })
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
        const { tableData, loading, createLoading, tableColumns, tableColumnsLoading } = this.state;
        return (
            <div className="pane-wrapper" style={{ padding: '0px 20px 50px 20px' }}>
                <SplitPane
                    split="vertical"
                    minSize={400}
                    maxSize={600}
                    defaultSize={400}
                    primary="second"
                    className="remove-default border"
                >
                    <div className="pane-wrapper" style={{ float: 'left', paddingTop: '24px' }}>
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
                                        // disabled={loading}
                                        style={{ width: 90, height: 30 }} type="primary"
                                        onClick={this.onCreate}
                                        loading={createLoading}
                                    >
                                        { createLoading ? '创建中' : '创建' }
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
                    <div className="pane-wrapper" style={{ float: 'right', paddingTop: '24px', marginRight: '30px' }}>
                        <Columns
                            tableColumns={tableColumns}
                            tableColumnsLoading={tableColumnsLoading}
                        ></Columns>
                    </div>
                </SplitPane>
            </div>
        )
    }
}
export default DataMap;
