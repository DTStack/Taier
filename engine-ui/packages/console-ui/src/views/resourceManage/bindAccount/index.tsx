import * as React from 'react';
import { Select, Table, Input, Button, message } from 'antd';
import { get } from 'lodash';
import utils from 'dt-common/src/utils';

import Api from '../../../api/console';
import AccountApi, { IAccount } from '../../../api/account';
import { ENGIN_TYPE_TEXT } from '../../../consts';
import BindAccountModal from './bindModal';

const Option = Select.Option;
const Search = Input.Search;

const PAGESIZE = 10;

interface IState {
    tableData: any[];
    queryParams: any;
    loading: boolean;
    visible: boolean;
    modalData: object;
    tenantList: any[];
    unbindUserList: any[];
}

interface IProps {
    engineType: number;
    clusterId: number;
}

class BindAccountTable extends React.Component<IProps, IState> {
    state: IState = {
        tableData: [],
        unbindUserList: [],
        tenantList: [],
        queryParams: {
            dtuicTenantId: '',
            username: '',
            currentPage: 1,
            total: 0,
            pageSize: PAGESIZE
        },
        modalData: null,
        loading: false,
        visible: false
    }

    componentDidMount () {
        this.fetchTenants();
    }

    fetchData = async () => {
        const { queryParams } = this.state;
        const { engineType } = this.props;
        this.setState({
            loading: true
        })
        queryParams.engineType = engineType;
        const res = await AccountApi.getBindAccounts(queryParams);
        if (res.code === 1) {
            this.setState({
                tableData: get(res, 'data.data', []),
                queryParams: Object.assign({}, queryParams, {
                    total: get(res, 'data.totalCount', '')
                })
            })
        }
        this.setState({
            loading: false
        })
    }

    fetchUnbindUsers = async () => {
        const { queryParams } = this.state;
        const { engineType } = this.props;
        const res = await AccountApi.getUnbindAccounts({ dtuicTenantId: queryParams.dtuicTenantId, engineType: engineType });
        if (res.code === 1) {
            this.setState({
                unbindUserList: get(res, 'data', [])
            })
        }
    }

    fetchTenants = async () => {
        const { clusterId, engineType } = this.props;
        const response = await Api.searchTenant({
            clusterId,
            engineType,
            currentPage: 1,
            pageSize: 1000
        })
        if (response.code === 1) {
            const tenantList = get(response, 'data.data', []);
            this.setState({
                tenantList
            })
            if (tenantList && tenantList.length > 0) {
                this.onTenantChange(`${tenantList[0].tenantId}`);
            }
        }
    }

    updateQueryParams = (params: any, callback?: () => void) => {
        this.setState({
            queryParams: Object.assign(this.state.queryParams, params)
        }, () => { if (callback) callback() });
    }

    onTenantChange = (value: string) => {
        this.updateQueryParams({
            dtuicTenantId: value
        }, () => {
            this.handleTableChange({ current: 1 });
            this.fetchUnbindUsers();
        });
    }

    handleTableChange = (pagination: any, filters?: any, sorter?: any) => {
        this.updateQueryParams({ currentPage: pagination.current }, this.fetchData)
    }

    showHideBindModal = (item?: any) => {
        this.setState({
            visible: !this.state.visible,
            modalData: item
        })
    }

    onUnBindAccount = async (account: IAccount) => {
        const res = await AccountApi.unbindAccount({
            id: account.bindUserId,
            name: account.name,
            password: account.password
        });
        if (res.code === 1) {
            message.success('解绑成功！');
            this.showHideBindModal(null);
            this.handleTableChange({ current: 1 })
            this.fetchUnbindUsers();
        }
    }

    onBindAccountUpdate = async (account: IAccount) => {
        const { queryParams, modalData } = this.state;
        const { engineType } = this.props;
        const isEdit = modalData;
        const handOk = () => {
            this.showHideBindModal(null);
            this.handleTableChange({ current: 1 })
            this.fetchUnbindUsers();
        }
        account.bindTenantId = queryParams.dtuicTenantId;
        account.engineType = engineType;
        let res = { code: 0 };
        if (isEdit) {
            res = await AccountApi.updateBindAccount(account);
        } else {
            res = await AccountApi.bindAccount(account);
        }
        if (res.code === 1) {
            message.success('绑定成功！');
            handOk();
        }
    }

    initColumns = () => {
        return [
            {
                title: '产品账号',
                dataIndex: 'username',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '数据库账号',
                dataIndex: 'name',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '最近修改人',
                dataIndex: 'modifyUserName',
                render (text: any, record: any) {
                    return text
                }
            },
            {
                title: '最近修改时间',
                dataIndex: 'gmtModified',
                render (text: any, record: any) {
                    return utils.formatDateTime(text);
                }
            },
            {
                title: '操作',
                dataIndex: 'deal',
                render: (text: any, record: any) => {
                    return <a onClick={ () => { this.showHideBindModal(record) }}>
                        修改绑定
                    </a>
                }
            }
        ]
    }

    render () {
        const {
            tableData, queryParams, loading,
            visible, modalData, tenantList, unbindUserList
        } = this.state;
        const { engineType } = this.props;
        const pagination: any = {
            current: queryParams.currentPage,
            pageSize: PAGESIZE,
            total: queryParams.total
        }
        return (
            <div style={{ margin: '15px' }}>
                <Select
                    className='cluster-select'
                    style={{ width: '180px' }}
                    placeholder='请选择租户'
                    showSearch
                    value={`${queryParams.dtuicTenantId}`}
                    optionFilterProp="title"
                    onChange={this.onTenantChange}
                >
                    {
                        tenantList && tenantList.map((item: any) => {
                            return <Option key={`${item.tenantId}`} title={item.tenantName} value={`${item.tenantId}`}>{item.tenantName}</Option>
                        })
                    }
                </Select>
                <Search
                    style={{ width: '200px', marginBottom: '20px', marginLeft: '10px' }}
                    placeholder='按产品账号、数据库账号搜索'
                    value={queryParams.username}
                    onChange={(e: any) => {
                        this.updateQueryParams({ username: e.target.value, currentPage: 1 });
                    } }
                    onSearch={this.fetchData}
                />
                <span className="right">
                    <Button type="primary" onClick={() => this.showHideBindModal()}>绑定账号</Button>
                </span>
                <Table
                    className='m-table border-table'
                    loading={loading}
                    rowKey={(record, index) => `accounts-${index}-${record.userId}`}
                    columns={this.initColumns()}
                    dataSource={tableData}
                    pagination={pagination}
                    onChange={this.handleTableChange}
                />
                <BindAccountModal
                    visible={visible}
                    data={modalData}
                    userList={modalData ? tableData : unbindUserList}
                    title={modalData ? '编辑账号' : '绑定账号'}
                    onOk={this.onBindAccountUpdate}
                    onUnbind={this.onUnBindAccount}
                    onCancel={() => this.showHideBindModal(null)}
                    engineText={ENGIN_TYPE_TEXT[engineType]}
                />
            </div>
        )
    }
}
export default BindAccountTable;
