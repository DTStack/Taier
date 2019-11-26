import * as React from 'react'
import {
    Row, Col, Button, message
} from 'antd'

import GoBack from 'main/components/go-back'

import Api from '../../../api/project'
import { formItemLayout } from '../../../consts'
import RoleForm from './form'

export default class RoleAdd extends React.Component<any, any> {
    form: any;
    goIndex = () => {
        this.props.router.go(-1)
    }

    submit = () => {
        const ctx = this
        ctx.form.validateFieldsAndScroll((err: any, roleData: any) => {
            if (!err) {
                roleData.roleType = 1; // 表示功能权限类型，还有数据类型权限
                Api.updateRole(roleData).then((res: any) => {
                    if (res.code === 1) {
                        message.success('创建角色成功！')
                        this.goIndex()
                    }
                })
            }
        })
    }

    render () {
        return (
            <div className="box-1">
                <div className="box-card">
                    <h1 className="card-title flex-middle"><GoBack type="textButton" /> 创建角色</h1>
                    <article className="section">
                        <RoleForm key="add-role" ref={(e: any) => this.form = e} isDisabled={false} />
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
