import * as React from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'

import {
    Modal,
    Input, Form, message
} from 'antd'

import utils from 'utils'

import { formItemLayout } from '../../consts'
import Api from '../../api/project'
import * as ProjectAction from '../../actions/project';

const FormItem = Form.Item

function myFrom (props: any) {
    const { getFieldDecorator } = props.form;
    const rowFix = {
        rows: 4
    }
    return (
        <Form>
            <FormItem
                {...formItemLayout}
                label="项目显示名称"
            >
                {getFieldDecorator('projectAlias', {
                    rules: [{
                        max: 20,
                        message: '项目显示名称不得超过20个字符！'
                    }],
                    initialValue: props.project ? props.project.projectAlias : ''
                })(
                    <Input placeholder="请输入项目显示名称" />
                )}
            </FormItem>
            <FormItem
                {...formItemLayout}
                label="项目描述"
            >
                {getFieldDecorator('projectDesc', {
                    rules: [{
                        max: 200,
                        message: '项目描述请控制在200个字符以内！'
                    }],
                    initialValue: props.project ? props.project.projectDesc : ''
                })(
                    <Input type="textarea" {...rowFix} />
                )}
            </FormItem>
        </Form>
    )
}

const DescForm = Form.create<any>()(myFrom as any)

class ProjectConfig extends React.Component<any, any> {
    myForm: any;
    state: any = {
        visibleUpdateDesc: false,
        scheduleStatusLoading: false,
        bindLoading: false,
        visibleChangeProduce: false,
        bindProject: {},
        projectBindList: []
    }
    componentDidMount () {
        const { project, dispatch } = this.props
        dispatch(ProjectAction.getProject(project.id));
    }
    updateProjectDesc = () => {
        const { params, project, dispatch } = this.props
        const ctx = this
        const formEle = this.myForm
        const projectForm = formEle.getFieldsValue()
        projectForm.projectId = parseInt(params.pid, 10)

        formEle.validateFields((err: any) => {
            if (!err) {
                Api.upateProjectInfo(projectForm).then((res: any) => {
                    if (res.code === 1) {
                        const newProject = Object.assign(project, projectForm)
                        dispatch(ProjectAction.setProject(newProject))
                        dispatch(ProjectAction.getProjects())
                        formEle.resetFields();
                        ctx.setState({ visibleUpdateDesc: false })
                        message.success('项目更新成功！')
                    }
                })
            }
        });
    }

    render () {
        const { visibleUpdateDesc } = this.state
        const { params, project = {} } = this.props
        const adminLength = project && project.adminUsers && project.adminUsers.length;
        const memberLength = project && project.memberUsers && project.memberUsers.length;
        const admins = project && project.adminUsers && project.adminUsers.length > 0
            ? project.adminUsers.map((item: any, index: any) => index == adminLength - 1 ? <span key={item.id}>{item.userName}</span> : <span key={item.id}>{item.userName}; </span>) : ''
        const members = project && project.memberUsers && project.memberUsers.length > 0
            ? project.memberUsers.map((item: any, index: any) => index == memberLength - 1 ? <span key={item.id}>{item.userName}</span> : <span key={item.id}>{item.userName};</span>) : ''
        const projectIdentifier = project && project.projectIdentifier;
        return (
            <div className="project-config">
                <h1 className="box-title">
                    项目配置
                </h1>
                <div className="box-card">
                    <table className="project-config-table bd">
                        <tbody>
                            <tr><td className="t-title">项目标识</td><td>{projectIdentifier}</td></tr>
                            <tr><td className="t-title">项目显示名称</td><td>
                                {project && project.projectAlias}
                                &nbsp;
                                <a onClick={() => { this.setState({ visibleUpdateDesc: true }) }}>
                                    修改
                                </a>
                            </td></tr>
                            <tr><td className="t-title">创建日期</td><td>{utils.formatDateTime(project && project.gmtCreate)}</td></tr>
                            <tr><td className="t-title">项目描述</td><td>
                                {(project && project.projectDesc) || ''}
                                &nbsp;
                                <a onClick={() => { this.setState({ visibleUpdateDesc: true }) }}>
                                    修改
                                </a>
                            </td></tr>
                            <tr><td className="t-title">管理员</td><td>{admins}</td></tr>
                            <tr>
                                <td className="t-title">普通成员</td>
                                <td>
                                    {members}
                                    <Link to={`/api/project/${params.pid}/member`}> 成员管理</Link>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <Modal
                    title="修改项目描述"
                    wrapClassName="vertical-center-modal"
                    visible={visibleUpdateDesc}
                    onOk={this.updateProjectDesc}
                    onCancel={() => { this.setState({ visibleUpdateDesc: false }); this.myForm.resetFields(); }}
                >
                    <DescForm ref={(e: any) => { this.myForm = e }} {...this.props} />
                </Modal>
            </div>
        )
    }
}

export default connect((state: any) => {
    return {
        project: state.project,
        projects: state.projects
    };
})(ProjectConfig)
