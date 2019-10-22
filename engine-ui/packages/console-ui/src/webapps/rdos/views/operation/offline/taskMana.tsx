import * as React from 'react'
import { connect } from 'react-redux'
import moment from 'moment'
import { cloneDeep } from 'lodash'

import {
    Table, message,
    Row, Col, Card,
    Button, Select, Form,
    Checkbox, Tabs
} from 'antd'

import utils from 'utils'
import { replaceObjectArrayFiledName } from 'funcs';
import SlidePane from 'widgets/slidePane';
import MultiSearchInput from 'widgets/multiSearchInput';

import Api from '../../../api'
import {
    offlineTaskPeriodFilter,
    SCHEDULE_STATUS,
    PROJECT_TYPE,
    TASK_TYPE
} from '../../../comm/const'

import { TaskTimeType, TaskType } from '../../../components/status'

import PatchDataModal from './patchDataModal'
import TaskFlowView from './taskFlowView'
import TaskRuntime from './taskFlowView/taskRuntime'
import { Circle } from 'widgets/circle'

import {
    workbenchActions
} from '../../../store/modules/offlineTask/offlineAction'

const FormItem = Form.Item
const Option: any = Select.Option
const TabPane = Tabs.TabPane

class OfflineTaskMana extends React.Component<any, any> {
    state: any = {
        tasks: {
            data: []
        },
        loading: false,
        patchDataVisible: false,
        visibleSlidePane: false,
        checkAll: false,
        current: 1, // 当前页
        tabKey: 'taskFlow',
        person: undefined,
        taskName: utils.getParameterByName('tname') ? utils.getParameterByName('tname') : '',
        selectedTask: '',
        patchTargetTask: '', // 补数据对象
        startTime: '',
        endTime: '',
        taskType: '',
        taskPeriodId: '',
        scheduleStatus: '',
        checkVals: [],
        selectedRowKeys: [],
        expandedRowKeys: [],
        searchType: 'fuzzy'
    }

    componentDidMount () {
        if (this.props.project.id !== 0) {
            this.search()
        }
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (project && oldProj.id !== project.id) {
            this.setState({ current: 1, taskName: '', visibleSlidePane: false }, () => {
                this.search()
            })
        }
    }

    getReqParams = () => {
        const {
            taskName, person,
            startTime, endTime, taskType,
            scheduleStatus, current, taskPeriodId, searchType
        } = this.state

        const reqParams: any = {
            currentPage: current || 1
        }
        if (taskName) {
            reqParams.name = taskName
        }
        if (startTime && endTime) {
            reqParams.startTime = startTime
            reqParams.endTime = endTime
        }
        if (person) {
            reqParams.ownerId = person
        }
        if (scheduleStatus) {
            reqParams.scheduleStatus = scheduleStatus
        }
        if (taskType) {
            reqParams.taskType = taskType.join(',')
        }
        if (taskPeriodId) {
            reqParams.taskPeriodId = taskPeriodId.join(',')
        }
        reqParams.searchType = searchType;
        return reqParams;
    }

    search = () => {
        const reqParams = this.getReqParams();
        this.loadTaskList(reqParams)
    }

    loadTaskList (params: any) { // currentPage, pageSize, isMine, status
        const ctx = this
        this.setState({ loading: true })
        const reqParams = Object.assign({
            currentPage: 1,
            pageSize: 20
        }, params)
        Api.queryOfflineTasks(reqParams).then((res: any) => {
            if (res.code === 1) {
                const tableData = res.data.data;
                const expandedRowKeys: any = [];
                replaceObjectArrayFiledName(tableData, 'relatedTasks', 'children');
                for (let i = 0; i < tableData.length; i++) {
                    let task = tableData[i];
                    if (task && task.taskType === TASK_TYPE.WORKFLOW) {
                        if (!task.children) {
                            task.children = [];
                        } else {
                            expandedRowKeys.push(task.id);
                        }
                    }
                }
                ctx.setState({ tasks: res.data, expandedRowKeys: expandedRowKeys });
            }
            this.setState({ loading: false })
        })
    }

