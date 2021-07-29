import * as React from 'react';
import { connect } from 'react-redux';
import moment from 'moment';
import { Link } from 'react-router';

import { Input, Select, message, Checkbox, Form, DatePicker, Table, Card, Modal, Pagination } from 'antd';

import Api from '../../../api';
const Search = Input.Search;
const Option = Select.Option;
const FormItem = Form.Item;
const confirm = Modal.confirm;

class PatchDataList extends React.Component<any, any> {
    state: any = {
        loading: false,
        current: 1,
        pageSize: 20,
        tasks: { data: [] },

        // 参数
        jobName: '',
        runDay: undefined,
        bizDay: '',
        dutyUserId: undefined,
        checkVals: [],
        projectUsers: [],
        appType: '',
        projectId: ''
    };

    componentDidMount () {
        this.setState({
            appType: sessionStorage.getItem('ywappType'),
            projectId: sessionStorage.getItem('ywprojectId')
        }, () => {
            this.loadPatchData();
            this.getPersonApi(1);
        });
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps(nextProps: any) {
        const { project } = nextProps;
        const oldProj = this.props.project;
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadPatchData();
        }
    }

    loadPatchData = (params?: any) => {
        const ctx = this;
        this.setState({ loading: true });
        let defaultParams = this.getReqParams();
        const reqParams = Object.assign(defaultParams, params);
        Api.getFillData(reqParams).then((res: any) => {
            if (res.code === 1) {
                ctx.setState({ tasks: res.data });
            }
            this.setState({ loading: false });
        });
    };
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
    killAllJobs = (job: any) => {
        confirm({
            title: '确认提示',
            content: '确定要杀死所有实例？',
            onOk () {
                Api.stopFillDataJobs({
                    fillDataJobName: job.fillDataJobName
                }).then((res: any) => {
                    if (res.code === 1) {
                        message.success('已成功杀死所有实例！');
                    }
                });
            }
        });
    };

    getReqParams = () => {
        const { jobName, runDay, bizDay, dutyUserId, current, pageSize, appType, projectId } = this.state;

        let reqParams: any = { currentPage: current || 1, pageSize: pageSize || 20 };

        if (jobName) {
            reqParams.jobName = jobName;
        }
        reqParams.appType = appType
        reqParams.projectId = projectId
        if (bizDay) {
            reqParams.bizDay = moment(bizDay).unix();
        }

        if (runDay) {
            reqParams.runDay = moment(runDay).unix();
        }
        if (dutyUserId) {
            reqParams.dutyUserId = dutyUserId;
        }

        return reqParams;
    };

    pageChange = (page: any) => {
        const { current } = this.state;
        const params: any = {
            currentPage: current === page.current ? 1 : page.current,
            pageSize: page.pageSize
        };
        this.setState({
            current: current === page.current ? 1 : page.current,
            pageSize: page.pageSize
        });
        this.loadPatchData(params);
    };
    onBuisTimeChange = (date: any) => {
        this.setState({ bizDay: date, current: 1 }, () => {
            this.loadPatchData({
                bizStartDay: moment(date).unix(),
                bizEndDay: moment(date).unix()
            })
        });
    };

    onRunningTime = (date: any) => {
        this.setState({ runDay: date, current: 1 }, this.loadPatchData);
    };

    onChangeJobName = (e: any) => {
        this.setState({ jobName: e.target.value });
    };

    onSearchByJobName = () => {
        this.setState(
            {
                current: 1
            },
            this.loadPatchData
        );
    };

    onOwnerChange = (value: any) => {
        const { user } = this.props;
        const { checkVals } = this.state;
        const setVals: any = {
            dutyUserId: value,
            current: 1
        };
        let checkArr: any = [...checkVals];
        if (value == user.id) {
            if (checkArr.indexOf('person') === -1) {
                checkArr.push('person');
            }
        } else {
            checkArr = [];
        }
        setVals.checkVals = checkArr;
        this.setState(setVals, this.loadPatchData);
    };
    onCheckChange = (checkedList: any) => {
        const { user } = this.props;
        const { dutyUserId } = this.state;

        const conditions: any = {
            checkVals: checkedList,
            runDay: ''
        };

        checkedList.forEach((item: any) => {
            if (item === 'person') {
                conditions.dutyUserId = `${user.id}`;
            } else if (item === 'todayUpdate') {
                conditions.runDay = moment();
                conditions.dutyUserId = `${user.id}`;
            }
        });

        // 清理掉责任人信息
        if (!conditions.dutyUserId && dutyUserId === `${user.id}`) {
            conditions.dutyUserId = '';
        }

        this.setState(conditions, this.loadPatchData);
    };

