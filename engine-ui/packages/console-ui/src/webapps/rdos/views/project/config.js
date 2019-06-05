import React, { Component } from 'react'
import { Link } from 'react-router'
import { connect } from 'react-redux'

import {
    Modal, Tooltip,
    Input, Form, message, Icon,
    Switch
} from 'antd'
import BindProjectModal from './bindProject';

import utils from 'utils'

import { formItemLayout, PROJECT_TYPE } from '../../comm/const'
import Api from '../../api'
import * as ProjectAction from '../../store/modules/project'

const FormItem = Form.Item

function myFrom (props) {
    const { getFieldDecorator } = props.form;
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
                    <Input type="textarea" rows={4} />
                )}
            </FormItem>
        </Form>
    )
}

const DescForm = Form.create()(myFrom)

class ProjectConfig extends Component {
    state = {
        visibleUpdateDesc: false,
        scheduleStatusLoading: false,
        isAllowDownloadLoading: false,
        visibleChangeProduce: false
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
                        dispatch(ProjectAction.getProjects())
                        formEle.resetFields();
                        ctx.setState({ visibleUpdateDesc: false })
                        message.success('项目更新成功！')
                    }
                })
            }
        });
    }

    changeScheduleStatus (checked) {
        const { project, dispatch } = this.props
        this.setState({
            scheduleStatusLoading: true
        })
        Api.updateProjectSchedule({
            status: checked ? 0 : 1
        })
            .then(
                (res) => {
                    this.setState({
                        scheduleStatusLoading: false
                    })
                    if (res.code == 1) {
                        message.success('周期调度状态切换成功！')
                        const newProject = Object.assign({}, project, { scheduleStatus: checked ? 0 : 1 });
                        dispatch(ProjectAction.setProject(newProject))
                        dispatch(ProjectAction.getProjects())
                    }
                }
            )
    }
    changeAllowDownloadStatus (checked) {
        const { project, dispatch } = this.props
        this.setState({
            isAllowDownloadLoading: true
        })
        checked = checked ? 1 : 0;
        Api.updateProjectAllowDownLoad({
            status: checked
        })
            .then(
                (res) => {
                    this.setState({
                        isAllowDownloadLoading: false
                    })
                    if (res.code == 1) {
                        message.success('切换成功！')
                        const newProject = Object.assign({}, project, { isAllowDownload: checked });
                        dispatch(ProjectAction.setProject(newProject))
                        dispatch(ProjectAction.getProjects())
                    }
                }
            )
    }
    changeBindModalVisible (isShow) {
        this.setState({
            visibleChangeProduce: isShow
        })
    }
    renderSubmit (project) {
        switch (project.projectType) {
            case PROJECT_TYPE.COMMON: {
                return (
                    <tr>
                        <td className="t-title">
                            发布目标
                            <Tooltip title="可以选择同一租户下的其他项目作为发布目标，您在数据开发界面中选择发布内容，将所选择的内容发布（迁移）至目标项目，可将本项目作为开发环境，目标项目作为生产环境，保障生产环境的安全稳定。" arrowPointAtCenter>
                                <Icon className="help-doc" type="question-circle-o" />
                            </Tooltip>
                        </td>
                        <td>
                            <a onClick={this.changeBindModalVisible.bind(this, true)}>立即绑定</a>
                        </td>
                    </tr>
                )
            }
            case PROJECT_TYPE.TEST: {
                return (
                    <tr>
                        <td className="t-title">
                            发布目标
                            <Tooltip title="可以选择同一租户下的其他项目作为发布目标，您在数据开发界面中选择发布内容，将所选择的内容发布（迁移）至目标项目，可将本项目作为开发环境，目标项目作为生产环境，保障生产环境的安全稳定。" arrowPointAtCenter>
                                <Icon className="help-doc" type="question-circle-o" />
                            </Tooltip>
                        </td>
                        <td>
                            {project.produceProject}
                        </td>
                    </tr>
                )
            }
            case PROJECT_TYPE.PRO: {
                return (
                    <tr>
                        <td className="t-title">发布源</td>
                        <td>
                            {project.testProject}
                        </td>
                    </tr>
                )
            }
            default: {
                return null;
            }
        }
    }
    render () {
        const { visibleUpdateDesc, scheduleStatusLoading, isAllowDownloadLoading, visibleChangeProduce } = this.state
        let { params, project } = this.props;
        project = project || {};
        const { isAllowDownload, scheduleStatus } = project;
        const isScheduleEnAbled = scheduleStatus == 0;
        const isAllowDownLoadEnAbled = isAllowDownload == 1;
        const adminLength = project && project.adminUsers && project.adminUsers.length;
        const memberLength = project && project.memberUsers && project.memberUsers.length;
        const admins = project && project.adminUsers && project.adminUsers.length > 0
            ? project.adminUsers.map((item, index) => index == adminLength - 1 ? <span key={item.id}>{item.userName}</span> : <span key={item.id}>{item.userName}; </span>) : ''
        const members = project && project.memberUsers && project.memberUsers.length > 0
            ? project.memberUsers.map((item, index) => index == memberLength - 1 ? <span key={item.id}>{item.userName}</span> : <span key={item.id}>{item.userName};</span>) : ''
        const projectIdentifier = project.projectIdentifier;
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
                                {project.projectAlias}
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
                            <tr><td className="t-title">管理员</td><td>{admins}</td></tr>
                            <tr>
                                <td className="t-title">普通成员</td>
                                <td>
                                    {members}
                                    <Link to={`/project/${params.pid}/member`}> 成员管理</Link>
                                </td>
                            </tr>
                            {this.renderSubmit(project)}
                            <tr>
                                <td className="t-title">启动周期调度</td>
                                <td>
                                    <Switch
                                        checkedChildren="开"
                                        unCheckedChildren="关"
                                        disabled={scheduleStatusLoading}
                                        checked={isScheduleEnAbled}
                                        onChange={this.changeScheduleStatus.bind(this)} />
                                </td>
                            </tr>
                            <tr>
                                <td className="t-title">下载SELECT结果</td>
                                <td>
                                    <Switch
                                        checkedChildren="开"
                                        unCheckedChildren="关"
                                        disabled={isAllowDownloadLoading}
                                        checked={isAllowDownLoadEnAbled}
                                        onChange={this.changeAllowDownloadStatus.bind(this)} />
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
                    <DescForm ref={(e) => { this.myForm = e }} {...this.props} />
                </Modal>
                <BindProjectModal
                    visible={visibleChangeProduce}
                    onClose={this.changeBindModalVisible.bind(this, false)}
                />
            </div>
        )
    }
}

export default connect((state) => {
    return {
        project: state.project,
        projects: state.projects
    };
})(ProjectConfig)
