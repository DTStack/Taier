import * as React from 'react'
import { assign } from 'lodash'
import {
    Row, Col, Button,
    message, Spin
} from 'antd'

import Api from '../../../api'
import GoBack from 'main/components/go-back'
import { formItemLayout } from '../../../comm/const'

import RoleForm from './form'

export default class RoleEdit extends React.Component<any, any> {
    form: any;
    state: any = {
        roleInfo: {},
        loading: false
    }

    componentDidMount () {
        this.loadRolePermission()
    }

    goIndex = () => {
        this.props.router.go(-1)
    }

    submit = () => {
        const ctx = this
        ctx.form.validateFieldsAndScroll((err: any, roleData: any) => {
            if (!err) {
                const updateData = assign(this.state.roleInfo, roleData)
                Api.updateRole(updateData).then((res: any) => {
                    if (res.code === 1) {
                        message.success('角色更新成功！')
                        ctx.goIndex()
                    }
                })
            }
        })
    }

    loadRolePermission = () => {
        const ctx = this
        const { params } = ctx.props
        ctx.setState({ loading: true })
        Api.getRoleInfo({ roleId: params.roleId }).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ roleInfo: res.data, loading: false })
            }
        })
    }

    render () {
        return (
            <div className="box-1">
                <div className="box-card full-screen-table-40">
                    <h1 className="card-title flex-middle"><GoBack type="textButton" /> 查看角色</h1>
                    <article title="查看角色">
                        <Spin tip="Loading..." spinning={this.state.loading}>
                            <article className="section">
                                <RoleForm
                                    key="edit-role"
                                    roleInfo={this.state.roleInfo}
                                    ref={(e: any) => this.form = e}
                                    isDisabled={true}
                                />
                                <Row>
                                    <Col {...formItemLayout.labelCol}></Col>
                                    <Col {...formItemLayout.wrapperCol}>
                                        <Button type="primary" disabled onClick={this.submit}>确认更新</Button>
                                        <Button style={{ marginLeft: '20px' }} onClick={this.goIndex}>取消</Button>
                                    </Col>
                                </Row>
                            </article>
                        </Spin>
                    </article>
                </div>
            </div>
        )
    }
}
