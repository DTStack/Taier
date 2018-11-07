import React, { Component } from 'react';
import { Modal, Icon, Row, message } from 'antd';

import CopyUtils from 'utils/copy';

import DBForm from './form';
import API from '../../../../api';
import Response from './response';

class UpdateDatabaseModal extends Component {

    state = {
        databaseData: null,
        submitted: false,
        requesting: false,
    }

    onSubmit = () => {
    
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
        const { defaultData } = this.props;
        form.validateFields( async (err, values) => {
            if (!err) {
                const result = await API.resetDBPassword({
                    databaseId: defaultData.id,
                    oldPwd: values.oldPwd,
                    newPwd: values.dbPwd,
                });
                if (result.code === 1) {
                    defaultData.dbPwd = values.dbPwd;
                    this.setState({
                        databaseData: defaultData,
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
                cancelText={databaseData ? '关闭' : '取消'}
                okText={databaseData ? '确认复制' : '重置'}
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