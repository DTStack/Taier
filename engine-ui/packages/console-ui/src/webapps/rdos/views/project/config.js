import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'

import {
    Row, Col, Modal, Card,
    Input, Form, message,
} from 'antd'

import utils from 'utils'

import { formItemLayout } from '../../comm/const'
import Api from '../../api'
import * as ProjectAction from '../../store/modules/project'

const FormItem = Form.Item

function myFrom(props) {
    const { getFieldDecorator } = props.form;
    return (
        <Form>
            <FormItem
                {...formItemLayout}
                label="项目别名"
            >
                {getFieldDecorator('projectAlias', {
                    rules: [{
                        max: 20,
                        message: '项目别名不得超过20个字符！',
                    }],
                    initialValue: props.project ? props.project.projectAlias : '',
                })(
                    <Input placeholder="请输入项目别名" />,
                )}
            </FormItem>
            <FormItem
              {...formItemLayout}
              label="项目描述"
            >
                {getFieldDecorator('projectDesc', {
                    rules: [{
                        max: 200,
                        message: '项目描述请控制在200个字符以内！',
                    }],
                    initialValue: props.project ? props.project.projectDesc : '',
                })(
                    <Input type="textarea" rows={4} />,
                )}
            </FormItem>
        </Form>
    )
}

const DescForm = Form.create()(myFrom)

class ProjectConfig extends Component {

    state = {
        visibleUpdateDesc: false,
    }

    updateProjectDesc = () => {
        const { params, project, dispatch } = this.props
        const ctx = this
        const formEle = this.myForm
        const projectForm = formEle.getFieldsValue()
        projectForm.projectId = parseInt(params.pid, 10)
        
        formEle.validateFields((err) => {
            if (!err) {
                Api.upateProjectInfo(projectForm).then((res) => {
                    if (res.code === 1) {
                        const newProject = Object.assign(project, projectForm)
                        dispatch(ProjectAction.setProject(newProject))
                        ctx.setState({ visibleUpdateDesc: false })
                        message.success('项目更新成功！')
                    }
                })
            }
        });
    }

    render() {
        const { visibleUpdateDesc } = this.state
        const { params, project } = this.props
        const admins = project && project.adminUsers && project.adminUsers.length > 0 ?
            project.adminUsers.map(item => <span key={item.id}>{item.userName}; </span>) : ''
        const members = project && project.memberUsers && project.memberUsers.length > 0 ?
            project.memberUsers.map(item => <span key={item.id}>{item.userName};</span>) : ''
        return (
            <div className="project-config">
                <article className="section">
                    <h1 className="title black" style={{paddingTop: '0'}}>
                        项目配置
                    </h1>
                    <Card>
                        <table className="project-config-table bd">
                            <tbody>
                                <tr><td className="t-title">项目名称</td><td>{project.projectIdentifier}</td></tr>
                                <tr><td className="t-title">项目别名</td><td>
                                    {project.projectAlias || '-'}
                                    &nbsp;
                                    <a onClick={() => { this.setState({ visibleUpdateDesc: true }) }}>
                                        修改
                                    </a>
                                </td></tr>
                                <tr><td className="t-title">创建日期</td><td>{utils.formatDateTime(project.gmtCreate)}</td></tr>
                                <tr><td className="t-title">项目描述</td><td>
                                    {project.projectDesc || ''}
                                    &nbsp;
                                    <a onClick={() => { this.setState({ visibleUpdateDesc: true }) }}>
                                        修改
                                    </a>
                                </td></tr>
                                <tr><td className="t-title">项目管理员</td><td>{admins}</td></tr>
                                <tr>
                                    <td className="t-title">普通成员</td>
                                    <td>
                                        {members}
                                        <Link to={`/project/${params.pid}/member`}> 成员管理</Link>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </Card>
                </article>
                <Modal
                    title="修改项目描述"
                    wrapClassName="vertical-center-modal"
                    visible={visibleUpdateDesc}
                    onOk={this.updateProjectDesc}
                    onCancel={() => this.setState({ visibleUpdateDesc: false })}
                    >
                        <DescForm ref={(e) => { this.myForm = e }} {...this.props} />
                </Modal>
            </div>
        )
    }
}

export default connect((state) => {
    return { project: state.project }
})(ProjectConfig)