    initTaskColumns = () => {
        return [
            {
                title: '补数据名称',
                dataIndex: 'fillDataJobName',
                key: 'fillDataJobName',
                width: 300,
                render: (text: any, record: any) => {
                    return <Link to={`/operation/task-patch-data/${text}`}>{text}</Link>;
                }
            },
            {
                width: 120,
                title: '成功/已完成/总实例',
                dataIndex: 'doneJobSum',
                key: 'doneJobSum',
                render: (text: any, record: any) => {
                    const isComplete =
                        record.finishedJobSum == record.doneJobSum && record.doneJobSum == record.allJobSum;
                    const style = isComplete ? { color: '#333333' } : { color: '#EF5350' };
                    return (
                        <span style={style}>
                            {record.finishedJobSum}/{record.doneJobSum}/{record.allJobSum}
                        </span>
                    );
                }
            },
            {
                width: 140,
                title: '业务日期',
                dataIndex: 'fromDay',
                key: 'fromDay',
                render: (text: any, record: any) => {
                    return (
                        <span>
                            {record.fromDay} ~ {record.toDay}
                        </span>
                    );
                }
            },
            {
                width: 120,
                title: '实例生成时间',
                dataIndex: 'createTime',
                key: 'createTime'
            },
            {
                width: 120,
                title: '操作人',
                dataIndex: 'dutyUserName',
                key: 'dutyUserName'
            },
            {
                width: 120,
                title: '操作',
                dataIndex: 'id',
                fixed: 'right' as any,
                key: 'id',
                render: (text: any, record: any) => {
                    return <a onClick={this.killAllJobs.bind(this, record)}>杀死所有实例</a>;
                }
            }
        ];
    };

    disabledDate = (current: any) => {
        return current && current.valueOf() > Date.now();
    };

    render () {
        const { tasks, current, checkVals, dutyUserId, bizDay, runDay, jobName, pageSize, loading, projectUsers } = this.state;

        // const { projectUsers } = this.props;
        const userItems =
            projectUsers && projectUsers.length > 0
                ? projectUsers.map((item: any) => {
                    return (
                        <Option key={item.dtuicUserId} value={`${item.dtuicUserId}`} >
                            {item.userName}
                        </Option>
                    );
                })
                : [];

        const pagination: any = {
            total: tasks.totalCount,
            defaultPageSize: 20,
            showSizeChanger: true,
            showQuickJumper: true,
            pageSizeOptions: ['10', '20', '50', '100', '200'],
            current,
            pageSize,
            onChange: (page: any, pageSize: any) => this.pageChange({ current: page, pageSize }),
            onShowSizeChange: (page: any, pageSize: any) => this.pageChange({ current: page, pageSize })
        };

        const title = (
            <Form layout="inline" className="m-form-inline font-weight-400">
                <FormItem>
                    <Search
                        placeholder="按任务名称搜索"
                        style={{ width: '150' }}
                        value={jobName}
                        size="default"
                        className="dt-form-shadow-bg"
                        onChange={this.onChangeJobName}
                        onSearch={this.onSearchByJobName}
                    />
                </FormItem>
                <FormItem label="操作人">
                    <Select
                        allowClear
                        showSearch
                        style={{ width: 150 }}
                        placeholder="请选择操作人"
                        optionFilterProp="name"
                        className="dt-form-shadow-bg"
                        value={dutyUserId}
                        onChange={this.onOwnerChange}
                    >
                        {userItems}
                    </Select>
                </FormItem>
                <FormItem label="业务日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="业务日期"
                        style={{ width: 150 }}
                        className="dt-form-shadow-bg"
                        value={bizDay || null}
                        size="default"
                        onChange={this.onBuisTimeChange}
                    />
                </FormItem>
                <FormItem label="运行日期">
                    <DatePicker
                        format="YYYY-MM-DD"
                        placeholder="运行日期"
                        className="dt-form-shadow-bg"
                        style={{ width: 150 }}
                        size="default"
                        value={runDay || null}
                        disabledDate={this.disabledDate}
                        onChange={this.onRunningTime}
                    />
                </FormItem>
                <FormItem>
                    <Checkbox.Group value={checkVals} onChange={this.onCheckChange}>
                        <Checkbox value="person" style={{ fontWeight: 400, color: '#333333' }}>我的任务</Checkbox>
                        <Checkbox value="todayUpdate" style={{ fontWeight: 400, color: '#333333' }}>我今天补的</Checkbox>
                    </Checkbox.Group>
                </FormItem>
            </Form>
        );
        let clientHeight = document.documentElement.clientHeight - 260;

        return (
            <div className="box-1 m-card offline__search-normal offline__search-normal_left" style={{ padding: 0, marginTop: 0, minWidth: 1080 }}>
                <Card title={title} bordered={false} loading={false}>
                    <Table
                        rowKey="id"
                        columns={this.initTaskColumns()}
                        className="dt-table-fixed-base dt-table-fixed-contain-footer"
                        style={{ marginTop: 1, height: 'calc(100vh - 160px)' }}
                        pagination={false}
                        dataSource={tasks.data || []}
                        onChange={this.pageChange}
                        loading={loading}
                        scroll={{ x: '920px', y: clientHeight }}
                        footer={() => {
                            return <Pagination {...pagination}/>
                        }}
                    />
                </Card>
            </div>
        );
    }
}
export default connect((state: any) => {
    return {
        user: state.user
    };
})(PatchDataList);
