import React, { Component } from 'react';
import { connect } from 'react-redux';
import { isEmpty } from 'lodash';
import {
    Button,
    Form,
    Select,
    Input,
    Table,
    message,
    Popconfirm,
    InputNumber
} from 'antd';

import { ruleConfigActions } from '../../../actions/ruleConfig';
import {
    rowFormItemLayout,
    operatorSelect,
    operatorSelect1
} from '../../../consts';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;

const mapStateToProps = state => {
    const { ruleConfig, common } = state;
    return { ruleConfig, common };
};

const mapDispatchToProps = dispatch => ({
    getRuleFunction (params) {
        dispatch(ruleConfigActions.getRuleFunction(params));
    },
    getTableColumn (params) {
        dispatch(ruleConfigActions.getTableColumn(params));
    }
});

@connect(
    mapStateToProps,
    mapDispatchToProps
)
class StepTwo extends Component {
    constructor (props) {
        super(props);
        this.state = {
            currentRule: {},
            functionList: [],
            enumFields: ['columnName', 'functionId', 'thresholdEnum'],
            SQLFields: ['customizeSql', 'verifyType', 'operator', 'threshold'],
            columnFields: [
                'columnName',
                'functionId',
                'verifyType',
                'operator',
                'threshold'
            ]
        };
    }

    componentDidMount () {
        const { editParams } = this.props;

        this.props.getRuleFunction();
        this.props.getTableColumn({
            sourceId: editParams.dataSourceId,
            tableName: editParams.tableName
        });
    }

    prev = () => {
        const { currentStep, navToStep } = this.props;
        const { currentRule } = this.state;

        if (!isEmpty(currentRule)) {
            this.cancel(currentRule.id);
            navToStep(currentStep - 1);
            // message.error('监控规则未保存');
        } else {
            navToStep(currentStep - 1);
        }
    };

    next = () => {
        const { currentStep, navToStep, editParams } = this.props;
        const { currentRule } = this.state;

        if (editParams.rules.length) {
            if (!isEmpty(currentRule)) {
                this.save(currentRule.id, () => {
                    navToStep(currentStep + 1);
                });
                // message.error('监控规则未保存');
            } else {
                navToStep(currentStep + 1);
            }
        } else {
            message.error('请添加监控规则');
        }

        // if (editParams.rules.length && isEmpty(currentRule)) {
        //     navToStep(currentStep + 1);
        // } else {
        //     message.error('请添加监控规则');
        // }
    };

    // 规则列表配置
    initColumns = () => {
        return [
            {
                title: '字段/数据表',
                dataIndex: 'columnName',
                key: 'columnName',
                render: (text, record) =>
                    this.renderColumns(text, record, 'columnName'),
                width: '15%'
            },
            {
                title: '统计函数',
                dataIndex: 'functionId',
                key: 'functionId',
                render: (text, record) =>
                    this.renderColumns(text, record, 'functionId'),
                width: '15%'
            },
            {
                title: '过滤条件',
                dataIndex: 'filter',
                key: 'filter',
                render: (text, record) =>
                    this.renderColumns(text, record, 'filter'),
                width: '25%'
            },
            {
                title: '校验方法',
                dataIndex: 'verifyType',
                key: 'verifyType',
                render: (text, record) =>
                    this.renderColumns(text, record, 'verifyType'),
                width: '15%'
            },
            {
                title: '阈值配置（不符合阈值条件时触发告警）',
                dataIndex: 'threshold',
                key: 'threshold',
                render: (text, record) =>
                    this.renderColumns(text, record, 'threshold'),
                width: '20%'
            },
            {
                title: '操作',
                width: '10%',
                render: (text, record) => {
                    const { editable } = record;
                    return (
                        <div>
                            {editable ? (
                                <span>
                                    <a
                                        className="m-r-8"
                                        onClick={() => this.save(record.id)}
                                    >
                                        保存
                                    </a>
                                    <a onClick={() => this.cancel(record.id)}>
                                        取消
                                    </a>
                                </span>
                            ) : (
                                <span>
                                    <a
                                        className="m-r-8"
                                        onClick={() => this.edit(record.id)}
                                    >
                                        编辑
                                    </a>
                                    <Popconfirm
                                        title="确定要删除吗？"
                                        onConfirm={() => this.delete(record.id)}
                                    >
                                        <a>删除</a>
                                    </Popconfirm>
                                </span>
                            )}
                        </div>
                    );
                }
            }
        ];
    };

