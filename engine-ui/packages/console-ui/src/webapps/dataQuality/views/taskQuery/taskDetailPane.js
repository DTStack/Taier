import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Table, message, Modal, Input, Select, Popconfirm } from 'antd';
import { taskQueryActions } from '../../actions/taskQuery';
import moment from 'moment';


const mapStateToProps = state => {
    const { taskQuery, common } = state;
    return { taskQuery, common }
}

const mapDispatchToProps = dispatch => ({
    getTaskDetail(params) {
        dispatch(taskQueryActions.getTaskDetail(params));
    },
   
})

@connect(mapStateToProps, mapDispatchToProps)
export default class TaskDetailPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            monitorId: undefined,
        };
    }

    componentDidMount() {
        const { data } = this.props;

    }

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;
        console.log(oldData,newData,'willre')
        if (isEmpty(oldData) && !isEmpty(newData)) {
            this.props.getTaskDetail({
                recordId: newData.id,
                monitorId: newData.monitorId
            })
        }
    }

    initRulesColumns = () => {
        return [{
            title: '字段',
            dataIndex: 'column',
            key: 'column',
            render: (text, record) => {
                let value = record.isCustomizeSql ? record.customizeSql : text;
                let obj = {
                    children: value,
                    props: {
                        colSpan: record.isCustomizeSql ? 3 : 1
                    },
                };

                return obj;
            },
            // width: '17%',
        }, {
            title: '统计函数',
            dataIndex: 'functionId',
            key: 'functionId',
            render: (text, record) => {
                let obj = {
                    children: record.functionName,
                    props: {
                        colSpan: record.isCustomizeSql ? 0 : 1
                    },
                };

                return obj;
            },
            // width: '17%',
        }, 
        {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => {
                let obj = {
                    children: text,
                    props: {
                        colSpan: record.isCustomizeSql ? 0 : 1
                    },
                };

                return obj;
            },
            // width: '16%'
        }, {
            title: '校验方法',
            dataIndex: 'verifyType',
            key: 'verifyType',
            render: (text, record) => {
                const { verifyType } = this.props.common.allDict;
                return verifyType[text - 1].name || undefined;
            },
            // width: '14%',
        }, {
            title: '状态',
            dataIndex: 'status',
            key: 'status',
            // width: '10%'
        }, {
            title: '统计值',
            dataIndex: 'statistic',
            key: 'statistic',
            // width: '10%'
        }, {
            title: '阈值',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => {
                if (record.isPercentage) {
                    return `${record.operator}  ${text}  %`;
                } else {
                    return `${record.operator}  ${text}`;
                }
            },
            // width: '10%'
        }, {
            title: '最近修改人',
            key: 'modifyUser',
            dataIndex: 'modifyUser',
            width: '13%',
            
        }, {
            title: '最近修改时间',
            key: 'gmtModified',
            dataIndex: 'gmtModified',
            width: '13%',
            render: (text) => (moment(text).format("YYYY-MM-DD HH:mm"))
        }, {
            title: '操作',
            render: () => (<a>查看报告</a>)
        }]  
    }

    render() {
        const { data, taskQuery, common } = this.props;
        const { monitorId, visible, selectedIds, remark } = this.state;
        const { loading, taskDetail } = taskQuery;

        return (
            <div className="task-detail-pane">
                <Table 
                    rowKey="id"
                    className="m-table common-table"
                    columns={this.initRulesColumns()}
                    pagination={false}
                    dataSource={taskDetail}
                />
            </div>
        );
    }
}