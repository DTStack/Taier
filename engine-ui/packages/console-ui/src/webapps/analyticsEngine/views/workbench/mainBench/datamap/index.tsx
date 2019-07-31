import * as React from 'react';
import { Row, Button, Modal, message } from 'antd';
import DataMapForm from './form';
import Columns from './column';
import API from '../../../../api';
import { singletonNotification } from 'funcs';
import { CATALOGUE_TYPE, dataMapStatus } from '../../../../consts';
import '../../../../styles/views/dataMap.scss';
import SplitPane from 'react-split-pane';
const confirm = Modal.confirm;

class DataMap extends React.Component<any, any> {
    state: any = {
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
    loadTable = async (params: any) => {
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
    getTableColumns = async (params: any) => {
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
        const params: any = {
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
        message.success('DataMap创建成功！');
    }
    onCreate = () => {
        const form = this.formInstance.props.form;
        form.validateFields(async (err: any, values: any) => {
            if (!err) {
                this.setState({
                    loading: true,
                    createLoading: true
                });
                values.configJSON.selectSql = this._selectSQL;
                if (values.configJSON.columns) {
                    values.configJSON.columns = values.configJSON.columns.join(',');
                }
                await API.createDataMap(values).then((res: any) => {
                    if (res.code === 1) {
                        if (res.data.status === dataMapStatus.INITIALIZE) {
                            this.setState({
                                createLoading: true
                            })
                            console.log('INITIALIZE为0')
                            this._checkStatus = setInterval(() => {
                                const dataMapId = res.data.id;
                                API.checkDataMapStatus({ dataMapId: dataMapId }).then((res: any) => {
                                    if (res.code === 1) {
                                        if (res.data.status === 1) {
                                            this.setState({
                                                createLoading: false
                                            })
                                            this._checkStatus && clearInterval(this._checkStatus);
                                            this.props.onGetDataMap({
                                                id: dataMapId
                                            });
                                            this.initStatusSuccess()
                                        } else if (res.data.status === 2) {
                                            this.setState({
                                                createLoading: false
                                            })
                                            this._checkStatus && clearInterval(this._checkStatus);
                                            singletonNotification('错误', res.data.log);
                                            // message.error('dataMap创建失败！')
                                        }
                                    }
                                })
                            }, 1500)
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

    onQueryTextChange = (value: any) => {
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
            <div className="datamap-container">
                <SplitPane
                    split="vertical"
                    minSize={300}
                    maxSize={-650}
                    defaultSize={360}
                    primary="second"
                    className="remove-default border"
                >
                    <div style={{ height: '100%', padding: '24px 0px', overflow: 'auto' }}>
                        <DataMapForm
                            data={data}
                            isCreate={isCreate}
                            tableData={tableData}
                            onGenerateCreateSQL={onGenerateCreateSQL}
                            onQueryTextChange={this.onQueryTextChange}
                            wrappedComponentRef={(e: any) => { this.formInstance = e }}
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
                    <div style={{ height: '100%', paddingTop: '24px', paddingRight: '30px', overflow: 'auto' }}>
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