    renderColumns (text, record, type) {
        let obj = {
            children: null,
            props: {}
        };

        if (record.isCustomizeSql) {
            switch (type) {
                case 'columnName':
                    obj.props.colSpan = 3;
                    break;
                case 'functionId':
                    obj.props.colSpan = 0;
                    return obj;
                case 'filter':
                    obj.props.colSpan = 0;
                    return obj;
                default:
                    break;
            }
        }
        obj.children = (
            <Form layout="inline">
                {record.editable
                    ? this.renderEditTD(text, record, type)
                    : this.renderTD(text, record, type)}
            </Form>
        );
        return obj;
    }

    // 参数改变回调
    changeRuleParams = (type, value) => {
        let obj = {};
        obj[type] = value.target ? value.target.value : value;

        this.setState({ currentRule: { ...this.state.currentRule, ...obj } });
    };

    // 校验字段回调
    onColumnNameChange = name => {
        const { currentRule } = this.state;
        const { form, ruleConfig } = this.props;
        const { tableColumn, monitorFunction } = ruleConfig;

        let columnType = tableColumn.filter(item => item.key === name)[0].type;

        let functionList = monitorFunction[columnType];

        let fields = {};
        fields[`functionId@${currentRule.id}`] = undefined;

        form.setFieldsValue(fields);
        this.setState({
            functionList,
            currentRule: {
                ...currentRule,
                columnName: name,
                functionId: undefined
            }
        });
    };

    // 统计函数变化回调
    onFunctionChange = id => {
        const { form } = this.props;
        const { functionList, currentRule: currentRuleState } = this.state;

        let isPercentage = functionList.filter(item => item.id == id)[0]
            .isPercent;

        let nameZc = functionList.filter(item => item.id == id)[0].nameZc;

        let isEnum = nameZc === '枚举值';

        let currentRule = {
            ...this.state.currentRule,
            functionId: id,
            functionName: nameZc,
            verifyType: isEnum ? '1' : undefined,
            operator: isEnum ? 'in' : undefined,
            isPercentage: isPercentage,
            percentType: isPercentage === 1 ? 'limit' : 'free'
        };

        let fields = {};
        fields[`verifyType@${currentRuleState.id}`] = isEnum ? '1' : undefined;
        fields[`operator@${currentRuleState.id}`] = undefined;

        form.setFieldsValue(fields);

        this.setState({ currentRule });
    };

    // 校验方法变化回调
    onVerifyTypeChange = value => {
        const { verifyType } = this.props.common.allDict;

        let currentRule = {
            ...this.state.currentRule,
            verifyType: value,
            verifyTypeValue: verifyType.filter(item => item.value == value)[0]
                .name
        };

        if (currentRule.percentType !== 'limit') {
            currentRule.isPercentage = value == 1 ? 0 : 1;
        }

        this.setState({ currentRule });
    };

