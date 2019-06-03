import React from 'react';
import { cloneDeep, get } from 'lodash';
import { connect } from 'react-redux';

import { Modal, Form, message, Select, Row, Col } from 'antd';

import { formItemLayout, PROJECT_TYPE } from '../../comm/const'
import Api from '../../api';
import * as ProjectAction from '../../store/modules/project'

const FormItem = Form.Item;
const Option = Select.Option;

@connect(state => {
    return {
        project: state.project,
        tenant: state.tenant
    }
})
class BindProjectModal extends React.Component {
    state = {
        bindLoading: false,
        projectBindList: []
    }
    getProjectList (value) {
        const { project, form } = this.props;
        const targetTenantId = value || form.getFieldValue('targetTenantId');
        Api.getBindingProjectList({
            projectAlias: project.projectAlias,
            targetTenantId
        }).then(
            (res) => {
                if (res && res.code == 1) {
                    this.setState({
                        projectBindList: res.data
                    })
                }
            }
        )
    }
    changeTenantId = (value) => {
        this.getProjectList(value);
        this.props.form.resetFields(['produceProjectId']);
    }
    bindProject () {
        const { projectBindList = [] } = this.state;
        const { project, dispatch, form } = this.props;
        form.validateFields((err, values) => {
            if (!err) {
                const selectProject = projectBindList.filter((project) => {
                    return project.id == values.produceProjectId;
                }) || {};
                Modal.confirm({
                    title: '确认绑定发布目标',
                    content: (<div style={{ color: 'ff0000', fontWeight: 'bold' }}>
                        <p>是否确定将{selectProject.name}项目指定为发布目标？</p>
                        <p>此配置不可逆，确认后不可修改</p>
                    </div>),
                    iconType: 'exclamation-circle',
                    onOk: () => {
                        this.setState({
                            bindLoading: true
                        })
                        Api.bindProductionProject(values).then(
                            (res) => {
                                this.setState({
                                    bindLoading: false
                                })
                                if (res.code == 1) {
                                    message.success('绑定成功！')
                                    const newProject = cloneDeep(Object.assign(project,
                                        {
                                            produceProject: selectProject.name,
                                            produceProjectId: selectProject.id,
                                            projectType: PROJECT_TYPE.TEST
                                        }
                                    ))
                                    dispatch(ProjectAction.setProject(newProject))
                                    dispatch(ProjectAction.getProjects())
                                    this.closeModal();
                                }
                            }
                        )
                    }
                });
            }
        })
    }
    closeModal = () => {
        this.props.onClose();
        this.props.form.resetFields();
    }
    render () {
        const { visible, form, tenant } = this.props;
        const { bindLoading, projectBindList } = this.state;
        const tenantList = tenant.tenantList || [];
        const currentTenant = tenant.currentTenant;
        return (
            <Modal
                title="绑定发布目标"
                visible={visible}
                onOk={this.bindProject.bind(this)}
                onCancel={this.closeModal}
                confirmLoading={bindLoading}
            >
                <Form>
                    <FormItem
                        label="租户"
                        {...formItemLayout}
                    >
                        {form.getFieldDecorator('targetTenantId', {
                            rules: [{
                                required: true,
                                message: '请选择租户'
                            }],
                            initialValue: get(currentTenant, 'targetTenantId')
                        })(
                            <Select
                                placeholder="请选择租户"
                                style={{ width: '100%' }}
                                showSearch
                                optionFilterProp="children"
                                onSelect={this.changeTenantId}
                            >
                                {tenantList.map(
                                    (tenant) => {
                                        return <Option
                                            key={tenant.tenantId}
                                            value={tenant.tenantId}>
                                            {tenant.tenantName}
                                        </Option>
                                    }
                                ).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                    <FormItem
                        label="指定发布目标"
                        {...formItemLayout}
                    >
                        {form.getFieldDecorator('produceProjectId', {
                            rules: [{
                                required: true,
                                message: '请选择发布目标'
                            }]
                        })(
                            <Select
                                placeholder="请选择发布项目"
                                style={{ width: '100%' }}
                                showSearch
                                optionFilterProp="children"
                            >
                                {projectBindList.map(
                                    (project) => {
                                        return <Option
                                            key={project.id}
                                            value={project.id}>
                                            {project.projectAlias}
                                        </Option>
                                    }
                                ).filter(Boolean)}
                            </Select>
                        )}
                    </FormItem>
                </Form>
                <Row>
                    <Col offset={1} span={23}>
                        <p style={{ color: '#ff0000', fontWeight: 'bold' }}>此配置不可逆，请确认后操作</p>
                        <p>可以选择其他项目作为发布目标，您在数据开发界面中选择发布内容，将所选择的内容发布（迁移）至目标项目，可将本项目作为开发环境，目标项目作为生产环境，保障生产环境的安全稳定。</p>
                        <p>发布目标的项目必须是空的，不能包含任何的任务、资源、函数、表。</p>
                    </Col>
                </Row>
            </Modal>
        )
    }
}

export default Form.create()(BindProjectModal);
