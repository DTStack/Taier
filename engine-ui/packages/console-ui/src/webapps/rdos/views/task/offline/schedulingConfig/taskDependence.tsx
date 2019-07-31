import * as React from 'react';
import { connect } from 'react-redux';
import { get } from 'lodash';

import {
    Row,
    Col,
    Form,
    Button,
    Table,
    Select
} from 'antd';
import TaskSelector from './taskSelector';
import RecommentTaskModal from '../recommentTaskModal';

import { TASK_TYPE } from '../../../../comm/const';
import ajax from '../../../../api';

const FormItem = Form.Item;
const Option = Select.Option

const formItemLayout: any = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 4 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
    }
}

@(connect((state: any) as any) => {
    return {
        tenant: state.tenant,
        project: state.project
    }
})
class TaskDependence extends React.Component<any, any> {
    state: any = {
        loading: false,
        recommentTaskModalVisible: false,
        recommentTaskList: [],
        projectList: [],
        tenantId: null,
        projectId: null
    }
    componentDidMount () {
        this.initTenant();
    }
    initTenant () {
        const tenantId = get(this.props, 'tenant.currentTenant.tenantId');
        if (tenantId) {
            this.setState({
                tenantId
            }, this.loadProjectList.bind(this));
        }
    }
    showRecommentTask () {
        const { tabData } = this.props;
        this.setState({
            loading: true
        })
        ajax.getRecommentTask({
            taskId: tabData.id
        })
            .then(
                (res: any) => {
                    this.setState({
                        loading: false
                    })
                    if (res.code == 1) {
                        this.setState({
                            recommentTaskModalVisible: true,
                            recommentTaskList: res.data
                        })
                    }
                }
            )
    }
    recommentTaskClose () {
        this.setState({
            recommentTaskModalVisible: false
        })
    }
    goEdit(task: any) {
        this.props.getTaskDetail(task.id)
    }
    initColumn () {
        const { project, tenant } = this.props;
        const currentTenantName = tenant.currentTenant.tenantName;
        const currentProjectName = project.projectName;
        return [
            {
                title: '租户',
                dataIndex: 'tenantName',
                key: 'tenantName',
                render(tenantName: any) {
                    if (tenantName) {
                        return tenantName;
                    } else {
                        return currentTenantName;
                    }
                }
            },
            {
                title: '项目标识',
                dataIndex: 'projectName',
                key: 'projectName',
                render(projectName: any) {
                    if (projectName) {
                        return projectName;
                    } else {
                        return currentProjectName;
                    }
                }
            },
            {
                title: '任务名称',
                dataIndex: 'name',
                key: 'name',
                render: (text: any, record: any) => {
                    if (record.tenantName == currentTenantName && record.projectName == currentProjectName) {
                        return (
                            <a
                                href="javascript:void(0)"
                                onClick={this.goEdit.bind(this, record)}
                            >{text}</a>
                        )
                    } else {
                        return text;
                    }
                }
            },
            {
                title: '责任人',
                dataIndex: 'createUser.userName',
                key: 'createUser.userName'
            },
            {
                title: '操作',
                key: 'action',
                render: (text: any, record: any) => (
                    <span>
                        <a href="javascript:void(0)"
                            onClick={() => { this.props.handleDelVOS(record) }}
                        >删除</a>
                    </span>
                )
            }
        ];
    }
    recommentTaskChoose(list: any) {
        for (let i = 0; i < list.length; i++) {
            this.props.handleAddVOS(list[i]);
        }
        this.setState({
            recommentTaskModalVisible: false
        })
    }
    onSelectTenant(value: any) {
        this.setState({
            tenantId: value,
            projectList: [],
            projectId: null
        }, this.loadProjectList.bind(this));
    }
    async loadProjectList () {
        const { tenantId } = this.state;
        let res = await ajax.getProjectByTenant({ searchTenantId: tenantId });
        if (res && res.code == 1) {
            this.setState({
                projectList: res.data || []
            }, this.setDefaultProject);
        }
    }
    setDefaultProject = () => {
        const { project, tenant } = this.props;
        const { tenantId } = this.state;
        if (tenant.currentTenant.tenantId == tenantId) {
            this.setState({
                projectId: project.id
            });
        }
    }
    onSelectProject(value: any) {
        this.setState({
            projectId: value
        });
    }
    render () {
        const { tabData, handleAddVOS, tenant } = this.props;
        const { loading, recommentTaskModalVisible, recommentTaskList, tenantId, projectId, projectList = [] } = this.state;
        const isSql = tabData.taskType == TASK_TYPE.SQL;
        return (
            <React.Fragment>
                {isSql && (<Button
                    loading={loading}
                    type="primary"
                    style={{ marginBottom: '20px', marginLeft: '12px' }}
                    onClick={this.showRecommentTask.bind(this)}>
                    自动推荐
                </Button>)
                }
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="租户"
                    >
                        <Select value={tenantId} onSelect={this.onSelectTenant.bind(this)}>
                            {tenant.tenantList.map((tenantItem: any) => {
                                return <Option key={tenantItem.tenantId} value={tenantItem.tenantId}>{tenantItem.tenantName}</Option>
                            })}
                        </Select>
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="项目"
                    >
                        <Select value={projectId} onSelect={this.onSelectProject.bind(this)}>
                            {projectList.map((projectItem: any) => {
                                return <Option key={projectItem.projectId} value={projectItem.projectId}>{projectItem.projectAlias}</Option>
                            })}
                        </Select>
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="上游任务"
                    >
                        <TaskSelector
                            onSelect={handleAddVOS}
                            taskId={tabData.id}
                            projectId={projectId}
                        />
                    </FormItem>
                </Form>
                {
                    tabData.taskVOS && tabData.taskVOS.length > 0
                        ? <Row>
                            <Col>
                                <Table
                                    className="dt-ant-table dt-ant-table--border"
                                    columns={this.initColumn()}
                                    bordered={false}
                                    dataSource={tabData.taskVOS}
                                    rowKey={(record: any) => record.id}
                                />
                            </Col>
                        </Row> : ''
                }
                <RecommentTaskModal
                    visible={recommentTaskModalVisible}
                    taskList={recommentTaskList}
                    onOk={this.recommentTaskChoose.bind(this)}
                    onCancel={this.recommentTaskClose.bind(this)}
                    existTask={tabData.taskVOS}
                />
            </React.Fragment>
        )
    }
}
export default TaskDependence;