    // 编辑状态的TD
    renderEditTD = (text, record, type) => {
        const { form, common, ruleConfig } = this.props;
        const { getFieldDecorator } = form;
        const { tableColumn } = ruleConfig;
        const { verifyType } = common.allDict;
        const { currentRule, functionList } = this.state;

        const isStringLength = name => {
            return name === '字符串最大长度' || name === '字符串最小长度';
        };

        let operatorMap = isStringLength(currentRule.functionName)
            ? operatorSelect1
            : operatorSelect;

        switch (type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return (
                        <FormItem
                            {...rowFormItemLayout}
                            className="rule-edit-td cell-center"
                        >
                            {getFieldDecorator(`customizeSql@${record.id}`, {
                                rules: [
                                    {
                                        required: true,
                                        message: '自定义SQL不可为空'
                                    }
                                ],
                                initialValue: record.customizeSql
                            })(
                                record.isCustomizeSql ? (
                                    <TextArea
                                        style={{ resize: 'vertical' }}
                                        autosize={{ minRows: 3, maxRows: 8 }}
                                        placeholder="查询结果为一个数值类型"
                                        onChange={this.changeRuleParams.bind(
                                            this,
                                            'customizeSql'
                                        )}
                                    />
                                ) : (
                                    <Input
                                        placeholder="查询结果为一个数值类型"
                                        onChange={this.changeRuleParams.bind(
                                            this,
                                            'customizeSql'
                                        )}
                                        disabled={record.editStatus === 'edit'}
                                    />
                                )
                            )}
                        </FormItem>
                    );
                } else {
                    return (
                        <FormItem
                            {...rowFormItemLayout}
                            className="rule-edit-td cell-center"
                        >
                            {getFieldDecorator(`columnName@${record.id}`, {
                                rules: [
                                    {
                                        required: true,
                                        message: '字段不可为空'
                                    }
                                ],
                                initialValue: record.columnName
                            })(
                                <Select
                                    showSearch
                                    onChange={this.onColumnNameChange}
                                    disabled={record.isTable}
                                >
                                    {tableColumn.map(item => {
                                        return (
                                            <Option
                                                key={item.key}
                                                value={item.key}
                                            >
                                                {item.key}
                                            </Option>
                                        );
                                    })}
                                </Select>
                            )}
                        </FormItem>
                    );
                }
            }

            case 'functionId': {
                return (
                    <FormItem
                        {...rowFormItemLayout}
                        className="rule-edit-td cell-center"
                    >
                        {getFieldDecorator(`functionId@${record.id}`, {
                            rules: [
                                {
                                    required: true,
                                    message: '统计函数不可为空'
                                }
                            ],
                            initialValue: record.functionId
                        })(
                            <Select onChange={this.onFunctionChange}>
                                {functionList.map(item => {
                                    return (
                                        <Option
                                            key={item.id}
                                            value={item.id.toString()}
                                        >
                                            {item.nameZc}
                                        </Option>
                                    );
                                })}
                            </Select>
                        )}
                    </FormItem>
                );
            }

            case 'filter': {
                return (
                    <FormItem
                        {...rowFormItemLayout}
                        className="rule-edit-td cell-center"
                    >
                        {getFieldDecorator(`filter@${record.id}`, {
                            rules: [],
                            initialValue: record.filter
                        })(
                            <Input
                                placeholder={`以"and"开头的条件语句，例：and colA = "value"`}
                                onChange={this.changeRuleParams.bind(
                                    this,
                                    'filter'
                                )}
                            />
                        )}
                    </FormItem>
                );
            }

            case 'verifyType': {
                return (
                    <FormItem
                        {...rowFormItemLayout}
                        className="rule-edit-td cell-center"
                    >
                        {getFieldDecorator(`verifyType@${record.id}`, {
                            rules: [
                                {
                                    required: true,
                                    message: '校验方法不可为空'
                                }
                            ],
                            initialValue: record.verifyType
                        })(
                            <Select
                                onChange={this.onVerifyTypeChange}
                                disabled={currentRule.operator === 'in'}
                            >
                                {verifyType.map(item => {
                                    return (
                                        <Option
                                            key={item.value}
                                            value={item.value.toString()}
                                        >
                                            {item.name}
                                        </Option>
                                    );
                                })}
                            </Select>
                        )}
                    </FormItem>
                );
            }

