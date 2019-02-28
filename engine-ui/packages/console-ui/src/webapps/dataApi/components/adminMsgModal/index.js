import React from 'react';

import { Modal } from 'antd';

import api from '../../api/mine';

class AdminMsgModal extends React.Component {
    constructor (props) {
        super(props);
        this.state = {
            data: {}
        }
        this.initData();
    }
    initData () {
        const { apiId } = this.props;
        api.getApiCreatorInfo({
            apiId: apiId
        }).then((res) => {
            if (res.code == 1) {
                this.setState({
                    data: res.data
                })
            }
        })
    }
    render () {
        const { data = {} } = this.state;
        return (
            <ul>
                <li>用户名:{data.userName}</li>
                <li>手机号码:{data.phoneNumber}</li>
                <li>邮箱:{data.email}</li>
            </ul>
        )
    }
}
export function showAdminMsg (apiId) {
    Modal.warning({
        title: '请联系管理员申请修改调用次数及周期',
        content: <AdminMsgModal apiId={apiId} />
    })
}
export default AdminMsgModal;
