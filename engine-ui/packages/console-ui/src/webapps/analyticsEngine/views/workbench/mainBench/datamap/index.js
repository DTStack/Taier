import React, { Component } from 'react';
import { Row, Button, Modal } from 'antd';

import DataMapForm from './form';

const confirm = Modal.confirm;

class DataMap extends Component {

    onCreate = () => {
        const { onCreateDataMap } = this.props;
        const form = this.formInstance.props.form;

        form.validateFields( async (err, values) => {
            if (!err) {
                onCreateDataMap(values);
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
                onRemoveDataMap(data);
            },
            onCancel() {
              console.log('Cancel');
            },
        });
    }

    render () {
        const { isCreate } = this.props;
      
        return (
            <div className="pane-wrapper" style={{ padding: '24px 20px 50px 20px' }}>
                <DataMapForm 
                    {...this.props} 
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