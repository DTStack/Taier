import React, { Component } from 'react'
import {
    Row, Col, Button, message
} from 'antd'

import utils from 'utils'

import RoleForm from './form'
import Api from '../../../api'
import GoBack from '../../../components/go-back'
import { formItemLayout } from '../../../consts'
import { AppName } from '../../../components/display'

export default class RoleAdd extends Component {
    state = {
        app: utils.getParameterByName('app')
    }

    goIndex = () => {
        this.props.router.go(-1)
    }

    submit = () => {
        const ctx = this
        const app = this.state.app

        ctx.form.validateFieldsAndScroll((err, roleData) => {
            if (!err) {
                roleData.roleType = 1; // 表示功能权限类型，还有数据类型权限
                Api.updateRole(app, roleData).then((res) => {
                    if (res.code === 1) {
                        message.success('创建角色成功！')
                        this.goIndex()
                    }
                })
            }
        })
    }

    render () {
        const app = this.state.app

        return (
            <div className="box-1">
                <div className="box-card">
                    <h1 className="card-title"><GoBack /> 创建 {AppName(app)}角色</h1>
                    <article className="section">
                        <RoleForm key="add-role" ref={(e) => this.form = e} isDisabled={false} />
                        <Row>
                            <Col {...formItemLayout.labelCol}></Col>
                            <Col {...formItemLayout.wrapperCol}>
                                <Button type="primary" onClick={this.submit}>确认添加</Button>
                                <Button style={{ marginLeft: '20px' }} onClick={this.goIndex}>取消</Button>
                            </Col>
                        </Row>
                    </article>
                </div>
            </div>
        )
    }
}
