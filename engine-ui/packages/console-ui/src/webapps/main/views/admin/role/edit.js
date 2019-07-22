import React, { Component } from 'react'
import { assign } from 'lodash'
import {
    Row, Col, Button, message, Spin
} from 'antd'

import utils from 'utils'

import Api from '../../../api'
import { formItemLayout } from '../../../consts'
import GoBack from '../../../components/go-back'
import { AppName } from '../../../components/display'

import RoleForm from './form'

export default class RoleEdit extends Component {
    state = {
        roleInfo: {},
        loading: false,
        app: utils.getParameterByName('app')
    }

    componentDidMount () {
        this.getRoleInfo()
    }

    goIndex = () => {
        this.props.router.go(-1)
    }

    submit = () => {
        const ctx = this
        const app = this.state.app

        ctx.form.validateFieldsAndScroll((err, roleData) => {
            if (!err) {
                const updateData = assign(this.state.roleInfo, roleData)

                Api.updateRole(app, updateData).then((res) => {
                    if (res.code === 1) {
                        message.success('角色更新成功！')
                        ctx.goIndex()
                    }
                })
            }
        })
    }

    getRoleInfo = () => {
        const ctx = this
        const app = this.state.app
        const { params } = ctx.props

        ctx.setState({ loading: true })
        Api.getRoleInfo(app, { roleId: params.roleId }).then(res => {
            if (res.code === 1) {
                ctx.setState({ roleInfo: res.data, loading: false })
            }
        })
    }

    render () {
        const { app, roleInfo } = this.state;

        return (
            <div className="box-1">
                <div className="box-card">
                    <h1 className="card-title"><GoBack /> 查看 {AppName(app)}角色</h1>
                    <Spin tip="Loading..." spinning={this.state.loading}>
                        <article className="section">
                            <RoleForm
                                roleInfo={roleInfo}
                                ref={(e) => this.form = e}
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
                </div>
            </div>
        )
    }
}
