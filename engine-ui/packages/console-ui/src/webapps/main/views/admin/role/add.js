import React, { Component } from 'react'
import { 
    Row, Col, Button,
    Card, message,
 } from 'antd'

 import GoBack from 'widgets/go-back'

 import Api from '../../../api'
 import { formItemLayout } from '../../../comm/const'
 import RoleForm from './form'
 
export default class RoleAdd extends Component {

    goIndex = () => {
        this.props.router.go(-1)
    }

    submit = () => {
        const ctx = this
        ctx.form.validateFieldsAndScroll((err, roleData) => {
            if (!err) {
                roleData.roleType = 1; // 表示功能权限类型，还有数据类型权限
                Api.updateRole(roleData).then((res) => {
                    if (res.code === 1) {
                        message.success('创建角色成功！')
                        this.goIndex()
                    }
                })
            }
        })
    }

    render() {
        const extra = <GoBack style={{marginTop: '10px'}} icon="rollback" size="small" />

        return (
            <div className="project-member">
                <Card title="创建角色" extra={extra}>
                    <article className="section">
                        <RoleForm key="add-role" ref={(e) => this.form = e} />
                        <Row>
                            <Col {...formItemLayout.labelCol}></Col>
                            <Col {...formItemLayout.wrapperCol}>
                                <Button type="primary" onClick={this.submit}>确认添加</Button>
                                <Button style={{marginLeft: '20px'}} onClick={this.goIndex}>取消</Button>
                            </Col>
                        </Row>
                    </article>
                </Card>
            </div>
        )
    }
}

