import React from 'react';
import { withRouter } from 'react-router';

import { Modal, Button, Table } from 'antd';

import api from '../../../api/approval';

class APIDetailModal extends React.Component {
    state = {
        data: [],
        loading: false
    }
    componentDidMount () {
        this.fetchData()
    }
    fetchData () {
        const { record = {}, visible } = this.props;
        if (!record.id || !visible) {
            return;
        }
        this.setState({
            loading: true
        })
        api.listSecurityApiInfo({ groupId: record.id }).then((res) => {
            this.setState({
                loading: false,
                data: []
            })
            if (res.code == 1) {
                this.setState({
                    data: res.data.data
                })
            }
        })
    }
    toDetail (id, name) {
        this.props.router.push({
            pathname: '/api/manage',
            state: {
                apiName: name,
                apiId: id
            }
        })
    }
    initColumns () {
        return [{
            title: 'API名称',
            dataIndex: 'name',
            key: 'name',
            width: '150px',
            render: (text, record) => {
                return <a onClick={this.toDetail.bind(this, record.id, record.name)}>{text}</a>
            }
        }, {
            title: '描述',
            dataIndex: 'apiDesc',
            key: 'apiDesc',
            width: '150px'
        }]
    }
    render () {
        const { visible, onCancel, record } = this.props;
        const { data } = this.state;
        return (
            <Modal
                visible={visible}
                onCancel={onCancel}
                footer={<Button onClick={onCancel} type="primary">关闭</Button>}
                title={`关联API (${record.name})`}
                width={480}
            >
                <Table
                    className="m-table border-table"
                    columns={this.initColumns()}
                    dataSource={data}
                />
            </Modal>
        )
    }
}

export default withRouter(APIDetailModal);
