import React, { Component } from 'react';
import { Modal, Icon, Row } from 'antd';

import workbenchAction from '../../../../consts/workbenchActionType';
import DBForm from './form';
import API from '../../../../api';
import Response from './response';

class UpdateDatabaseModal extends Component {

    state = {
        databaseData: null,
        submitted: false,
    }

    onSubmit = () => {
        
        if (this.state.submitted) {
            this.resetModal();
            return false;
        }

        const form = this.dbForm.props.form;
        form.validateFields( async (err, values) => {
            if (!err) {
                const result = await API.resetDBPassword(values);
                if (result.code === 1) {
                    this.setState({
                        databaseData: result.data,
                        submitted: true,
                    })
                }
            }
        });
    }

    resetModal = () => {
        this.props.onCancel();
        setTimeout(() => {
            this.setState({
                databaseData: null,
                submitted: false,
            });
        }, 0)
    }

    render () {
        const { databaseData } = this.state;
        const { visible, defaultData } = this.props;
        return (
            <Modal
                title="重置密码"
                visible={visible}
                onOk={this.onSubmit}
                onCancel={this.resetModal}
                bodyStyle={{ padding: 0 }}
            >   
                {
                    databaseData ? 
                    <Response data={databaseData} message="重置成功" /> 
                    : 
                    <DBForm
                        databaseData={defaultData}
                        isCreate={false}
                        wrappedComponentRef={(e) => { this.dbForm = e }}
                    />
                }
                <Row className="update-warning" style={{ padding: '0 0 26px 60px' }}>
                    <Icon type="exclamation-circle-o" />&nbsp;
                    <span>重置数据库密码后，您需要手动修改已有连接才能正常访问数据</span>
                </Row>
            </Modal>
        )
    }
}

export default UpdateDatabaseModal