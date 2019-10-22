import * as React from 'react'
import { Form, Input, Modal, Button, Checkbox, Pagination, Icon, Tooltip } from 'antd'

import ajax from '../../../api/dataManage';

import '../../../styles/pages/dataManage.scss';

const FormItem = Form.Item;
const CheckboxGroup = Checkbox.Group;
const pageSize = 20;
const formItemLayout1: any = { // ddl,dml表单布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 4 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 20 }
    }
}

let num = 1; // 解决切换tab栏后会多次发出请求

class DetailPermission extends React.Component<any, any> {
    state: any = {
        currentPage: 1,
        arr: [],
        permissionParams: {},
        reply: undefined,
        applyReason: undefined
    }

    shouldComponentUpdate (nextProps: any) {
        if (this.props.visible != nextProps.visible && nextProps.visible) {
            return false
        }
        return true
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        const table = nextProps.table[0];
        if (this.props.table != nextProps.table) {
            this.setState({
                reply: table.reply,
                applyReason: table.applyReason
            })
            this.setState({
                permissionParams: {}
            })
            if (num <= 2) {
                this.getPermissionData(table);
                num++;
            }
        } else {
        }
    }

    // 请求数据
    getPermissionData = (record: any) => {
        const { onCancel } = this.props
        ajax.getApplyDetail({
            tableId: record.resourceId,
            tableName: record.resourceName,
            applyId: record.applyId
        }).then((res: any) => {
            if (res.code === 1) {
                const data = res.data;
                const fullDdls = data.fullDdls;
                const fullDmls = data.fullDmls;
                const fullColumns = data.fullColumnList;
                // ddl数据转化成checkboxGroup可用数据
                const fullDdlsData = fullDdls.map((item: any) => {
                    return {
                        label: item.name,
                        value: item.value,
                        status: item.status
                    }
                });
                const ddlCheck = fullDdlsData.filter((item: any) => {
                    return item.status === true
                })
                const ddlCheckArray = ddlCheck.map((item: any) => {
                    return item.value
                })
                // dml数据转化成checkboxGroup可用数据
                const fullDmlsData = fullDmls.map((item: any) => {
                    return {
                        label: item.name,
                        value: item.value,
                        status: item.status
                    }
                });
                const dmlCheck = fullDmlsData.filter((item: any) => {
                    return item.status === true
                })
                const dmlCheckArray = dmlCheck.map((item: any) => {
                    return item.value
                })
                // 字段名数据转化成checkboxGroup可用数据
                const fullColumnsData = fullColumns.map((item: any) => {
                    return item.column
                });
                const fullColumnsCheckArray = fullColumns.filter((item: any) => {
                    return item.status === true
                })
                const ids = fullColumnsCheckArray.map((item: any) => {
                    return item.column
                })
                const total = fullColumnsData.length;
                // 判断是否全选
                const ischeckAll = (fullDdlsData.length + fullDmlsData.length) == (ddlCheck.length + dmlCheck.length);
                const idCheckIds = (fullColumns.length == fullColumnsCheckArray.length);
                const params: any = {
                    fullDdlsData,
                    ddlCheckArray,
                    fullDmlsData,
                    dmlCheckArray,
                    fullColumnsData,
                    fullColumns,
                    ids,
                    total,
                    ischeckAll,
                    idCheckIds
                }
                this.setState({
                    permissionParams: params
                }, () => {
                    this.getFirstPagination()
                })
                num = 2;
            } else {
                num = 2;
                onCancel()
            }
        })
    }
    // 修复初始时无法显示第一页数据
    getFirstPagination = () => {
        const { permissionParams } = this.state;
        const fullColumnsCheck = permissionParams.fullColumnsData;
        const arr = fullColumnsCheck.slice(0, pageSize)
        this.setState({
            arr
        })
    }
    changePagination = (currentPage: any, pageSize: any) => {
        const { permissionParams } = this.state;
        const fullColumnsCheck = permissionParams.fullColumnsData;
        const arr = fullColumnsCheck.slice((currentPage - 1) * pageSize, (currentPage - 1) * pageSize + pageSize)
        this.setState({
            currentPage,
            arr
        })
    }

    submit = (e: any) => {
        e.preventDefault()
        const { onOk, form } = this.props
        const formData = form.getFieldsValue()
        form.validateFields((err: any) => {
            if (!err) {
                setTimeout(() => {
                    this.setState({
                        currentPage: 1
                    })
                    form.resetFields()
                }, 200)
                onOk(formData)
            }
        });
    }

    cancle = () => {
        const { onCancel, form } = this.props
        onCancel()
        form.resetFields()
        this.setState({
            currentPage: 1
        })
    }

    handleResource (type: any) {
        const { table } = this.props;
        let data: any;
        if (type === 'resourceName') {
            const resourceName = table.map((v: any) => {
                return v.resourceName
            })
            data = resourceName.join(' 、')
        } else {
            data = table.map((v: any) => {
                return v.applyId
            })
        }
        return data
    }

