import * as React from 'react';
import utils from 'utils';
import { connect } from 'react-redux';
import {
    Table, Card, Input, Button, Popconfirm, message, Tooltip
} from 'antd';

import { SECURITY_TYPE } from '../../../consts';
import approvalApi from '../../../api/approval';
import APIDetailModal from './apiDetailModal';
import EditModal from './editModal';

const mapStateToProps = (state: any) => {
    const { user, approval, apiManage, project } = state;
    return { approval, apiManage, user, project }
};

@(connect(mapStateToProps, null) as any)

class Security extends React.Component<any, any> {
    state: any = {
        pagination: {
            current: 1,
            pageSize: 20,
            total: 0
        },
        name: undefined,
        groupList: [],
        loading: false,
        record: {},
        editRecord: {},
        editModalMode: '',
        isApiModalVisible: false,
        isEditModalVisible: false,
        editModalKey: null

    }
    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.fetchGroupList();
        }
    }

    componentDidMount () {
        this.fetchGroupList();
    }

    fetchGroupList () {
        const { pagination, name } = this.state;
        let fetchParams: any = {};
        fetchParams.currentPage = pagination.current;
        fetchParams.pageSize = pagination.pageSize;
        fetchParams.name = name;

        this.setState({
            loading: true
        })
        approvalApi.getSecurityList(fetchParams).then((res: any) => {
            this.setState({
                loading: false
            })
            if (res.code == 1) {
                this.setState({
                    groupList: res.data.data,
                    pagination: {
                        ...pagination,
                        total: res.data.totalCount
                    }
                })
            }
        });
    }
    initColumns () {
        return [{
            title: '名称',
            dataIndex: 'name',
            key: 'name',
            width: '150px'
        }, {
            title: '类型',
            dataIndex: 'type',
            key: 'type',
            render (text: any) {
                return text == SECURITY_TYPE.BLACK ? '黑名单' : '白名单';
            },
            width: '80px'
        }, {
            title: 'IP地址',
            dataIndex: 'ip',
            key: 'ip',
            width: '200px',
            render (text: any) {
                const maxLength = 20;
                return text.length > maxLength ? (text.substring(0, maxLength) + '...') : text;
            }
        }, {
            title: '关联API数量',
            dataIndex: 'refCount',
            key: 'refCount',
            width: '120px',
            render: (text: any, record: any) => {
                return <a onClick={this.openApiDetail.bind(this, record)}>{text}</a>
            }
        }, {
            title: '最近修改人',
            dataIndex: 'modifyUserName',
            key: 'modifyUserName',
            width: 150
        }, {
            title: '最近修改时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            width: 150,
            render (text: any) {
                return utils.formatDateTime(text);
            }
        }, {
            title: '操作',
            dataIndex: 'deal',
            key: 'deal',
            width: '150px',
            render: (text: any, record: any) => {
                return <span>
                    <a onClick={this.openEditModal.bind(this, record, 'view')}>查看详情</a>
                    <span className="ant-divider" ></span>
                    <a onClick={this.openEditModal.bind(this, record, 'edit')}>编辑</a>
                    <span className="ant-divider" ></span>
                    {record.refCount > 0 ? (
                        <Tooltip arrowPointAtCenter={true} title='此安全组已关联API，请解除关联后再删除'>
                            <span>删除</span>
                        </Tooltip>
                    ) : (<Popconfirm title='确认删除此安全组吗？' onConfirm={this.delete.bind(this, record)} okText="确认" cancelText="取消">
                        <a>删除</a>
                    </Popconfirm>)}
                </span>
            }
        }]
    }
    delete (record: any) {
        approvalApi.deleteSecurity({
            groupId: record.id
        }).then((res: any) => {
            if (res.code == 1) {
                message.success('删除成功')
                this.fetchGroupList();
            }
        })
    }
    openEditModal (record: any, mode: any) {
        this.setState({
            isEditModalVisible: true,
            editRecord: record,
            editModalMode: mode,
            editModalKey: Math.random()
        })
    }
    closeEditModal () {
        this.setState({
            isEditModalVisible: false,
            editRecord: {}
        })
    }
    editCallBack () {
        this.fetchGroupList();
        this.closeEditModal();
    }
    openApiDetail (record: any) {
        this.setState({
            isApiModalVisible: true,
            record: record
        })
    }
    closeApiDetail () {
        this.setState({
            isApiModalVisible: false,
            record: {}
        })
    }
    searchRequire (value: any) {
        this.setState({
            name: value
        }, () => {
            this.fetchGroupList();
        })
    }
    getCardTitle () {
        return (
            <div>
                <Input.Search
                    style={{ width: '200px' }}
                    placeholder="按名称或IP地址搜索"
                    onSearch={this.searchRequire.bind(this)}
                />
            </div>
        )
    }
    onTableChange (page: any, filter: any, sorter: any) {
        this.setState({
            pagination: {
                ...this.state.pagination,
                current: page.current
            }
        }, () => {
            this.fetchGroupList();
        });
    }
    render () {
        const { groupList, pagination,
            loading, isApiModalVisible,
            isEditModalVisible, record,
            editRecord, editModalMode,
            editModalKey } = this.state;
        return (
            <div className="api-approval">
                <h1 className="box-title">安全组</h1>
                <div className="margin-0-20 m-card">
                    <Card
                        noHovering
                        title={this.getCardTitle()}
                        className="shadow"
                        extra={<Button onClick={this.openEditModal.bind(this, {}, 'new')} type='primary'>新建安全组</Button>}
                    >
                        <Table
                            rowKey="id"
                            className="m-table monitor-table"
                            columns={this.initColumns()}
                            pagination={pagination}
                            dataSource={groupList}
                            onChange={this.onTableChange.bind(this)}
                            loading={loading}
                        />
                    </Card>
                </div>
                <APIDetailModal
                    key={record.id}
                    record={record}
                    visible={isApiModalVisible}
                    onCancel={this.closeApiDetail.bind(this)}
                />
                <EditModal
                    key={editModalKey}
                    record={editRecord}
                    visible={isEditModalVisible}
                    onCancel={this.closeEditModal.bind(this)}
                    onok={this.editCallBack.bind(this)}
                    mode={editModalMode}
                />
            </div>
        )
    }
}
export default Security;
