import React, { Component } from 'react';
import { Modal, message } from 'antd';

import CopyUtils from 'utils/copy';
import workbenchAction from '../../../../consts/workbenchActionType';
import DBForm from './form';
import API from '../../../../api';
import Response from './response';

class CreateDatabaseModal extends Component {

    state = {
        databaseData: null,
        submitted: false,
    }

    onSubmit = async () => {
        const { loadCatalogue } = this.props;
        if (this.state.submitted) {
            const copyInstance = new CopyUtils();
            const { databaseData } = this.state;
            const copyContent = `
                数据库标识：${databaseData.name}\m
                JDBC信息：${databaseData.jdbcUrl}\m
                用户名：${databaseData.dbUserName}\m
                密码：${databaseData.dbPwd}
            `;
            copyInstance.copy(copyContent, (success) => {
                if (success) message.success('复制成功！')
            })
            return false;
        }

        const form = this.dbForm.props.form;

        form.validateFields( async (err, values) => {
            if (!err) {
                const result = await API.createDB(values);
                if (result.code === 1) {
                    this.setState({
                        databaseData: result.data,
                        submitted: true,
                    });
                    loadCatalogue();
                }
            }
        });
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

        console.log('modal:', modal, visible)
        return (
            <Modal
                title="创建数据库"
                visible={visible}
                okText={databaseData ? '确认复制' : '确认'}
                cancelText={databaseData ? '关闭' : '取消'}
                onOk={this.onSubmit}
                onCancel={this.resetModal}
                bodyStyle={{ padding: 0 }}
            >
                {
                    databaseData ? 
                    <Response data={databaseData} message="创建成功" /> 
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

export default CreateDatabaseModal