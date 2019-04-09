import React from 'react';
import { Table, Modal, Select, message, Form } from 'antd';
import { connect } from 'react-redux';
import { cloneDeep, uniq } from 'lodash';

import { formItemLayout } from '../../consts'
import Api from '../../api/console';
import { getUser } from '../../actions/console'
import SwitchQueue from '../switchQueue';
const Option = Select.Option;

function mapStateToProps (state) {
    return {
        consoleUser: state.consoleUser
    }
}
function mapDispatchToProps (dispatch) {
    return {
        getTenantList () {
            dispatch(getUser())
        }
    }
}
@connect(mapStateToProps, mapDispatchToProps)
class ChangeResourceModal extends React.Component {
    state = {
        loading: false,
        selectUserMap: {},
        selectUser: '', // select输入value
        selectHack: false, // select combobox自带bug
        switchQueueVisible: false,
        tenantInfo: {},
        queueList: []
    }
    componentDidMount () {
        const { resource } = this.props;
        this.setState({
            selectUserMap: this.exchangeSelectMap(resource.tenants)
        })
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const { resource: nextResource, visible: nextVisible } = nextProps;
        const { visible } = this.props;
        if (visible != nextVisible && nextVisible) {
            this.setState({
                selectUserMap: this.exchangeSelectMap(nextResource.tenants),
                loading: false
            })
            this.resetValueByHack();
            this.props.getTenantList();
            this.getQueueLists({ id: nextResource.id })
        }
    }
    /**
     * 获取当前队列集群下的队列列表
    */
    getQueueLists = (params) => {
        Api.getQueueLists(params).then(res => {
            if (res.code === 1) {
                this.setState({
                    queueList: res.data
                })
            }
        })
    }
    exchangeSelectMap (userList = []) {
        let result = {};
        userList.map(
            (item) => {
                result[item.tenantId] = {
                    tenantName: item.tenantName
                }
            }
        )
        return result;
    }
    changeUserValue (value) {
        this.setState({
            selectUser: value
        })
    }
    selectUser (value, option) {
        const { selectUserMap } = this.state;
        this.setState({
            selectUser: '',
            selectUserMap: {
                ...selectUserMap,
                [option.props.tenantid]: {
                    tenantName: option.props.children
                }
            }
        })
    }
    removeUser (id) {
        let { selectUserMap } = this.state;
        selectUserMap = cloneDeep(selectUserMap);
        delete selectUserMap[id];
        this.setState({
            selectUserMap: selectUserMap
        })
    }
    getUserOptions () {
        const result = [];
        const { selectUserMap } = this.state;
        const { consoleUser, resource } = this.props;
        let userList = consoleUser.userList;
        let extUserList = resource.tenants || [];
        userList = uniq(userList.concat(extUserList), 'tenantId')// 去重合并

        for (let i = 0; i < userList.length; i++) {
            const user = userList[i];
            if (!selectUserMap[user.tenantId]) {
                result.push(<Option tenantid={user.tenantId} value={user.tenantName}>{user.tenantName}</Option>)
            }
        }
        return result;
    }
    switchQueue = (record) => {
        this.setState({
            tenantInfo: record,
            switchQueueVisible: true
        })
    }
    initColumns () {
        return [
            {
                title: '租户名称',
                dataIndex: 'name',
                className: 'text-middle',
                render: (text, record) => {
                    return <span className="text-right">{text}</span>
                }
            },
            {
                title: '',
                className: 'text-left',
                render: (text, record) => {
                    return <a className="text-left" onClick={() => { this.switchQueue(record) }}>切换队列</a>
                }
            }
        ]
    }
    getTableDataSource () {
        const { selectUserMap } = this.state;
        const keyAndValue = Object.entries(selectUserMap);
        // 去空
        let noEmptyTenant = [];
        keyAndValue.map(item => {
            if (item[1].tenantName != '') {
                noEmptyTenant.push(item)
            }
        })
        return noEmptyTenant.map((item) => {
            return {
                id: item[0],
                name: item[1].tenantName
            }
        })
    }
    getTenantsList () {
        const { selectUserMap } = this.state;
        const selectKeys = Object.keys(selectUserMap);
        return selectKeys;
    }
    changeResource () {
        this.setState({
            loading: true
        })

        const { resource } = this.props;
        Api.bindUserToQuere({
            queueId: resource.queueId,
            tenants: this.getTenantsList()
        })
            .then(
                (res) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        message.success('修改成功');
                        this.props.resourceUserChange();
                    }
                }
            )
    }
    onCancel () {
        this.props.onCancel()
    }
    resetValueByHack () {
        this.setState({
            selectHack: true
        }, () => {
            this.setState({
                selectHack: false,
                selectUser: ''
            })
        })
    }
    closeVisible () {
        this.setState({
            switchQueueVisible: false
        })
    }
    render () {
        const { selectUser, loading, selectHack, switchQueueVisible, tenantInfo, queueList } = this.state;
        const { visible, resource } = this.props;
        const columns = this.initColumns();
        const { queueName } = resource;

        return (
            <div className="contentBox">
                <Modal
                    title={`修改 (${queueName})`}
                    visible={visible}
                    onCancel={this.onCancel.bind(this)}
                    onOk={this.changeResource.bind(this)}
                    confirmLoading={loading}
                    maskClosable={false}
                    className="m-card"
                >

                    <Form.Item
                        label="绑定租户"
                        {...formItemLayout}
                    >
                        {!selectHack && <Select
                            mode="combobox"
                            style={{ width: '100%' }}
                            placeholder="请选择租户"
                            onSelect={this.selectUser.bind(this)}
                            onSearch={this.changeUserValue.bind(this)}
                            value={selectUser}
                        >
                            {this.getUserOptions()}
                        </Select>}
                    </Form.Item>

                    <Table
                        className="m-table single-th-table"
                        style={{ margin: '0px 20px', marginTop: '30px' }}
                        columns={columns}
                        pagination={false}
                        dataSource={this.getTableDataSource()}
                        scroll={{ y: 300 }}
                    />
                    <SwitchQueue
                        visible={switchQueueVisible}
                        onCancel={this.closeVisible.bind(this)}
                        closeResourceModal={this.props.onCancel}
                        tenantInfo={tenantInfo}
                        resource={this.props.resource}
                        queueList={queueList}
                    />
                </Modal>
            </div>
        )
    }
}

export default ChangeResourceModal;
