import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Table, message, Modal, Input, Select, Popconfirm } from 'antd';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import moment from 'moment';

import RCApi from '../../../api/ruleConfig';

const Option = Select.Option;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { ruleConfig, common } = state;
    return { ruleConfig, common }
}

const mapDispatchToProps = dispatch => ({
    getRemoteTrigger(params) {
        dispatch(ruleConfigActions.getRemoteTrigger(params));
    },
    getMonitorRule(params) {
        dispatch(ruleConfigActions.getMonitorRule(params));
    },
    
})

@connect(mapStateToProps, mapDispatchToProps)
export default class RemoteTriggerPane extends Component {
    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            selectedIds: [],
            remark: undefined,
            monitorId: undefined,
        };
    }

    componentDidMount() {
        const { data } = this.props;
        let monitorId = data.monitorPartVOS[0].monitorId;

        this.props.getRemoteTrigger({ tableId: data.tableId });
        this.props.getMonitorRule({ monitorId });
        this.setState({ monitorId });
    }

    initTriggerColumns = () => {
        return [{
            title: '分区',
            dataIndex: 'partitionValue',
            key: 'partitionValue',
            width: '15%',
        }, {
            title: '触发规则数',
            dataIndex: 'ruleNumber',
            key: 'ruleNumber',
            width: '8%',
        }, 
        {
            title: '访问接口',
            dataIndex: 'url',
            key: 'url',
            width: '28%'
        },  {
            title: '最近修改人',
            key: 'modifyUser',
            dataIndex: 'modifyUser',
            width: '12%',
        }, {
            title: '最近修改时间',
            key: 'gmtModified',
            dataIndex: 'gmtModified',
            width: '12%',
            render: (text) => (moment(text).format("YYYY-MM-DD HH:mm"))
        }, {
            title: '备注',
            dataIndex: 'remark',
            key: 'remark',
            width: '15%',
        }, {
            title: '操作',
            width: '10%',
            render: (text, record) => {
                return (
                    <div className="editable-row-operations">
                        <a className="m-r-8" onClick={() => this.editTrigger(record)}>编辑</a>
                        <Popconfirm title="确定要删除吗？" onConfirm={() => this.deleteTrigger(record.id)}>
                            <a>删除</a>
                        </Popconfirm>
                    </div>
                );
            },
        }]  

    }

    editTrigger = (record) => {
        const { data } = this.props;
        let monitorPart = data.monitorPartVOS.filter(item => record.id === item.monitorId)[0];

        if (monitorPart) {
            this.setState({ 
                monitorId: record.id, 
                selectedIds: record.ruleIds,
                remark: record.remark
            });
            this.showRemoteModal();
        }
    }

    deleteTrigger = (id) => {
        RCApi.delRemoteTrigger({ monitorId: id }).then((res) => {
            if (res.code === 1) {
                message.success('删除成功！');
            }
        });
    }

    initColumns = () => {
        return [{
            title: '字段',
            dataIndex: 'columnName',
            key: 'columnName',
            render: (text, record) => this.renderColumns(text, record, 'columnName'),
            width: '17%',
        }, {
            title: '统计函数',
            dataIndex: 'functionId',
            key: 'functionId',
            render: (text, record) => this.renderColumns(text, record, 'functionId'),
            width: '17%',
        }, 
        {
            title: '过滤条件',
            dataIndex: 'filter',
            key: 'filter',
            render: (text, record) => this.renderColumns(text, record, 'filter'),
            width: '16%'
        }, {
            title: '校验方法',
            dataIndex: 'verifyType',
            key: 'verifyType',
            render: (text, record) => this.renderColumns(text, record, 'verifyType'),
            width: '14%',
        }, {
            title: '阈值配置',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => this.renderColumns(text, record, 'threshold'),
            width: '10%'
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
        }]  
    }

    renderColumns(text, record, type) {
        const { currentRule } = this.state;
        let obj = {
            children: this.renderTD(text, record, type),
            props: {},
        };

        if (record.isCustomizeSql) {
            switch(type) {
                case 'columnName':
                    obj.props.colSpan = 3;
                    break;
                case 'functionId':
                    obj.props.colSpan = 0;
                    break;
                case 'filter':
                    obj.props.colSpan = 0;
                    break;
                default:
                    break;
            }
        }

        return obj;
    }

    renderTD = (text, record, type) => {
        const { ruleConfig, common } = this.props;
        const { monitorFunction } = ruleConfig;
        const { verifyType } = common.allDict;

        switch (type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return record.customizeSql;
                } else {
                    return text;
                }
            }

            case 'functionId': {
                return  record.functionName;
            }

            case 'verifyType': {
                return verifyType[text - 1].name || undefined;
            }

            case 'threshold': {
                return text ? `${record.operator}  ${text}` : 0;
            }

            default:
                return text;
        }
    }

    onMonitorIdChange = (value) => {
        const { data } = this.props;
        let monitorId = value;
        
        this.props.getRemoteTrigger({ tableId: data.tableId });
        this.props.getMonitorRule({ monitorId });
        this.setState({ monitorId });
    }

    onRemarkChange = (e) => {
        this.setState({ remark: e.target.value });
    }

    showRemoteModal = () => {
        this.setState({
            visible: true
        });
    }

    closeRemoteModal = () => {
        this.setState({
            visible: false,
            selectedIds: []
        });
    }

    onRemoteTrigger = () => {
        const { selectedIds, monitorId, remark } = this.state;
        const { data } = this.props;
        
        if (selectedIds.length) {
            RCApi.addRemoteTrigger({
                monitorId: monitorId,
                ruleIds: selectedIds,
                remark: remark
            }).then((res) => {
                if (res.code === 1) {
                    message.success('操作成功！');
                    this.closeRemoteModal();
                    this.props.getRemoteTrigger({ tableId: data.tableId });
                }
            });
        } else {
            message.error('请选择一个监控规则！');
        }
    }

    render() {
        const { data, ruleConfig, common } = this.props;
        const { monitorId, visible, selectedIds, remark } = this.state;
        const { loading, triggerList, monitorRules } = ruleConfig;

        let rowSelection = {
            selectedRowKeys: selectedIds,
            onChange: (selectedIds, selectedRows) => {
                this.setState({
                    selectedIds: selectedIds,
                    selectedRows: selectedRows
                })
            },
        };

        return (
            <div className="trigger-box">
                {
                    triggerList.length > 0
                    &&
                    <Table 
                        rowKey="id"
                        className="m-table common-table"
                        loading={loading}
                        columns={this.initTriggerColumns()}
                        pagination={false}
                        dataSource={triggerList}
                    />
                }

                <h2>什么是远程触发？</h2>
                <p>您可以在自建的大数据平台或公有云平台上通过接口调用的形式,触发本系统中的数据质量校验任务执行。</p>
                <p>典型的场景：您自建的大数据平台上对某张表进行ETL任务处理，需要在ETL任务处理前，
                对此表的数据质量进行校验，校验通过后再启动后续的计算任务
                （防止错误数据流入下一个计算环节，同时可以节约计算资源）。
                </p>

                <p style={{ marginTop: 10 }}>配置的流程如下：</p>
                <p>1、在本平台配置表的数据质量检测任务，并在本页面开启远程触发</p>
                <p>2、在您自建的大数据平台上，新建一个脚本任务，并编写调用的代码，详细的使用教程请参考<a>《帮助文档》</a>；</p>
                <p>3、在您自建的大数据平台上，将数据处理任务作为脚本任务的下游任务；</p>

                {
                    triggerList.length < 1
                    &&
                    <Button style={{ margin: '10px' }} type="primary" onClick={this.showRemoteModal}>配置远程触发</Button>
                }

                <Modal
                    title="配置远程调用"
                    wrapClassName="remoteTriggerModal"
                    maskClosable={false}
                    visible={visible}
                    width={'70%'}
                    okText="生成远程调用API"
                    cancelText="取消"
                    onOk={this.onRemoteTrigger}
                    onCancel={this.closeRemoteModal}>
                    {
                        data.monitorPartVOS.length > 1
                        &&
                        <div>
                            分区：
                            <Select 
                                value={monitorId ? monitorId.toString() : undefined}
                                style={{ width: 150 }}
                                onChange={this.onMonitorIdChange}>
                                {
                                    data.monitorPartVOS.map((item) => {
                                        return <Option key={item.monitorId} value={item.monitorId.toString()}>{item.partValue}</Option>
                                    })
                                }
                            </Select>
                        </div>
                    }

                    <Table 
                        rowKey="id"
                        className="m-table common-table"
                        rowSelection={rowSelection}
                        columns={this.initColumns()}
                        pagination={false}
                        dataSource={monitorRules}
                    />
                    
                    <TextArea 
                        className="trigger-remarks" 
                        value={remark}
                        autosize={{ minRows: 3, maxRows: 6 }} 
                        placeholder="备注信息" 
                        onChange={this.onRemarkChange} />
                </Modal>
            </div>
        );
    }
}