    render () {
        const { getFieldDecorator } = this.props.form;
        const { visible, agreeApply } = this.props;

        const title = (this.props.listType == 0 && agreeApply) ? '通过申请' : ((this.props.listType == 0 && !agreeApply) ? '驳回申请' : '查看详情')
        const { arr, currentPage, permissionParams = {}, reply, applyReason } = this.state;
        return (
            <Modal
                title={title}
                visible={visible}
                // onOk={this.submit}
                onOk={this.props.listType == 0 ? this.submit : this.cancle}
                onCancel={this.cancle}
                width="750px"
                footer={
                    this.props.listType == 0 ? [
                        <Button key="permissionCancel" size="large" onClick={this.cancle}>取消</Button>,
                        <Button key="permissionSubmit" type="primary" size="large" onClick={this.submit}>确定</Button>
                    ] : <Button type="primary" size="large" onClick={this.cancle}>关闭</Button>
                }
            >
                <Form>
                    <FormItem
                        {...formItemLayout1}
                        label="名称"
                        hasFeedback
                    >
                        {getFieldDecorator('tableName', {
                            rules: []
                        })(
                            <span style={{ marginRight: '10px' }}>{this.handleResource('resourceName')}</span>

                        )}
                        <Checkbox disabled checked={permissionParams.ischeckAll}>All</Checkbox>
                    </FormItem>

                    {/* 表段权限 */}
                    <FormItem
                        {...formItemLayout1}
                        label="DDL"
                        style={{ marginLeft: '20px', marginTop: '-20px' }}
                    >
                        <div className="content">
                            <CheckboxGroup options={permissionParams.fullDdlsData} value={permissionParams.ddlCheckArray} disabled></CheckboxGroup>
                        </div>
                    </FormItem>
                    <FormItem
                        {...formItemLayout1}
                        label="DML"
                        style={{ marginLeft: '20px', marginTop: '-20px' }}
                    >
                        <div className="content">
                            <CheckboxGroup options={permissionParams.fullDmlsData} value={permissionParams.dmlCheckArray} disabled ></CheckboxGroup>
                        </div>
                    </FormItem>

                    {/* 字段权限 */}
                    <FormItem
                        {...formItemLayout1}
                        label="字段权限"
                        style={{ background: '#FAFAFA' }}
                    >
                        <Checkbox disabled checked={permissionParams.idCheckIds}>All(包括新增字段)</Checkbox>
                        <Tooltip title={(
                            <div>
                                <p>字段权限包括对字段进行select。</p>
                                <p>若勾选了All，如果表中有增加的字段，则此用户自动拥有此字段的权限；</p>
                                <p>若未勾选All，如果表中有增加的字段，则此用户不会拥有此字段的权限；</p>
                                <p>表、字段的权限适用于所有分区；</p>
                            </div>
                        )}>
                            <Icon className="formItem_inline_icon" type="question-circle-o" />
                        </Tooltip>
                        <div className="content">
                            <div>
                                <CheckboxGroup options={arr} value={permissionParams.ids} disabled></CheckboxGroup>
                            </div>
                        </div>
                        {/* 控制字段名分页 */}
                        {permissionParams.total > 0 ? <Pagination
                            size="small"
                            total={permissionParams.total}
                            pageSize={pageSize}
                            current={currentPage}
                            onChange={this.changePagination}
                            style={{ marginLeft: '70%', marginTop: '10px', marginBottom: '20px' }}
                        /> : ''}
                    </FormItem>

                    <FormItem
                        style={{ display: 'none' }}
                    >
                        {getFieldDecorator('ids', {
                            rules: [],
                            initialValue: this.handleResource('ids')
                        })(
                            <Input type="hidden" />
                        )}
                    </FormItem>

                    <FormItem
                        style={{ display: 'none' }}
                    >
                        {getFieldDecorator('status', {
                            rules: [],
                            initialValue: agreeApply ? 1 : 2
                        })(
                            <Input type="hidden" />
                        )}
                    </FormItem>

                    {/* 申请记录 */}
                    {
                        <FormItem
                            {...formItemLayout1}
                            label={'申请理由'}
                            hasFeedback
                        >
                            <Input.TextArea rows={4} placeholder="" disabled={true} value={applyReason} style={{ width: '80%' }} />
                        </FormItem>
                    }
                    {/* 已处理  权限回收 */}
                    {
                        this.props.listType == 1 || this.props.listType == 2 || this.props.listType == 3 ? <FormItem
                            {...formItemLayout1}
                            label={'审批意见'}
                            hasFeedback
                        >
                            <Input.TextArea rows={4} placeholder="" disabled={true} value={reply} style={{ width: '80%' }} />
                        </FormItem> : ''
                    }

                    {/* 待我审批 */}
                    {this.props.listType == 0 ? <FormItem
                        {...formItemLayout1}
                        label={agreeApply ? '请输入审批意见' : '请输入驳回原因'}
                        hasFeedback
                    >
                        {getFieldDecorator('reply', {
                            rules: [{
                                required: !agreeApply,
                                max: 200,
                                message: '请控制在200个字符以内！'
                            }]
                            // initialValue: this.props.listType == 1 ? 123 : null
                        })(
                            <Input.TextArea rows={4} placeholder="回复内容" style={{ width: '80%' }} />
                        )}
                    </FormItem> : ''
                    }
                </Form>
            </Modal>
        )
    }
}
const FormWrapper = Form.create<any>()(DetailPermission);
export default FormWrapper
