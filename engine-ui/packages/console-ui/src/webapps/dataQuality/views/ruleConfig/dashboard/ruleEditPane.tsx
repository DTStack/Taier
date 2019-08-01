import * as React from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import {
    Button,
    Form,
    Select,
    Row,
    Col,
    // Table,
    message
} from 'antd';

import RuleList from '../ruleList';
import ExecuteForm from './executeForm';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import {
    DATA_SOURCE
} from '../../../consts';
import RCApi from '../../../api/ruleConfig';
import EnvModal from '../../envModal';

const Option = Select.Option;

const mapStateToProps = (state: any) => {
    const { ruleConfig, common } = state;
    return { ruleConfig, common };
};

const mapDispatchToProps = (dispatch: any) => ({
    getRuleFunction (params: any) {
        dispatch(ruleConfigActions.getRuleFunction(params));
    },
    getTableColumn (params: any) {
        dispatch(ruleConfigActions.getTableColumn(params));
    },
    getMonitorDetail (params: any) {
        dispatch(ruleConfigActions.getMonitorDetail(params));
    },
    changeMonitorStatus (params: any) {
        dispatch(ruleConfigActions.changeMonitorStatus(params));
    }
});

@(connect(
    mapStateToProps,
    mapDispatchToProps
) as any)
class RuleEditPane extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            monitorId: undefined,
            havePart: false,
            showExecuteModal: false,
            visibleEnvModal: false
        };
    }

    componentDidMount () {
        this.props.getRuleFunction();
    }

    // eslint-disable-next-line
    UNSAFE_componentWillReceiveProps(nextProps: any) {
        let oldData = this.props.data;

        let newData = nextProps.data;

        if (!isEmpty(newData) && oldData.tableId !== newData.tableId) {
            let monitorId = newData.monitorPartVOS[0].monitorId;

            this.initData(monitorId, newData);
            this.setState({
                monitorId,
                havePart: false
            });

            if (newData.dataSourceType === DATA_SOURCE.HIVE || newData.dataSourceType === DATA_SOURCE.MAXCOMPUTE) {
                this.setState({ havePart: true });
            }
        }
    }

    // ajax获取数据
    initData = (monitorId: any, data: any) => {
        this.props.getMonitorDetail({ monitorId });
        this.props.getTableColumn({
            sourceId: data.sourceId,
            tableName: data.tableName
        });
    };

    deleteRule = async (rule: any) => {
        const { monitorId } = this.state;
        try {
            let res = await RCApi.deleteMonitorRule({
                ruleIds: [rule.id],
                monitorId: monitorId
            });
            if (res && res.code == 1) {
                return true;
            }
            return false;
        } catch {
            return false;
        }
    }

    onSaveRule = async (rule: any) => {
        const { monitorId } = this.state;
        try {
            let params: any = { ...rule, monitorId };
            if (params.isNew) {
                params.id = undefined
            }
            let res = await RCApi.saveMonitorRule(params);
            if (res && res.code == 1) {
                message.success('保存成功');
                return res.data;
            }
            return false;
        } catch {
            return false;
        }
    }

    // 切换分区
    onMonitorIdChange = (value: any) => {
        let monitorId = value;

        this.props.getMonitorDetail({ monitorId });
        this.setState({
            monitorId
        });
    };

    // 立即执行监控
    executeMonitor = (monitorId: any) => {
        // const { data } = this.props;

        RCApi.executeMonitor({ monitorId }).then((res: any) => {
            if (res.code === 1) {
                message.success('操作成功，稍后可在任务查询中查看详情');
                this.props.closeSlidePane();
                // hashHistory.push(`/dq/taskQuery?tb=${data.tableName}&source=${data.dataSourceType}`);
            } else {
                message.error('执行失败');
            }
        });
    };

    // 开启或关闭监控
    changeMonitorStatus = (monitorId: any) => {
        RCApi.changeMonitorStatus({ monitorId }).then((res: any) => {
            if (res.code === 1) {
                message.success('操作成功');
                this.props.getMonitorDetail({ monitorId });
            }
        });
    };

    // 修改任务参数
    updateEnvParams = (value: any) => {
        if (!value) {
            message.error('环境参数为空！');
            return;
        }
        const { monitorId } = this.state;

        RCApi.updateTaskParams({ monitorId, taskParams: value }).then((res: any) => {
            if (res.code === 1) {
                message.success('修改环境参数成功');
                this.setState({
                    visibleEnvModal: false
                });
                this.props.getMonitorDetail({ monitorId });
            }
        });
    };

    // 打开编辑弹窗
    openExecuteModal = () => {
        this.setState({
            showExecuteModal: true
        });
    };

    /**
     * 关闭编辑弹窗（是否更新数据）
     * @param {boolean} updated
     */
    closeExecuteModal = (updated: any) => {
        const { monitorId } = this.state;

        if (updated) {
            this.props.getMonitorDetail({ monitorId });
        }

        this.setState({
            showExecuteModal: false
        });
    };

    render () {
        const { data, ruleConfig } = this.props;
        const { monitorDetail, tableColumn } = ruleConfig;

        const {
            monitorId,
            havePart,
            showExecuteModal,
            visibleEnvModal
        } = this.state;
        let monitorPart = data.monitorPartVOS ? data.monitorPartVOS : [];

        return (
            <div className="rule-manage">
                <Row>
                    <Col span={12} className="txt-left">
                        {havePart && (
                            <div>
                                分区：
                                <Select
                                    style={{ width: 150 }}
                                    value={
                                        monitorId
                                            ? monitorId.toString()
                                            : undefined
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
                    </Col>

                    <Col span={12} className="txt-right">
                        <Button
                            type="primary"
                            onClick={this.executeMonitor.bind(this, monitorId)}
                        >
                            立即执行
                        </Button>
                        <Button
                            className="m-l-8"
                            type="primary"
                            onClick={this.openExecuteModal}
                        >
                            编辑调度属性
                        </Button>
                        <Button
                            className="m-l-8"
                            type="primary"
                            onClick={this.changeMonitorStatus.bind(
                                this,
                                monitorId
                            )}
                        >
                            {monitorDetail.isClosed ? '开启检测' : '关闭检测'}
                        </Button>
                        {monitorDetail &&
                            monitorDetail.sourceType === DATA_SOURCE.HIVE &&
                            (<Button
                                className="m-l-8"
                                type="primary"
                                onClick={() => {
                                    this.setState({
                                        visibleEnvModal: true
                                    });
                                }}
                            >环境参数</Button>)}
                    </Col>
                </Row>

                <div className="monitor-info-table">
                    <table style={{ width: '100%' }} cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <th>执行周期</th>
                                <td className="width-3">
                                    {monitorDetail.periodTypeName}
                                </td>
                                <th>告警方式</th>
                                <td className="width-3">
                                    {monitorDetail.sendTypeNames}
                                </td>
                            </tr>
                            <tr>
                                <th>执行时间</th>
                                <td className="width-3">
                                    {monitorDetail.executeTime}
                                </td>
                                <th>接收人</th>
                                <td className="width-3">
                                    {monitorDetail.notifyUser
                                        ? monitorDetail.notifyUser
                                            .map((item: any) => item.name)
                                            .join('，')
                                        : ''}
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <RuleList
                    key={monitorId}
                    tableColumn={tableColumn}
                    data={data}
                    onDeleteRule={this.deleteRule}
                    onSaveRule={this.onSaveRule}
                    getInitData={function () {
                        return RCApi.getMonitorRule({ monitorId }).then((res: any) => {
                            if (res.code === 1) {
                                return res.data;
                            }
                        })
                    }}
                />
                <ExecuteForm
                    data={monitorDetail}
                    visible={showExecuteModal}
                    closeModal={this.closeExecuteModal}
                />

                <EnvModal
                    key="ruleConfigEnvModal"
                    title="配置环境参数"
                    visible={visibleEnvModal}
                    onCancel={() =>
                        this.setState({
                            visibleEnvModal: false
                        })
                    }
                    value={monitorDetail.taskParams}
                    onOk={this.updateEnvParams}
                />
            </div>
        );
    }
}
export default Form.create<any>()(RuleEditPane);
