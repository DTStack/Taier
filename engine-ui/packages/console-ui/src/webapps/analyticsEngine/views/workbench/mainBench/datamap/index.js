import React, { Component } from 'react';
import { Row, Button, Modal, message } from 'antd';

import DataMapForm from './form';
import API from '../../../../api';
import { CATALOGUE_TYPE } from '../../../../consts';

const confirm = Modal.confirm;

class DataMap extends Component {

    state = {
        tableData: undefined,
        loading: false,
    }

    // 查询语句
    _selectSQL = undefined;
    
    componentDidMount() {
        const data = this.props.data;
        this.loadTable({
            id: data.tableId,
            databaseId: data.databaseId,
        })
    }

    loadTable = async (params) => {
        const result = await API.getTableById(params);
        this.setState({
            loading: true,
        })
        if (result.code === 1) {
            this.setState({
                tableData: result.data,
                loading: false,
            })
        }
    }

    reloadDataMapCatalogue = () => {
        const { loadCatalogue, data } = this.props;
        const params = {
            id: data.tableId,
            databaseId: data.databaseId,
        };
        // 重新加载DataMap
        loadCatalogue(params, CATALOGUE_TYPE.TABLE);
    }

    onCreate = () => {
        const form = this.formInstance.props.form;
        this.setState({
            loading: true,
        });
        form.validateFields( async (err, values) => {
            if (!err) {
                values.configJSON.selectSql = this._selectSQL;
                if (values.configJSON.columns) {
                    values.configJSON.columns = values.configJSON.columns.join(',');
                }
                const res = await API.createDataMap(values);
                if (res.code === 1) {
                    message.success('创建DataMap成功！');
                   this.reloadDataMapCatalogue();
                   if (res.data) {
                       this.props.onGetDataMap({
                           id: res.data.id
                       });
                   }
                }
            }
            this.setState({ loading: false, })
        });
    }

    onQueryTextChange = (value) => {
        this._selectSQL = value;
        const form = this.formInstance.props.form;
        form.setFieldsValue({
            'configJSON.selectSql': value,
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
            onOk() {
                onRemoveDataMap({
                    databaseId: data.databaseId,
                    tableId: data.tableId,
                    id: data.id,
                });
            },
            onCancel() {
              console.log('Cancel');
            },
        });
    }

    render () {
        const { isCreate, data, onGenerateCreateSQL } = this.props;
        const { tableData, loading } = this.state;
        return (
            <div className="pane-wrapper" style={{ padding: '24px 20px 50px 20px' }}>
                <DataMapForm 
                    data={data}
                    isCreate={isCreate}
                    tableData={tableData}
                    onGenerateCreateSQL={onGenerateCreateSQL}
                    onQueryTextChange={this.onQueryTextChange}
                    wrappedComponentRef={(e) => { this.formInstance = e }}
                />
                <Row style={{paddingLeft: 130}}>
                    {
                        isCreate ? 
                            <Button
                                disabled={loading}
                                style={{ width: 90, height: 30 }} type="primary"
                                onClick={this.onCreate}
                            >
                                创建
                            </Button>
                        :
                            <Button 
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

export default DataMap