            case 'threshold': {
                if (currentRule.operator === 'in') {
                    return (
                        <FormItem
                            {...rowFormItemLayout}
                            className="rule-edit-td cell-center"
                        >
                            {getFieldDecorator(`thresholdEnum@${record.id}`, {
                                rules: [
                                    {
                                        required: true,
                                        message: '范围不可为空'
                                    }
                                ],
                                initialValue: record.threshold
                            })(
                                <Input
                                    placeholder="枚举格式为(value1,value2,.....)"
                                    onChange={this.changeRuleParams.bind(
                                        this,
                                        'threshold'
                                    )}
                                />
                            )}
                        </FormItem>
                    );
                } else {
                    return (
                        <div className="cell-center-box">
                            <FormItem className="cell-multiple-center">
                                {getFieldDecorator(`operator@${record.id}`, {
                                    rules: [
                                        {
                                            required: true,
                                            message: '设置不可为空'
                                        }
                                    ],
                                    initialValue: record.operator
                                })(
                                    <Select
                                        style={{ width: 80 }}
                                        onChange={this.changeRuleParams.bind(
                                            this,
                                            'operator'
                                        )}
                                    >
                                        {operatorMap.map(item => {
                                            return (
                                                <Option
                                                    key={item.value}
                                                    value={item.value}
                                                >
                                                    {item.text}
                                                </Option>
                                            );
                                        })}
                                    </Select>
                                )}
                            </FormItem>
                            <FormItem className="cell-multiple-center">
                                {getFieldDecorator(`threshold@${record.id}`, {
                                    rules: [
                                        {
                                            required: true,
                                            message: '阈值不可为空'
                                        }
                                    ],
                                    initialValue: record.threshold
                                })(
                                    <InputNumber
                                        style={{ width: 80, marginRight: 10 }}
                                        onChange={this.changeRuleParams.bind(
                                            this,
                                            'threshold'
                                        )}
                                    />
                                )}
                            </FormItem>
                            {currentRule.isPercentage === 1 && (
                                <span
                                    style={{ height: 32, lineHeight: '32px' }}
                                >
                                    %
                                </span>
                            )}
                        </div>
                    );
                }
            }
        }
    };

    // 已有数据
    renderTD = (text, record, type) => {
        switch (type) {
            case 'columnName': {
                return record.isCustomizeSql ? record.customizeSql : text;
            }
            case 'functionId': {
                return record.functionName;
            }
            case 'verifyType': {
                return record.verifyTypeValue;
            }
            case 'threshold': {
                let value = `${
                    record.operator ? record.operator : ''
                }  ${text}`;
                return record.isPercentage ? `${value} %` : value;
            }
            default:
                return text;
        }
    };

    // 编辑规则
    edit (id) {
        const { currentRule } = this.state;
        const { ruleConfig } = this.props;
        const { monitorFunction, tableColumn } = ruleConfig;
        let functionList = [];

        let newData = [...this.props.editParams.rules];

        let target = newData.filter(item => id === item.id)[0];

        if (!isEmpty(currentRule)) {
            let current = newData.filter(item => currentRule.id === item.id)[0];
            if (current.editStatus === 'edit') {
                delete current.editable;
                delete current.editStatus;
            } else {
                newData.shift();
            }
        }

        if (target) {
            target.editable = true;
            target.editStatus = 'edit';
            if (!target.isCustomizeSql) {
                if (target.isTable) {
                    functionList = monitorFunction.all.filter(
                        item => item.level === 1
                    );
                } else {
                    let columnType = tableColumn.filter(
                        item => item.key === target.columnName
                    )[0].type;
                    functionList = monitorFunction[columnType];
                }
            }
            this.setState({ currentRule: target, functionList: functionList });
            this.props.changeParams({ rules: newData });
        }
    }

    // 取消编辑
    cancel (id) {
        let newData = [...this.props.editParams.rules];

        let target = newData.filter(item => id === item.id)[0];

        let index = newData.indexOf(target);

        if (target.editStatus === 'edit') {
            delete target.editable;
            delete target.editStatus;
        } else {
            newData.splice(index, 1);
        }

        this.setState({ currentRule: {} });
        this.props.changeParams({ rules: newData });
    }

    // 删除规则
    delete (id) {
        let newData = [...this.props.editParams.rules];

        let target = newData.filter(item => id === item.id)[0];

        let index = newData.indexOf(target);

        if (target) {
            newData.splice(index, 1);
            this.props.changeParams({ rules: newData });
        }
    }

    checkRepeat () {
        const { currentRule } = this.state;
        const newData = [...this.props.editParams.rules];
        const keys = [
            'columnName',
            'filter',
            'functionId',
            'verifyType',
            'customizeSql',
            'operator'
        ];
        let pass = true;

        for (let i = 0; i < newData.length; i++) {
            const item = newData[i];
            let itemPass = false; // 当前元素校验是否通过

            if (item.id == currentRule.id) {
                continue;
            }

            for (let j = 0; j < keys.length; j++) {
                let key = keys[j];
                if (currentRule[key] != item[key]) {
                    itemPass = true; // 检测到一项不同，通过。
                    break;
                }
            }
            if (!itemPass) {
                pass = false;
                break;
            }
        }
        return pass;
    }

    // 保存规则
    save (id, callback) {
        // const { currentRule, enumFields, SQLFields, columnFields } = this.state;
        const { currentRule } = this.state;

        let newData = [...this.props.editParams.rules];

        let target = newData.filter(item => id === item.id)[0];

        let index = newData.indexOf(target);

        // let fields = currentRule.isCustomizeSql ? SQLFields : columnFields;

        // if (currentRule.operator === 'in') {
        //     fields = enumFields;
        // }
        if (!this.checkRepeat()) {
            message.error('规则不能重复！');
            return;
        }
        this.props.form.validateFields(null, {}, (err, values) => {
            console.log(err, values);
            if (!err) {
                delete currentRule.editStatus;
                delete currentRule.editable;
                newData[index] = currentRule;
                this.setState({ currentRule: {} }, () => {
                    if (callback) {
                        callback();
                    }
                });
                this.props.changeParams({ rules: newData });
            }
        });
    }
    addNewRuleWrap = type => {
        const { currentRule } = this.state;
        if (!isEmpty(currentRule)) {
            this.save(currentRule.id, () => {
                this.addNewRule(type);
            });
        } else {
            this.addNewRule(type);
        }
    };
    // 新增规则
    addNewRule = type => {
        const { form, editParams, ruleConfig } = this.props;
        const { monitorFunction } = ruleConfig;
        const { currentRule } = this.state;

        let newData = [...editParams.rules];

        if (!isEmpty(currentRule)) {
            if (currentRule.editStatus === 'edit') {
                delete currentRule.editable;
                delete currentRule.editStatus;
            } else {
                newData.shift();
                form.resetFields();
                this.setState({
                    currentRule: {},
                    functionList: []
                });
            }
        }

        let target = {
            id: newData[0] ? newData[0].id + 1 : 1,
            editStatus: 'new',
            editable: true,
            filter: '',
            verifyType: undefined,
            operator: undefined,
            threshold: undefined
        };

        switch (type) {
            case 'column':
                target.isCustomizeSql = false;
                target.columnName = undefined;
                target.functionId = undefined;
                break;
            case 'SQL':
                target.isCustomizeSql = true;
                target.customizeSql = undefined;
                break;
            case 'table':
                target.isTable = true;
                target.isCustomizeSql = false;
                target.columnName = editParams.tableName;
                target.functionId = undefined;

                // 表规则的统计函数
                this.setState({
                    functionList: monitorFunction.all.filter(
                        item => item.level === 1
                    )
                });
                break;
            default:
                break;
        }

        newData.unshift(target);
        this.setState({ currentRule: target });
        this.props.changeParams({ rules: newData });
    };

    render () {
        const { rules } = this.props.editParams;
        return (
            <div>
                <div className="steps-content">
                    <div className="rule-action">
                        <Button
                            type="primary"
                            onClick={this.addNewRuleWrap.bind(this, 'table')}
                        >
                            添加表级规则
                        </Button>
                        <Button
                            type="primary"
                            className="m-l-8"
                            onClick={this.addNewRuleWrap.bind(this, 'column')}
                        >
                            添加字段级规则
                        </Button>
                        <Button
                            type="primary"
                            className="m-l-8"
                            onClick={this.addNewRuleWrap.bind(this, 'SQL')}
                        >
                            添加自定义SQL
                        </Button>
                    </div>

                    <Table
                        rowKey="id"
                        className="m-table rule-edit-table"
                        style={{ margin: '0 20px' }}
                        columns={this.initColumns()}
                        pagination={false}
                        dataSource={rules}
                        onChange={this.onTableChange}
                    />
                </div>

                <div className="steps-action">
                    <Button onClick={this.prev}>上一步</Button>
                    <Button
                        className="m-l-8"
                        type="primary"
                        onClick={this.next}
                    >
                        下一步
                    </Button>
                </div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
