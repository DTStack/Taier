import * as React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { cloneDeep } from 'lodash';
import { Table, message, Select, Form, Checkbox, Tabs,
    Pagination, Col, Button } from 'antd';

import utils from 'dt-common/src/utils';
import { replaceObjectArrayFiledName } from 'dt-common/src/funcs';
import { SlidePane, MultiSearchInput } from 'dt-react-component';

import PatchDataModal from './patchDataModal';
import TaskFlowView from './taskFlowView';

import { goToTaskDev } from './hlep';
import Api from '../../../api/operation';
import { offlineTaskPeriodFilter, SCHEDULE_STATUS,
    PROJECT_TYPE, TASK_TYPE } from '../../../consts/comm';
import { APPS_TYPE } from '../../../consts';
import { TaskTimeType, TaskType } from '../../../components/status';
import { getProjectList } from '../../../actions/operation';

const FormItem = Form.Item;
const Option: any = Select.Option;
const TabPane = Tabs.TabPane;

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
        pageSize: 20,
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
        searchType: 'fuzzy',
        appType: APPS_TYPE.INDEX,
        projectId: ''
    };

    componentDidMount () {
        const { appType, pid, taskId } = this.props.router.location?.query ?? {}
        const params = { appType: appType ?? APPS_TYPE.INDEX, projectId: pid ?? '' }
        this.setState({ ...params }, () => {
            this.search()
            this.getTaskTypesX()
            this.getProjectList()
            if (taskId) this.showTask({ ...params, id: taskId })
        })
    }

    getReqParams = () => {
        const {
            taskName,
            person,
            startTime,
            endTime,
            taskType,
            scheduleStatus,
            current,
            taskPeriodId,
            searchType,
            appType,
            projectId
            // taskTypeFilter
        } = this.state;

        const reqParams: any = {
            currentPage: current || 1
        };
        reqParams.appType = appType;
        reqParams.projectId = projectId;
        if (taskName) {
            reqParams.name = taskName;
        }
        if (startTime && endTime) {
            reqParams.startTime = startTime;
            reqParams.endTime = endTime;
        }
        if (person) {
            reqParams.ownerId = person;
        }
        if (scheduleStatus) {
            reqParams.scheduleStatus = scheduleStatus;
        }
        if (taskType) {
            reqParams.taskType = taskType.join(',');
        }
        if (taskPeriodId) {
            reqParams.taskPeriodId = taskPeriodId.join(',');
        }
        reqParams.searchType = searchType;
        return reqParams;
    };

    search = () => {
        const reqParams = this.getReqParams();
        this.loadTaskList(reqParams);
    };

    getTaskTypesX = () => {
        const ctx = this;
        let parmms = {
            appType: ctx.state.appType,
            projectId: ctx.state.projectId
        }
        Api.getTaskTypesX(parmms).then((res: any) => {
            if (res.code === 1) {
                const taskTypes = res.data;
                let taskTypeFilter = taskTypes && taskTypes.filter((item) => item.value === 'Shell' || item.value === 'ImpalaSQL').map((type: any) => {
                    return {
                        value: type.key,
                        id: type.key,
                        text: type.value
                    }
                });
                this.setState({ taskTypeFilter: taskTypeFilter });
            }
        });
    }

    getProjectList = (value?: string) => {
        const { dispatch } = this.props
        const { appType, projectId } = this.state
        dispatch(getProjectList({ name: value ?? '', appType, projectId }))
    }

    loadTaskList (params: any) {
        // currentPage, pageSize, isMine, status
        const ctx = this;
        const { pageSize, current, appType, projectId } = this.state;
        this.setState({ loading: true });
        const reqParams = Object.assign(
            {
                currentPage: current || 1,
                pageSize: pageSize || 20,
                projectId,
                appType
            },
            params
        );
        Api.queryOfflineTasks(reqParams).then((res: any) => {
            if (res.code === 1) {
                const tableData = res.data.pageResult.data;
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
                ctx.setState({ tasks: res.data.pageResult, expandedRowKeys: expandedRowKeys });
            }
            this.setState({ loading: false });
        });
    }

    forzenTasks = (mode: any) => {
        const ctx = this;
        const selected = this.state.selectedRowKeys;
        if (!selected || selected.length <= 0) {
            message.error('您没有选择任何任务！');
            return false;
        }

        Api.forzenTask({
            taskIdList: selected,
            scheduleStatus: mode,
            appType: ctx.state.appType, //  1正常调度, 2暂停 NORMAL(1), PAUSE(2),
            projectId: ctx.state.projectId
        }).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ selectedRowKeys: [], checkAll: false });
                ctx.search();
            }
        });
    };

    handleTableChange = (pagination: any, filters: any) => {
        const { current, pageSize } = this.state;
        this.setState(
            {
                checkAll: false,
                selectedRowKeys: [],
                current: current === pagination.current ? 1 : pagination.current,
                pageSize: pagination.pageSize || pageSize,
                taskType: filters.taskType,
                taskPeriodId: filters.taskPeriodId
            },
            this.search
        );
    };

    clickPatchData = (task: any) => {
        this.setState({
            patchDataVisible: true,
            patchTargetTask: task
        });
    };

    showTask = (task: any) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        });
    };

    changePerson = (target: any) => {
        // 责任人变更
        // const { user } = this.props;
        const { checkVals } = this.state;
        const setVals: any = {
            person: target,
            current: 1,
            appType: this.state.appType,
            projectId: this.state.projectId
        };
        if (target == utils.getCookie('dt_user_id')) {
            if (checkVals.indexOf('person') === -1) {
                checkVals.push('person');
            }
        } else {
            const i = checkVals.indexOf('person');
            if (i > -1) {
                checkVals.splice(i, 1);
            }
        }
        setVals.checkVals = [...checkVals];
        this.setState(setVals, this.search);
    };

    changeTaskName = (value: any) => {
        // 任务名变更
        this.setState({ taskName: value });
    };

    changeSearchType = (type: any) => {
        this.setState({ searchType: type });
        this.onSearchByTaskName();
    };

    onSearchByTaskName = () => {
        this.setState(
            { current: 1 },
            this.search
        );
    };

    onTabChange = (tabKey: any) => {
        this.setState({
            tabKey
        });
    };

    onCheckAllChange = (e: any) => {
        let selectedRowKeys: any = [];
        if (e.target.checked) {
            selectedRowKeys = this.state.tasks.data.map((item: any) => item.id);
        }

        this.setState({
            selectedRowKeys,
            checkAll: e.target.checked
        });
    };

    onCheckChange = (checkedList: any) => {
        // const { user } = this.props;
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
                conditions.person = utils.getCookie('dt_user_id');
            } else if (item === 'todayUpdate') {
                conditions.startTime = moment()
                    .set({
                        hour: 0,
                        minute: 0,
                        second: 0
                    })
                    .unix();
                conditions.endTime = moment()
                    .set({
                        hour: 23,
                        minute: 59,
                        second: 59
                    })
                    .unix();
            } else if (item === 'stopped') {
                conditions.scheduleStatus = 2; // 任务状态(1:正常 2：冻结)
            }
        });
        // 清理掉责任人信息
        if (!conditions.person && person == utils.getCookie('dt_user_id')) {
            conditions.person = '';
        }
        this.setState(conditions, this.search);
    };

    changeProject = (value: string | number) => {
        this.setState({ projectId: value }, this.search)
    }

    closeSlidePane = () => {
        this.setState({
            visibleSlidePane: false,
            selectedTask: null
        });
    };

    initTaskColumns = () => {
        const isPro = this.props.project?.projectType == PROJECT_TYPE.PRO;
        const pre = isPro ? '发布' : '提交';
        // const { taskTypeFilter } = this.props;
        const { taskPeriodId, taskType, taskTypeFilter } = this.state;

        return [
            {
                title: '任务名称',
                dataIndex: 'name',
                key: 'name',
                render: (text: any, record: any) => {
                    const content =
                        record.isDeleted === 1 ? (
                            `${text} (已删除)`
                        ) : (
                            <a
                                onClick={() => {
                                    this.showTask(record);
                                }}
                            >
                                {record.name + (record.scheduleStatus == SCHEDULE_STATUS.STOPPED ? ' (已冻结)' : '')}
                            </a>
                        );
                    return content;
                }
            },
            {
                title: pre + '时间',
                dataIndex: 'gmtModified',
                key: 'gmtModified',
                render: (text: any) => {
                    return <span>{utils.formatDateTime(text)}</span>;
                }
            },
            {
                title: '任务类型',
                dataIndex: 'taskType',
                key: 'taskType',
                render: (text: any) => {
                    return <TaskType value={text} />;
                },
                filters: taskTypeFilter,
                filteredValue: taskType
            },
            {
                title: '调度周期',
                dataIndex: 'taskPeriodId',
                key: 'taskPeriodId',
                render: (text: any) => {
                    return <TaskTimeType value={text} />;
                },
                filters: offlineTaskPeriodFilter,
                filteredValue: taskPeriodId
            },
            {
                title: '责任人',
                dataIndex: 'userName',
                key: 'userName',
                render: (text: any, record: any) => {
                    return <span>{record.ownerUser && record.ownerUser.userName}</span>;
                }
            },
            {
                title: '操作',
                key: 'operation',
                width: 120,
                render: (text: any, record: any) => {
                    return (
                        <span>
                            <a
                                onClick={() => {
                                    this.clickPatchData(record);
                                }}
                            >
                                补数据
                            </a>
                            <span className="ant-divider"></span>
                            <a onClick={() => {
                                goToTaskDev(record)
                            }}
                            >
                                修改
                            </a>
                        </span>
                    );
                }
            }
        ];
    };
    onExpandRows = (expandedRows: any) => {
        this.setState({ expandedRowKeys: expandedRows });
    };
    onExpand = (expanded: any, record: any) => {
        if (expanded) {
            const { tasks } = this.state;
            let newTasks = cloneDeep(tasks);
            const reqParams = this.getReqParams();
            reqParams.taskId = record.id;
            Api.getRelatedTasks(reqParams).then((res: any) => {
                if (res.code == 1) {
                    const index = newTasks.data.findIndex((task: any) => {
                        return task.id === record.id;
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
                    });
                }
            });
        } else {
            console.log('record');
        }
    };

    renderStatus = (list: any) => {
        return list.map((item: any, index: any) => {
            const { className, children } = item;
            return (
                <span key={index} className={className}>
                    {Object.prototype.toString.call(children) === '[object Object]'
                        ? `${children.title}: ${children.dataSource || 0}`
                        : children.map((childItem: any) => {
                            return (
                                <span key={childItem.title}>
                                    {childItem.title}: {childItem.dataSource || 0}
                                </span>
                            );
                        })}
                </span>
            );
        });
    };
    render () {
        const {
            tasks, patchDataVisible, selectedTask, person, checkVals, patchTargetTask, current,
            taskName, visibleSlidePane, selectedRowKeys, tabKey, searchType, pageSize,
            appType, projectId
        } = this.state;
        const { projectList, personList } = this.props

        const pagination: any = {
            total: tasks.totalCount,
            showQuickJumper: true,
            showSizeChanger: true,
            defaultPageSize: 20,
            pageSizeOptions: ['10', '20', '50', '100', '200'],
            current,
            pageSize,
            onChange: (page: any, pageSize: any) => this.handleTableChange({ current: page, pageSize }, {}),
            onShowSizeChange: (page: any, pageSize: any) => this.handleTableChange({ current: page, pageSize }, {})
        };

        const rowSelection: any = {
            onChange: (selectedRowKeys: any, selectedRows: any) => {
                this.setState({
                    selectedRowKeys
                });
            },
            selectedRowKeys: selectedRowKeys
        };

        const tableFooter = (currentPageData: any) => {
            return (
                <div className="flex-between">
                    <div style={{ paddingLeft: '25px' }}>
                        <Button type="primary" onClick={this.forzenTasks.bind(this, 2)}>
                            冻结
                        </Button>
                        <Button style={{ marginLeft: 15 }} onClick={this.forzenTasks.bind(this, 1)}>
                            解冻
                        </Button>
                    </div>
                    <div>
                        <Pagination style={{ top: 12 }} {...pagination} />
                    </div>
                </div>
            );
        };

        const getTitle = (label: string) => {
            return <span className="form-label">{label}</span>
        }

        const clientHeight = document.documentElement.clientHeight - 280;

        return (
            <div className="c-taskMana__wrap">
                <Form layout="inline" style={{ marginBottom: 8 }}>
                    <Col>
                        <FormItem label={getTitle('产品')}>
                            <Select
                                className="dt-form-shadow-bg"
                                style={{ width: 200 }}
                                placeholder="请选择产品"
                                value={appType}
                            >
                                <Option value={APPS_TYPE.INDEX}>指标管理</Option>
                            </Select>
                        </FormItem>
                        <FormItem label={getTitle('项目')}>
                            <Select
                                allowClear
                                showSearch
                                className="dt-form-shadow-bg"
                                style={{ width: 200 }}
                                placeholder="请选择项目"
                                value={projectId}
                                optionFilterProp="children"
                                onSearch={this.getProjectList}
                                onChange={this.changeProject}
                            >
                                {projectList.map(item => {
                                    return <Option key={item.projectId} value={`${item.projectId}`}>{item.projectName}</Option>
                                })}
                            </Select>
                        </FormItem>
                        <FormItem label="" className="batch-operation_offlineImg dt-form-shadow-bg">
                            <MultiSearchInput
                                placeholder="按任务名称搜索"
                                style={{ width: 250 }}
                                value={taskName}
                                searchType={searchType}
                                onChange={this.changeTaskName}
                                onTypeChange={this.changeSearchType}
                                onSearch={this.onSearchByTaskName}
                            />
                        </FormItem>
                        <FormItem label={getTitle('责任人')}>
                            <Select
                                allowClear
                                showSearch
                                size="default"
                                className="dt-form-shadow-bg"
                                style={{ width: 200 }}
                                placeholder="请选择责任人"
                                optionFilterProp="name"
                                value={person}
                                onChange={this.changePerson}
                            >
                                {personList.map((item: any) => {
                                    return <Option key={item.dtuicUserId} value={`${item.dtuicUserId}`}>{item.userName}</Option>
                                })}
                            </Select>
                        </FormItem>
                        <FormItem>
                            <Checkbox.Group value={checkVals} onChange={this.onCheckChange}>
                                <Checkbox value="person" className="select-task">
                                    我的任务
                                </Checkbox>
                                <Checkbox value="todayUpdate" className="select-task">
                                    今日修改的任务
                                </Checkbox>
                                <Checkbox value="stopped" className="select-task">
                                    冻结的任务
                                </Checkbox>
                            </Checkbox.Group>
                        </FormItem>
                    </Col>
                </Form>
                <Table
                    key={`task-list${tasks.data && tasks.data.length}`}
                    rowKey="id"
                    rowClassName={(record: any, index: any) => {
                        if (this.state.selectedTask && this.state.selectedTask.id == record.id) {
                            return 'row-select';
                        } else {
                            return '';
                        }
                    }}
                    style={{ height: `calc(100vh - 200px)` }}
                    className="dt-table-fixed-contain-footer dt-table-border"
                    expandedRowKeys={this.state.expandedRowKeys}
                    pagination={false}
                    rowSelection={rowSelection}
                    loading={this.state.loading}
                    scroll={{ y: clientHeight }}
                    columns={this.initTaskColumns()}
                    dataSource={tasks.data || []}
                    onChange={this.handleTableChange}
                    onExpand={this.onExpand}
                    onExpandedRowsChange={this.onExpandRows}
                    footer={tableFooter}
                />
                <SlidePane
                    className="m-tabs bd-top bd-right m-slide-pane"
                    onClose={this.closeSlidePane}
                    visible={visibleSlidePane}
                    style={{
                        right: '0px',
                        width: '60%',
                        height: '100%',
                        position: 'fixed',
                        minHeight: '600px',
                        paddingTop: '64px'
                    }}
                >
                    <Tabs
                        className="c-taskMana__slidePane__tabs"
                        animated={false}
                        onChange={this.onTabChange}
                        tabBarStyle={{ zIndex: 3 }}
                        style={{ height: '100%' }}
                    >
                        <TabPane tab="依赖视图" key="taskFlow">
                            <TaskFlowView
                                reload={this.search}
                                key={`taskGraph-${selectedTask && selectedTask.id}-${tabKey}`}
                                visibleSlidePane={visibleSlidePane}
                                clickPatchData={this.clickPatchData}
                                tabData={selectedTask}
                            />
                        </TabPane>
                    </Tabs>
                </SlidePane>
                <PatchDataModal
                    visible={patchDataVisible}
                    task={patchTargetTask}
                    handCancel={() => {
                        this.setState({ patchDataVisible: false, patchTargetTask: '' });
                    }}
                />
            </div>
        )
    }
}
export default connect(
    (state: any) => {
        return {
            projectList: state.operation.projectList,
            personList: state.operation.personList
        }
    }
)(OfflineTaskMana);
