import React, { Component } from 'react';
import { Modal } from 'antd';

import workbenchAction from '../../../../consts/workbenchActionType';
import DBForm from './form';
import API from '../../../../api';
import UpdateSucc from './updateSucc';

class UpdateDatabaseModal extends Component {

    state = {
        databaseData: null,
        submitted: false,
    }

    onSubmit = async () => {
        if (this.state.submitted) {
            this.resetModal();
            return false;
        }

        const form = this.dbForm.props.form;
        const formData = form.getFieldsValue();

        const result = await API.createOrUpdateDB(formData);
        if (result.code === 1) {
            this.setState({
                databaseData: result.data,
                submitted: true,
            })
        }
    }

    resetModal = () => {
        this.props.resetModal();
        setTimeout(() => {
            this.setState({
                databaseData: null,
                submitted: false,
            });
        }, 0)
    }

    render () {
        const { databaseData } = this.state;
        const { modal } = this.props;
        const visible =  modal && modal.visibleModal === workbenchAction.OPEN_CREATE_DATABASE 
        ? true : false;

        return (
            <Modal
                title="创建数据库"
                visible={visible}
                onOk={this.onSubmit}
                onCancel={this.resetModal}
                bodyStyle={{ padding: 0 }}
            >   
                {
                    databaseData ? 
                    <UpdateSucc data={databaseData} message="创建成功" /> 
                    : 
                    <DBForm
                        isCreate={true}
                        wrappedComponentRef={(e) => { this.dbForm = e }}
                    />
                }
            </Modal>
        )
    }
}

export default UpdateDatabaseModal