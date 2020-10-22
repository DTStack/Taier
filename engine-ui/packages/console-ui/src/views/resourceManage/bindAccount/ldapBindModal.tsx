import * as React from 'react'
import { Modal, Alert, Table, Select, Input, Icon,
    Form } from 'antd'
import { formItemLayout } from '../../../consts'
import { FormComponentProps } from 'antd/lib/form/Form'
import { giveMeAKey } from './help'
import { cloneDeep, get } from 'lodash'

const Option = Select.Option
const MAX_ACCOUNT_NUM = 20

interface IProps extends FormComponentProps {
    title: string;
    visible: boolean;
    userList: any[];
    data: object;
    onCancel: (e: any) => any;
    onOk: (e: any) => any;
}

interface TableData {
    id: string;
    name?: string;
    bindUserId?: number;
    passworld?: string;
}

class BindAccountModal extends React.Component<IProps, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            tableData: [
                {
                    id: giveMeAKey()
                }
            ]
        }
    }

    addTableData = () => {
        this.setState((prevState: any) => ({
            tableData: [...prevState.tableData, {
                id: giveMeAKey()
            }]
        }))
    }

    deleteTableData = (record: TableData) => {
        const { tableData } = this.state
        let newTableData: any[] = cloneDeep(tableData)
        newTableData = newTableData.filter((table: TableData) => table.id !== record.id)
        this.setState({
            tableData: newTableData
        })
    }

    handleValueChange = (key: string, value: any, record: TableData) => {
        const { tableData } = this.state
        const newTableData = cloneDeep(tableData)
        const index = newTableData.findIndex(t => t.id == record.id)
        if (index > -1) newTableData[index][key] = value
        this.setState({
            tableData: newTableData
        })
    }

    onCancel = (e: any) => {
        this.props.onCancel(e)
    }

    onSubmit = () => {
        const { form, data, userList, onOk } = this.props
        const isEdit = data !== null && data !== undefined
        if (!isEdit) {
            const { tableData } = this.state
            const newTableData = tableData.map(table => {
                const selectedUser = userList.find(u => u.userId == table.bindUserId)
                return {
                    ...table,
                    password: '',
                    username: selectedUser.userName,
                    emali: selectedUser.userName
                }
            })
            onOk(newTableData)
            return;
        }
        form.validateFields((err, user) => {
            if (!err) {
                user.id = get(data, 'id', '')
                user.bindUserId = ''
                const selectedUser = userList.find(u => u.userId == user.bindUserId)
                if (selectedUser) {
                    user.username = selectedUser.userName
                    user.email = selectedUser.userName
                    user.bindUserId = selectedUser.bindUserId
                }
                onOk(user);
            }
        })
    }

    initColumns = () => {
        const { userList } = this.props
        return [
            {
                title: '产品账号',
                dataIndex: 'bindUserId',
                render: (text: any, record: TableData) => {
                    return (
                        <Select
                            defaultValue={text}
                            style={{ width: 160 }}
                            onChange={(value) => this.handleValueChange('bindUserId', value, record)}
                        >
                            {userList.map((user) => {
                                return <Option key={`${user.userId}`} value={user.userId}>{user.userName}</Option>
                            })}
                        </Select>
                    )
                }
            },
            {
                title: 'LDAP账号',
                dataIndex: 'name',
                render: (text: any, record: TableData) => {
                    return <Input
                        onChange={(e) => this.handleValueChange('name', e.target.value, record)}
                        style={{ width: 160 }}
                        value={text} />
                }
            },
            {
                title: null,
                dataIndex: 'action',
                width: 40,
                render: (text: any, record: TableData) => {
                    return <Icon type="delete" onClick={() => this.deleteTableData(record)} />
                }
            }
        ]
    }
    render () {
        const { tableData } = this.state
        const { title, visible, data } = this.props
        const { getFieldDecorator } = this.props.form
        return (
            <Modal
                className="no-padding-modal"
                closable
                title={title}
                visible={visible}
                onCancel={this.onCancel}
                onOk={this.onSubmit}
            >
                <Alert
                    style={{ border: 0 }}
                    message="产品账号绑定LDAP账号后任务提交至Yarn上将对接用"
                    type="info"
                    showIcon
                />
                <div style={{ padding: '12px 20px' }}>
                    {
                        !data ? <React.Fragment>
                            <Table
                                className='c-ldap__bindModal'
                                columns={this.initColumns()}
                                dataSource={tableData}
                                pagination={false}
                            />
                            {
                                tableData.length > MAX_ACCOUNT_NUM ? <a
                                    style={{ color: '#BFBFBF' }} >
                                    <Icon type="plus" />新增
                                </a> : <a
                                    style={{ color: '#3F87FF' }}
                                    onClick={this.addTableData}>
                                    <Icon type="plus" />新增
                                </a>
                            }
                        </React.Fragment> : <React.Fragment>
                            <Form>
                                <Form.Item
                                    key="bindUserId"
                                    label="产品账号"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator('bindUserId', {
                                        rules: [{
                                            required: true,
                                            message: '产品账号不可为空！'
                                        }],
                                        initialValue: get(data, 'username', '')
                                    })(
                                        <Input disabled />
                                    )}
                                </Form.Item>
                                <Form.Item
                                    key="bindLdapAccount"
                                    label="LDAP账号"
                                    {...formItemLayout}
                                >
                                    {getFieldDecorator('name', {
                                        rules: [{
                                            required: true,
                                            message: 'LDAP账号不可为空！'
                                        }],
                                        initialValue: get(data, 'name', '')
                                    })(
                                        <Input />
                                    )}
                                </Form.Item>
                            </Form>
                        </React.Fragment>
                    }
                </div>
            </Modal>
        )
    }
}

export default Form.create<IProps>()(BindAccountModal)
