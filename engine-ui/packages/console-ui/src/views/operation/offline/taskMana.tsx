import * as React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { cloneDeep } from 'lodash';

import { Table, message, Card, Select, Form, Checkbox, Tabs, Pagination } from 'antd';

import utils from 'dt-common/src/utils';
import { replaceObjectArrayFiledName } from 'dt-common/src/funcs';
import { SlidePane } from 'dt-react-component';
import MultiSearchInput from 'dt-common/src/widgets/multiSearchInput';
import Api from '../../../api';
import { offlineTaskPeriodFilter, SCHEDULE_STATUS, PROJECT_TYPE, TASK_TYPE } from '../../../consts/comm';

import { TaskTimeType, TaskType } from '../../../components/status';

import PatchDataModal from './patchDataModal';
import TaskFlowView from './taskFlowView';

import { workbenchActions } from '../../../actions/operation';

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
        projectUsers: [],
        appType: '',
        projectId: ''
    };

    componentDidMount () {
        let ywappType = sessionStorage.getItem('ywappType')
        // 判断是否有taskId存在，如有则现实详情
        if (this.props.router.location.query.taskId) {
            let record = {
                id: this.props.router.location.query.taskId,
                appType: this.props.router.location.query.appType,
                projectId: this.props.router.location.query.pid
            }
            this.showTask(record)
            this.search();
            this.getPersonApi(1);
            this.getTaskTypesX()
        }
        let urlAppType = this.props.router.location.query.appType
        let urlProjectId = this.props.router.location.query.pid
        if (ywappType && ywappType != undefined && ywappType != '') {
            if (urlAppType && urlAppType != undefined && urlAppType != '') {
                this.setState({
                    appType: urlAppType,
                    projectId: urlProjectId
                }, () => {
                    sessionStorage.setItem('ywappType', urlAppType)
                    sessionStorage.setItem('ywprojectId', urlProjectId)
                    if (this.props.project?.id !== 0) {
                        this.search();
                        this.getPersonApi(1);
                        this.getTaskTypesX()
                    };
                });
            } else {
                this.setState({
                    appType: sessionStorage.getItem('ywappType'),
                    projectId: sessionStorage.getItem('ywprojectId')
                }, () => {
                    if (this.props.project?.id !== 0) {
                        this.search();
                        this.getPersonApi(1);
                        this.getTaskTypesX()
                    };
                });
            }
        } else {
            if (urlAppType == undefined) {
                this.props.router.location.query.appType = ''
                this.props.router.location.query.pid = ''
            }
            this.setState({
                appType: this.props.router.location.query.appType,
                projectId: this.props.router.location.query.pid
            }, () => {
                sessionStorage.setItem('ywappType', this.props.router.location.query.appType)
                sessionStorage.setItem('ywprojectId', this.props.router.location.query.pid)
                if (this.props.project?.id !== 0) {
                    this.search();
                    this.getPersonApi(1);
                    this.getTaskTypesX()
                };
            });
        }
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps: any) {
        const project = nextProps.project;
        const oldProj = this.props.project;
        if (project && oldProj.id !== project.id) {
            this.setState({ current: 1, taskName: '', visibleSlidePane: false }, () => {
                this.search();
            });
        }
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

    // 责任人接口
    getPersonApi (parmms: any) {
        const ctx = this;
        Api.getPersonInCharge(parmms).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ projectUsers: res.data });
            }
            this.setState({ loading: false });
        });
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

    goToTaskDevHtml = (record: any) => {
        if (record.appType == 10) {
            let path2 = `${document.location.origin}/easy-index/index-define?taskId=${record.id}`
            window.open(path2);
        }
        // else{
        //     // const url=`${document.location.origin}/batch/#/offline/task?taskId=${id}`
        //     // window.open(url)
        // }
    };
    showTask = (task: any) => {
        this.setState({
            visibleSlidePane: true,
            selectedTask: task
        });
    };

    clickMenu = (target: any) => {
        const task = target.item.props.value;
        if (target.key === 'edit') {
            this.props.goToTaskDev(task.id);
        }
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
                                console.log('gg修改')
                                this.props.goToTaskDev(record);
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
        // const { projectUsers, project } = this.props;
        const {
            tasks,
            patchDataVisible,
            selectedTask,
            person,
            checkVals,
            patchTargetTask,
            current,
            taskName,
            visibleSlidePane,
            selectedRowKeys,
            tabKey,
            searchType,
            pageSize,
            projectUsers
        } = this.state;
        // const isPro = project?.projectType == PROJECT_TYPE.PRO;
        const isPro = false
        // const isTest = project.projectType == PROJECT_TYPE.TEST;
        const userItems =
            projectUsers && projectUsers.length > 0
                ? projectUsers.map((item: any) => {
                    return (
                        <Option key={item.dtuicUserId} value={`${item.dtuicUserId}`} name={item.userName}>
                            {item.userName}
                        </Option>
                    );
                })
                : [];

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
                        {/* <Button type="primary" onClick={this.forzenTasks.bind(this, 2)}>
                            冻结
                        </Button>
                        <Button style={{ marginLeft: 15 }} onClick={this.forzenTasks.bind(this, 1)}>
                            解冻
                        </Button> */}
                    </div>
                    <div>
                        <Pagination style={{ top: 20 }} {...pagination} />
                    </div>
                </div>
            );
        };

        let clientHeight = document.documentElement.clientHeight - 280;
        return (
            <div>
                <div
                    className={`m-card box-2 task-manage offline__search-normal offline__search-normal_left`}
                    style={{ padding: 0 }}
                >
                    <Card
                        bordered={false}
                        loading={false}
                        className="dt-table-fixed-base"
                        title={
                            <Form layout="inline" className="m-form-inline font-weight-400" style={{ height: '36px' }}>
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
                                <FormItem label="责任人">
                                    <Select
                                        allowClear
                                        showSearch
                                        size="default"
                                        className="dt-form-shadow-bg"
                                        style={{ width: 220 }}
                                        placeholder="请选择责任人"
                                        optionFilterProp="name"
                                        value={person}
                                        onChange={this.changePerson}
                                    >
                                        {userItems}
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
                            </Form>
                        }
                    >
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
                            style={{ marginTop: '1px', height: `calc(100vh - 160px)` }}
                            className={`dt-ant-table dt-ant-table--border rdos-ant-table-placeholder border-table ${
                                isPro ? 'full-screen-table-90' : 'full-screen-table-120'
                            } dt-table-fixed-base dt-table-fixed-contain-footer dt-batch-table-height`}
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
                                        goToTaskDev={this.props.goToTaskDev}
                                        clickPatchData={this.clickPatchData}
                                        tabData={selectedTask}
                                    />
                                </TabPane>
                            </Tabs>
                        </SlidePane>
                    </Card>
                </div>
                <PatchDataModal
                    visible={patchDataVisible}
                    task={patchTargetTask}
                    handCancel={() => {
                        this.setState({ patchDataVisible: false, patchTargetTask: '' });
                    }}
                />
            </div>
        );
    }
}
export default connect(
    null,
    (dispatch: any) => {
        const actions = workbenchActions(dispatch);
        return {
            goToTaskDev: (record: any) => {
                actions.openTaskInDev(record);
            }
        };
    }
)(OfflineTaskMana);
