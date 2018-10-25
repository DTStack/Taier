import React, { Component } from 'react';
import { Row, Button, Modal, message } from 'antd';

import DataMapForm from './form';
import API from '../../../../api';

const confirm = Modal.confirm;

class DataMap extends Component {

    state = {
        tableData: undefined,
    }

    componentDidMount() {
        const data = this.props.data;
        this.loadTable({
            id: data.tableId,
            databaseId: data.databaseId,
        })
    }

    loadTable = async (params) => {
        const result = await API.getTableById(params);
        if (result.code === 1) {
            this.setState({
                tableData: result.data,
            })
        }
    }

    onCreate = () => {
        const form = this.formInstance.props.form;
        form.validateFields( async (err, values) => {
            if (!err) {
                values.configJSON = JSON.stringify(values.configJSON);
                values.datamapType = undefined;
                const res = await API.createDataMap(values);
                if (res.code === 1) {
                    message.success('创建DataMap成功！');
                }
            }
        });
    }
    
    onRemove = () => {
        const { onRemoveDataMap, data } = this.props;
        confirm({
            title: '警告',
            content: '确认删除当前的DataMap吗？',
            okText: '确定',
            okType: 'danger',
            cancelText: '取消',
            onOk() {
                onRemoveDataMap({
                    databaseId: data.databaseId,
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
        const { tableData } = this.state;
        return (
            <div className="pane-wrapper" style={{ padding: '24px 20px 50px 20px' }}>
                <DataMapForm 
                    data={data}
                    tableData={tableData}
                    onGenerateCreateSQL={onGenerateCreateSQL}
                    wrappedComponentRef={(e) => { this.formInstance = e }}
                />
                <Row style={{paddingLeft: 130}}>
                    {
                        isCreate ? 
                            <Button
                                style={{ width: 90, height: 30 }} type="primary"
                                onClick={this.onCreate}
                            >
                                创建
                            </Button>
                        :
                            <Button 
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