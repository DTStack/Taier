import * as React from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import {
    Button,
    Table,
    message,
    Modal,
    Input,
    Select,
    Popconfirm,
    Form,
    Checkbox
} from 'antd';
import moment from 'moment';

import ToolTipCopy from '../../../components/tooltipCopy';
import RuleView from '../../../components/ruleView';

import { ruleConfigActions } from '../../../actions/ruleConfig';
import { rowFormItemLayout, DATA_SOURCE, HELP_DOC_URL } from '../../../consts';
import RCApi from '../../../api/ruleConfig';

const Option = Select.Option;
const TextArea = Input.TextArea;
const FormItem = Form.Item;

// API服务器
// eslint-disable-next-line
const API_SERVER = APP_CONF && APP_CONF.API_SERVER ? APP_CONF.API_SERVER : '';

const mapStateToProps = (state: any) => {
    const { ruleConfig } = state;
    return { ruleConfig };
};

const mapDispatchToProps = (dispatch: any) => ({
    getRemoteTrigger (params: any) {
        dispatch(ruleConfigActions.getRemoteTrigger(params));
    },
    getMonitorRule (params: any) {
        dispatch(ruleConfigActions.getMonitorRule(params));
    }
});

@connect(
    mapStateToProps,
    mapDispatchToProps
)
class RemoteTriggerPane extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            visible: false,
            havePart: false,
            selectedIds: [],
            remark: undefined,
            monitorId: undefined
        };
    }

    componentDidMount () {
        const { data } = this.props;
        let monitorId = data.monitorPartVOS[0].monitorId;

        this.props.getRemoteTrigger({ tableId: data.tableId });
        const params: any = { monitorId };
        if (
            data.dataSourceType === DATA_SOURCE.HIVE ||
            data.dataSourceType === DATA_SOURCE.MAXCOMPUTE
        ) {
            params.havePart = true;
        }
        this.setState(params);
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        let oldData = this.props.data;

        let newData = nextProps.data;

        if (!isEmpty(newData) && oldData.tableId !== newData.tableId) {
            let monitorId = newData.monitorPartVOS[0].monitorId;

            this.props.getRemoteTrigger({ tableId: newData.tableId });
            this.setState({
                monitorId,
                havePart: false,
                remark: undefined,
                selectedIds: []
            });

            if (
                newData.dataSourceType === DATA_SOURCE.HIVE ||
                newData.dataSourceType === DATA_SOURCE.MAXCOMPUTE
            ) {
                this.setState({ havePart: true });
            }
        }
    }

    // 远程调用table设置
    initTriggerColumns = () => {
        const cols: any = [
            {
                title: '触发规则数',
                dataIndex: 'ruleNumber',
                key: 'ruleNumber',
                width: '120px'
            },
            {
                title: '触发接口',
                dataIndex: 'url',
                key: 'url',
                width: '200px',
                render: (url: any) => {
                    return <ToolTipCopy value={`${API_SERVER}${url}`} />;
                }
            },
            {
                title: '结果获取接口',
                dataIndex: 'viewStateUrl',
                key: 'viewStateUrl',
                width: '200px',
                render: (viewStateUrl: any) => {
                    return (
                        <ToolTipCopy value={`${API_SERVER}${viewStateUrl}`} />
                    );
                }
            },
            {
                title: '请求方式',
                dataIndex: 'method',
                key: 'method',
                width: '80px',
                render: (text: any) => `POST`
            },
            {
                title: '最近修改人',
                key: 'modifyUser',
                dataIndex: 'modifyUser',
                width: '160px'
            },
            {
                title: '最近修改时间',
                key: 'gmtModified',
                dataIndex: 'gmtModified',
                width: '12%',
                render: (text: any) => moment(text).format('YYYY-MM-DD HH:mm')
            },
            {
                title: '备注',
                dataIndex: 'remark',
                key: 'remark',
                width: '170px'
            },
            {
                title: '操作',
                width: '120px',
                render: (text: any, record: any) => {
                    return (
                        <div>
                            <a
                                className="m-r-8"
                                onClick={() => this.editTrigger(record)}
                            >
                                编辑
                            </a>
                            <Popconfirm
                                title="确定要删除吗？"
                                onConfirm={() => this.deleteTrigger(record.id)}
                            >
                                <a>删除</a>
                            </Popconfirm>
                        </div>
                    );
                }
            }
        ];

        if (this.state.havePart) {
            cols.splice(0, 0, {
                title: '分区',
                dataIndex: 'partitionValue',
                key: 'partitionValue',
                width: '12%'
            });
        }
        return cols;
    };

    // 编辑远程调用
    editTrigger = (record: any) => {
        this.showRemoteModal();
        this.props.getMonitorRule({ monitorId: record.monitorId });

        this.setState({
            monitorId: record.monitorId,
            chooseRule: record,
            selectedIds: record.ruleIds,
            remark: record.remark
        });
    };

    // 删除远程调用
    deleteTrigger = (id: any) => {
        const { data } = this.props;

        RCApi.delRemoteTrigger({ remoteId: id }).then((res: any) => {
            if (res.code === 1) {
                message.success('删除成功！');
                this.props.getRemoteTrigger({ tableId: data.tableId });
            }
        });
    };

    // 切换分区
    onMonitorIdChange = (value: any) => {
        let monitorId = value;

        this.props.getMonitorRule({ monitorId });
        this.setState({
            monitorId,
            selectedIds: [],
            remark: undefined
        });
    };

    // 备注回调
    onRemarkChange = (e: any) => {
        this.setState({ remark: e.target.value });
    };

    // 新建远程调用
    newRemoteTrigger = () => {
        const { monitorId } = this.state;

        this.props.getMonitorRule({ monitorId });
        this.showRemoteModal();
    };

    showRemoteModal = () => {
        this.setState({
            visible: true
        });
    };

    closeRemoteModal = () => {
        this.setState({
            visible: false,
            selectedIds: [],
            chooseRule: {}
        });

        this.props.form.resetFields();
    };

    // 生成远程调用
    saveRemoteTrigger = () => {
        const { selectedIds, monitorId, remark, chooseRule = {} } = this.state;
        const { data, form, getRemoteTrigger } = this.props;

        if (selectedIds.length) {
            form.validateFields((err: any, values: any) => {
                // console.log(err,values)
                if (!err) {
                    RCApi.addRemoteTrigger({
                        remoteId: chooseRule.id,
                        monitorId: monitorId,
                        ruleIds: selectedIds,
                        remark: remark
                    }).then((res: any) => {
                        if (res.code === 1) {
                            this.setState({
                                chooseRule: {},
                                selectedIds: [],
                                remark: null
                            });
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
    };
    selectRule = (checkedValue: any) => {
        this.setState({
            selectedIds: checkedValue
        });
    }
    changeAllSelect = (e: any) => {
        const { ruleConfig } = this.props;
        const { monitorRules } = ruleConfig;
        const checked = e.target.checked;
        if (checked) {
            this.setState({
                selectedIds: monitorRules.map((rule: any) => {
                    return rule.id
                })
            })
        } else {
            this.setState({
                selectedIds: []
            })
        }
    }
    render () {
        const { data, ruleConfig, form } = this.props;
        const { getFieldDecorator } = form;
        const { triggerList, monitorRules } = ruleConfig;
        const {
            monitorId,
            havePart,
            visible,
            selectedIds,
            remark
        } = this.state;

        let monitorPart = data.monitorPartVOS ? data.monitorPartVOS : [];
        return (
            <div className="trigger-box">
                <div
                    style={{ overflow: 'hidden', padding: '0px 10px 10px 0px' }}
                >
                    <Button
                        type="primary"
                        onClick={this.newRemoteTrigger}
                        style={{ float: 'right' }}
                    >
                        新增远程触发
                    </Button>
                </div>
                {triggerList.length > 0 && (
                    <Table
                        rowKey="id"
                        className="m-table"
                        columns={this.initTriggerColumns()}
                        pagination={false}
                        dataSource={triggerList}
                        scroll={{ y: 300 }}
                    />
                )}

                <h2>什么是远程触发？</h2>
                <p>
                    您可以在自建的大数据平台或公有云平台上通过接口调用的形式,触发本系统中的数据质量校验任务执行。
                </p>
                <p>
                    典型的场景：您自建的大数据平台上对某张表进行ETL任务处理，需要在ETL任务处理前，
                    对此表的数据质量进行校验，校验通过后再启动后续的计算任务
                    （防止错误数据流入下一个计算环节，同时可以节约计算资源）。
                </p>

                <p style={{ marginTop: 10 }}>配置的流程如下：</p>
                <p>
                    1、在本平台配置表的数据质量检测任务，并在本页面开启远程触发
                </p>
                <p>
                    2、在您自建的大数据平台上，新建一个脚本任务，并编写调用的代码，详细的使用教程请参考
                    <a href={HELP_DOC_URL.REMOTE} target="blank">《帮助文档》</a>；
                </p>
                <p>
                    3、在您自建的大数据平台上，将数据处理任务作为脚本任务的下游任务；
                </p>

                <Modal
                    title="配置远程调用"
                    wrapClassName="remoteTriggerModal"
                    width={'85%'}
                    visible={visible}
                    maskClosable={false}
                    okText="生成远程调用API"
                    cancelText="取消"
                    onOk={this.saveRemoteTrigger}
                    onCancel={this.closeRemoteModal}
                >
                    {havePart && (
                        <div>
                            分区：
                            <Select
                                style={{ width: 150 }}
                                value={
                                    monitorId ? monitorId.toString() : undefined
                                }
                                onChange={this.onMonitorIdChange}
                            >
                                {monitorPart.map((item: any) => {
                                    return (
                                        <Option
                                            key={item.monitorId}
                                            value={item.monitorId.toString()}
                                        >
                                            {item.partValue
                                                ? item.partValue
                                                : '全表'}
                                        </Option>
                                    );
                                })}
                            </Select>
                        </div>
                    )}
                    <Checkbox
                        checked={selectedIds.length && selectedIds.length == monitorRules.length}
                        onChange={this.changeAllSelect}
                        indeterminate={selectedIds.length && selectedIds.length != monitorRules.length}
                        style={{ marginBottom: '5px' }}
                    >
                        全选
                    </Checkbox>
                    <Checkbox.Group value={selectedIds} onChange={this.selectRule}>
                        {monitorRules.map((rule: any) => {
                            return <RuleView key={rule.id} data={rule} leftView={<Checkbox value={rule.id} style={{ marginTop: '4px', marginRight: '5px' }} />} />
                        })}
                    </Checkbox.Group>
                    <Form>
                        <FormItem
                            {...rowFormItemLayout}
                            style={{ marginBottom: 0 }}
                        >
                            {getFieldDecorator('remark', {
                                rules: [
                                    {
                                        max: 100,
                                        message: '备注不能超过100个字符'
                                    }
                                ],
                                initialValue: remark
                            })(
                                <TextArea
                                    placeholder="备注信息"
                                    className="trigger-remarks"
                                    autosize={{ minRows: 3, maxRows: 6 }}
                                    onChange={this.onRemarkChange}
                                />
                            )}
                        </FormItem>
                    </Form>
                </Modal>
            </div>
        );
    }
}

export default Form.create<any>()(RemoteTriggerPane);
