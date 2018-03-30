import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import { Button, Table, message, Modal, Input, Select, Popconfirm, Form } from 'antd';
import moment from 'moment';

import { ruleConfigActions } from '../../../actions/ruleConfig';
import { rowFormItemLayout } from '../../../consts';
import RCApi from '../../../api/ruleConfig';

const Option = Select.Option;
const TextArea = Input.TextArea;
const FormItem = Form.Item;

// API服务器
const API_SERVER = APP_CONF ? APP_CONF.API_SERVER : '';

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
    }
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

    componentWillReceiveProps(nextProps) {
        let oldData = this.props.data,
            newData = nextProps.data;

        if (!isEmpty(newData) && oldData !== newData) {
            console.log(oldData,newData,'trigger')
            let monitorId = newData.monitorPartVOS[0].monitorId;

            if (monitorId) {
                this.props.getRemoteTrigger({ tableId: newData.tableId });
                this.props.getMonitorRule({ monitorId });
                this.setState({ monitorId });
            }
        }
    }

    // 远程调用table设置
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
            width: '28%',
            render: (url) => `${API_SERVER}${url}`
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

    // 编辑远程调用
    editTrigger = (record) => {
        const { data } = this.props;
        let monitorPart = data.monitorPartVOS.filter(item => record.id === item.monitorId)[0];
        this.props.getMonitorRule({ monitorId: record.id });
        if (monitorPart) {
            this.setState({ 
                monitorId: record.id, 
                selectedIds: record.ruleIds,
                remark: record.remark
            });
            this.showRemoteModal();
        }
    }

    // 删除远程调用
    deleteTrigger = (id) => {
        const { data } = this.props;
        RCApi.delRemoteTrigger({ monitorId: id }).then((res) => {
            if (res.code === 1) {
                message.success('删除成功！');
                this.props.getRemoteTrigger({ tableId: data.tableId });
            }
        });
    }

    initRulesColumns = () => {
        return [{
            title: '字段',
            dataIndex: 'columnName',
            key: 'columnName',
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
            width: '17%',
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
            width: '17%',
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
            width: '16%'
        }, {
            title: '校验方法',
            dataIndex: 'verifyType',
            key: 'verifyType',
            render: (text, record) => {
                const { verifyType } = this.props.common.allDict;
                return verifyType[text - 1].name || undefined;
            },
            width: '14%',
        }, {
            title: '阈值配置',
            dataIndex: 'threshold',
            key: 'threshold',
            render: (text, record) => {
                if (record.verifyType !== 1) {
                    return `${record.operator}  ${text}  %`;
                } else {
                    return `${record.operator}  ${text}`;
                }
            },
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

    onMonitorIdChange = (value) => {
        let monitorId = value;

        this.props.getMonitorRule({ monitorId });
        this.setState({ 
            monitorId,
            selectedIds: [],
            remark: undefined
        });
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

        this.props.form.resetFields();
    }

    onRemoteTrigger = () => {
        const { selectedIds, monitorId, remark } = this.state;
        const { data, form, getRemoteTrigger } = this.props;
        
        if (selectedIds.length) {
            form.validateFields({ force: true }, (err, values) => {
                console.log(err,values)
                if(!err) {
                    RCApi.addRemoteTrigger({
                        monitorId: monitorId,
                        ruleIds: selectedIds,
                        remark: remark
                    }).then((res) => {
                        if (res.code === 1) {
                            message.success('操作成功！');
                            this.closeRemoteModal();
                            getRemoteTrigger({ tableId: data.tableId });
                        }
                    });
                }
            });
        } else {
            message.error('请选择一个监控规则！');
        }
    }

    render() {
        const { data, ruleConfig, common, form } = this.props;
        const { monitorId, visible, selectedIds, remark } = this.state;
        const { triggerList, monitorRules } = ruleConfig;
        const { getFieldDecorator } = form;

        let monitorPartVOS = data.monitorPartVOS ? data.monitorPartVOS : [];
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
                    <Button style={{ marginTop: 10 }} type="primary" onClick={this.showRemoteModal}>配置远程触发</Button>
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
                    
                    <div>
                        分区：
                        <Select 
                            value={monitorId ? monitorId.toString() : undefined}
                            style={{ width: 150 }}
                            onChange={this.onMonitorIdChange}>
                            {
                                monitorPartVOS.map((item) => {
                                    return <Option key={item.monitorId} value={item.monitorId.toString()}>{item.partValue}</Option>
                                })
                            }
                        </Select>
                    </div>

                    <Table 
                        rowKey="id"
                        className="m-table common-table"
                        rowSelection={rowSelection}
                        columns={this.initRulesColumns()}
                        pagination={false}
                        dataSource={monitorRules}
                    />

                    <FormItem {...rowFormItemLayout}>
                        {
                            getFieldDecorator('remark', {
                                rules: [
                                    { max: 100, message: '备注不能超过100个字符'}
                                ], 
                                initialValue: remark
                            })(
                                <TextArea 
                                    className="trigger-remarks" 
                                    autosize={{ minRows: 3, maxRows: 6 }} 
                                    placeholder="备注信息" 
                                    onChange={this.onRemarkChange} />
                            )
                        }
                    </FormItem>
                </Modal>
            </div>
        );
    }
}

RemoteTriggerPane = Form.create()(RemoteTriggerPane);