    forzenTasks = (mode: any) => {
        const ctx = this
        const selected = this.state.selectedRowKeys

        if (!selected || selected.length <= 0) {
            message.error('您没有选择任何任务！')
            return false;
        }

        Api.forzenTask({
            taskIdList: selected,
            scheduleStatus: mode //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
        }).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ selectedRowKeys: [], checkAll: false })
                ctx.search()
            }
        })
    }

    handleTableChange = (pagination: any, filters: any) => {
        this.setState({
            checkAll: false,
            selectedRowKeys: [],
            current: pagination.current,
            taskType: filters.taskType,
            taskPeriodId: filters.taskPeriodId
        }, this.search)
    }

    clickPatchData = (task: any) => {
        this.setState({
            patchDataVisible: true,
            patchTargetTask: task
        })
    }

    showTask = (task: any) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        })
    }

    clickMenu = (target: any) => {
        const task = target.item.props.value
        if (target.key === 'edit') {
            this.props.goToTaskDev(task.id)
        }
    }

    changePerson = (target: any) => { // 责任人变更
        const { user } = this.props
        const { checkVals } = this.state
        const setVals: any = {
            person: target,
            current: 1
        }
        if (target == user.id) {
            if (checkVals.indexOf('person') === -1) {
                checkVals.push('person')
            }
        } else {
            const i = checkVals.indexOf('person');
            if (i > -1) {
                checkVals.splice(i, 1)
            }
        }
        setVals.checkVals = [...checkVals]
        this.setState(setVals, this.search)
    }

    changeTaskName = (value: any) => { // 任务名变更
        this.setState({ taskName: value })
    }

    changeSearchType = (type: any) => {
        this.setState({ searchType: type });
        this.onSearchByTaskName()
    }

    onSearchByTaskName = () => {
        this.setState({
            current: 1
        }, this.search)
    }

    onTabChange = (tabKey: any) => {
        this.setState({
            tabKey
        })
    }

    onCheckAllChange = (e: any) => {
        let selectedRowKeys: any = []
        if (e.target.checked) {
            selectedRowKeys = this.state.tasks.data.map((item: any) => item.id)
        }

        this.setState({
            selectedRowKeys,
            checkAll: e.target.checked
        })
    }

    onCheckChange = (checkedList: any) => {
        const { user } = this.props;
        const { person } = this.state;
        const conditions: any = {
            startTime: '',
            endTime: '',
            scheduleStatus: '',
            checkVals: checkedList,
            current: 1
        };
        checkedList.forEach((item: any) => {
            if (item === 'person') {
                conditions.person = `${user.id}`;
            } else if (item === 'todayUpdate') {
                conditions.startTime = moment().set({
                    'hour': 0,
                    'minute': 0,
                    'second': 0
                }).unix()
                conditions.endTime = moment().set({
                    'hour': 23,
                    'minute': 59,
                    'second': 59
                }).unix()
            } else if (item === 'stopped') {
                conditions.scheduleStatus = 2; // 任务状态(1:正常 2：冻结)
            }
        })
        // 清理掉责任人信息
        if (!conditions.person && person === `${user.id}`) {
            conditions.person = '';
        }
        this.setState(conditions, this.search)
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            selectedTask: null
        })
    }

    initTaskColumns = () => {
        const isPro = this.props.project.projectType == PROJECT_TYPE.PRO;
        const pre = isPro ? '发布' : '提交'
        const { taskTypeFilter } = this.props;
        const { taskPeriodId, taskType } = this.state;

        return [{
            title: '任务名称',
            dataIndex: 'name',
            key: 'name',
            render: (text: any, record: any) => {
                const content = record.isDeleted === 1 ? `${text} (已删除)`
                    : <a onClick={() => { this.showTask(record) }}>
                        {record.name + (record.scheduleStatus == SCHEDULE_STATUS.STOPPED ? ' (已冻结)' : '')}
                    </a>
                return content;
            }
        }, {
            title: pre + '时间',
            dataIndex: 'gmtModified',
            key: 'gmtModified',
            render: (text: any) => {
                return <span>{utils.formatDateTime(text)}</span>
            }
        }, {
            title: '任务类型',
            dataIndex: 'taskType',
            key: 'taskType',
            render: (text: any) => {
                return <TaskType value={text} />
            },
            filters: taskTypeFilter,
            filteredValue: taskType
        }, {
            title: '调度周期',
            dataIndex: 'taskPeriodId',
            key: 'taskPeriodId',
            render: (text: any) => {
                return <TaskTimeType value={text} />
            },
            filters: offlineTaskPeriodFilter,
            filteredValue: taskPeriodId
        }, {
            title: '责任人',
            dataIndex: 'userName',
            key: 'userName',
            render: (text: any, record: any) => {
                return <span>{record.ownerUser && record.ownerUser.userName}</span>
            }
        }, {
            title: '操作',
            key: 'operation',
            width: 120,
            render: (text: any, record: any) => {
                return (
                    <span>
                        <a onClick={() => { this.clickPatchData(record) }}>补数据</a>
                        <span className="ant-divider"></span>
                        <a onClick={() => { this.props.goToTaskDev(record.id) }}>修改</a>
                    </span>
                )
            }
        }]
    }

    onExpandRows = (expandedRows: any) => {
        this.setState({ expandedRowKeys: expandedRows })
    }

    onExpand = (expanded: any, record: any) => {
        if (expanded) {
            const { tasks } = this.state;
            let newTasks = cloneDeep(tasks);
            const reqParams = this.getReqParams();
            reqParams.taskId = record.id;
            Api.getRelatedTasks(reqParams).then((res: any) => {
                if (res.code == 1) {
                    const index = newTasks.data.findIndex((task: any) => {
                        return task.id === record.id
                    });
                    if (index || index == 0) {
                        newTasks.data[index] = {
                            ...res.data,
                            children: res.data.relatedTasks,
                            relatedTasks: undefined
                        };
                    }
                    this.setState({
                        tasks: newTasks
                    })
                }
            })
        } else {
            console.log('record')
        }
    }

    tableFooter = (currentPageData: any) => {
        return (
            <Row>
                <Col className="inline" style={{ padding: '15px 10px 10px 23px' }}>
                    <Checkbox
                        checked={this.state.checkAll}
                        onChange={this.onCheckAllChange}
                    >
                    </Checkbox>
                </Col>
                <Col className="inline" style={{ paddingLeft: '15px' }}>
                    <Button
                        size="small"
                        type="primary"
                        onClick={this.forzenTasks.bind(this, 2)}
                    >
                        冻结
                    </Button>
                    <Button
                        size="small"
                        onClick={this.forzenTasks.bind(this, 1)}
                    >
                        解冻
                    </Button>
                </Col>
            </Row>
        )
    }

    render () {
        const { projectUsers, project } = this.props
        const {
            tasks, patchDataVisible, selectedTask, person, checkVals, patchTargetTask,
            current, taskName, visibleSlidePane, selectedRowKeys, tabKey, searchType
        } = this.state;
        const isPro = project.projectType == PROJECT_TYPE.PRO;
        const isTest = project.projectType == PROJECT_TYPE.TEST;
        const userItems = projectUsers && projectUsers.length > 0
            ? projectUsers.map((item: any) => {
                return (<Option key={item.userId} value={`${item.userId}`} name={item.user.userName}>
                    {item.user.userName}
                </Option>)
            }) : []

        const pagination: any = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            current
        };

        const rowSelection: any = {
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                this.setState({
                    selectedRowKeys
                })
            },
            selectedRowKeys: selectedRowKeys
        };

        console.log(this.state);
        return (
            <div>
                {isTest && (
                    <h1 className="box-title" style={{ lineHeight: '50px' }}>
                        <div style={{ marginTop: '5px' }}>
                            <span className="ope-statistics">
                                <span style={{ color: '#2E3943' }}>
                                    <Circle style={{ background: '#2E3943' }} />&nbsp;
                            任务总数: &nbsp;{tasks.totalCount || 0}
                                </span>&nbsp;
                                <span style={{ color: '#F5A623' }}>
                                    <Circle style={{ background: '#F5A623 ' }} />&nbsp;
                            已发布: &nbsp;{tasks.publishedTasks || 0}
                                </span>&nbsp;
                            </span>
                        </div>
                    </h1>
                )}
                <div className={`m-card ${!isTest ? 'box-1' : 'box-2'} task-manage`}>
                    <Card
                        noHovering
                        bordered={false}
                        loading={false}
                        title={
                            <Form
                                layout="inline"
                                className="m-form-inline"
                            >
                                <FormItem label="">
                                    <MultiSearchInput
                                        placeholder="按任务名称"
                                        style={{ width: 250, height: '26px' }}
                                        value={taskName}
                                        searchType={searchType}
                                        onChange={this.changeTaskName}
                                        onTypeChange={this.changeSearchType}
                                        onSearch={this.onSearchByTaskName}
                                    />
                                </FormItem>
                                <FormItem
                                    label="责任人"
                                >
                                    <Select
                                        allowClear
                                        showSearch
                                        size="default"
                                        style={{ width: 126 }}
                                        placeholder="责任人"
                                        optionFilterProp="name"
                                        value={person}
                                        onChange={this.changePerson}
                                    >
                                        {userItems}
                                    </Select>
                                </FormItem>
                                <FormItem>
                                    <Checkbox.Group value={checkVals} onChange={this.onCheckChange} >
                                        <Checkbox value="person" className="select-task">我的任务</Checkbox>
                                        <Checkbox value="todayUpdate" className="select-task">今日修改的任务</Checkbox>
                                        <Checkbox value="stopped" className="select-task">冻结的任务</Checkbox>
                                    </Checkbox.Group>
                                </FormItem>
                            </Form>
                        }
                    >
                        <Table
                            key={`task-list${tasks.data && tasks.data.length}`}
                            rowKey="id"
                            rowClassName={
                                (record: any, index: any) => {
                                    if (this.state.selectedTask && this.state.selectedTask.id == record.id) {
                                        return 'row-select'
                                    } else {
                                        return '';
                                    }
                                }
                            }
                            style={{ marginTop: '1px' }}
                            className={`dt-ant-table dt-ant-table--border rdos-ant-table-placeholder border-table ${isPro ? 'full-screen-table-90' : 'full-screen-table-120'}`}
                            expandedRowKeys={this.state.expandedRowKeys}
                            pagination={pagination}
                            rowSelection={rowSelection}
                            loading={this.state.loading}
                            columns={this.initTaskColumns()}
                            dataSource={tasks.data || []}
                            onChange={this.handleTableChange}
                            onExpand={this.onExpand}
                            onExpandedRowsChange={this.onExpandRows}
                            footer={this.tableFooter}
                        />
                        <SlidePane
                            className="m-tabs bd-top bd-right m-slide-pane"
                            onClose={this.closeSlidePane}
                            visible={visibleSlidePane}
                            style={{ right: '0px', width: '60%', height: '100%', position: 'fixed', minHeight: '600px', paddingTop: '50px' }}
                        >
                            <Tabs className='c-taskMana__slidePane__tabs' animated={false} onChange={this.onTabChange} tabBarStyle={{ zIndex: 3 }} style={{ height: '100%' }}>
                                <TabPane tab="依赖视图" key="taskFlow">
                                    <TaskFlowView
                                        reload={this.search}
                                        key={`taskGraph-${selectedTask && selectedTask.id}-${tabKey}`}
                                        visibleSlidePane={visibleSlidePane}
                                        goToTaskDev={this.props.goToTaskDev}
                                        clickPatchData={this.clickPatchData}
                                        tabData={selectedTask}
                                    />
                                </TabPane>
                                <TabPane tab="运行报告" key="runTime">
                                    <TaskRuntime
                                        visibleSlidePane={visibleSlidePane}
                                        tabData={selectedTask}
                                        key={`taskRunTime-${selectedTask && selectedTask.id}-${tabKey}`}
                                    />
                                </TabPane>
                            </Tabs>
                        </SlidePane>
                    </Card>
                </div>
                <PatchDataModal
                    visible={patchDataVisible}
                    task={patchTargetTask}
                    handCancel={() => { this.setState({ patchDataVisible: false, patchTargetTask: '' }) }}
                />
            </div>
        )
    }
}
export default connect((state: any) => {
    return {
        project: state.project,
        projectUsers: state.projectUsers,
        workbench: state.workbench,
        user: state.user,
        taskTypeFilter: state.offlineTask.comm.taskTypeFilter
    }
}, (dispatch: any) => {
    const actions = workbenchActions(dispatch)
    return {
        goToTaskDev: (id: any) => {
            actions.openTaskInDev(id)
        }
    }
})(OfflineTaskMana)
