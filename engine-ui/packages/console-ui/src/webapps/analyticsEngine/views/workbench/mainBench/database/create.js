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
        requesting: false
    }

    onSubmit = async () => {
        this.setState({
            requesting: true
        })
        const { loadCatalogue } = this.props;

        if (this.state.submitted) {
            const copyInstance = new CopyUtils();
            const { databaseData } = this.state;
            const copyContent = `
                数据库标识：${databaseData.name}\n
                JDBC信息：${databaseData.jdbcUrl}\n
                用户名：${databaseData.dbUserName}\n
                密码：${databaseData.dbPwd}
            `;
            copyInstance.copy(copyContent, (success) => {
                if (success) message.success('复制成功！')
            })
            return false;
        }

        const form = this.dbForm.props.form;

        form.validateFields(async (err, values) => {
            if (!err) {
                // 创建数据库默认ID -1, 用于后端权限校验
                values.databaseId = -1;
                const result = await API.createDB(values);
                if (result.code === 1) {
                    this.setState({
                        databaseData: result.data,
                        submitted: true
                    });
                    loadCatalogue();
                    // 移除当前元素active样式
                    document.activeElement.blur();
                }
                this.setState({
                    requesting: false
                })
            }
        });
    }

    resetModal = () => {
        this.props.resetModal();
        this.dbForm.props.form.resetFields();
        setTimeout(() => {
            this.setState({
                databaseData: null,
                submitted: false,
                requesting: false
            });
        }, 0)
    }

    render () {
        const { databaseData, requesting } = this.state;
        const { modal } = this.props;
        const visible = !!(modal && modal.visibleModal === workbenchAction.OPEN_CREATE_DATABASE);

        return (
            <Modal
                title="创建数据库"
                visible={visible}
                okText={databaseData ? '复制' : '确认'}
                cancelText={databaseData ? '关闭' : '取消'}
                onOk={this.onSubmit}
                maskClosable={false}
                onCancel={this.resetModal}
                bodyStyle={{ padding: 0 }}
            >
                {
                    databaseData
                        ? <Response data={databaseData} message="创建成功" />
                        : <DBForm
                            isCreate={true}
                            wrappedComponentRef={(e) => { this.dbForm = e }}
                        />
                }
            </Modal>
        )
    }
}

export default CreateDatabaseModal
