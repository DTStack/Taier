import React, { Component } from 'react';
import { connect } from 'react-redux';
import { Form, Input, Button, Icon, message, Select, InputNumber } from 'antd';
import { ruleConfigActions } from '../../../actions/ruleConfig';
import { dataSourceActions } from '../../../actions/dataSource';

const Option = Select.Option;

const mapStateToProps = state => {
    const { ruleConfig, dataSource, common } = state;
    return { ruleConfig, dataSource, common }
}

@connect(mapStateToProps)
export default class RuleEditTD extends Component {
    constructor(props) {
        super(props);
        this.state = {}
    }

    componentDidMount() {
        // const { type, editable, editParams } = this.props;
        // switch (type) {
        //     case 'columnName': 
        //         this.props.getDataSourcesColumn({
        //             sourceId: editParams.dataSourceId,
        //             tableName: editParams.tableName
        //         });
        //         break;
        //     case 'functionId': 
        //         this.props.getMonitorFunction();
        //         break;
        //     default: 
        //         break;
        // }
    }

    componentWillReceiveProps(nextProps) {
        

    }

    renderEditTD = () => {
        const { getFieldDecorator } = this.props.form;
        const { sourceColumn } = this.props.dataSource;
        const { monitorFunction } = this.props.ruleConfig;
        const { allDict } = this.props.common;
        const { value, record, type, data } = this.props;

        switch(type) {
            case 'columnName': {
                if (data.isCustomizeSql) {
                    return <div>
                        {
                            getFieldDecorator('customizeSql', {
                                rules: [{
                                    required: true, message: '自定义SQL不可为空！',
                                }],
                                initialValue: data.customizeSql
                            })(
                                <Input onChange={this.changeRuleParams.bind(this, 'customizeSql')}/>
                            )
                        }
                    </div>
                } else {
                    return (
                        <div>
                        {
                            getFieldDecorator('columnName', {
                                rules: [{
                                    required: true, message: '字段不可为空！',
                                }],
                                initialValue: data.columnName
                            })(
                                <Select style={{ width: '100%' }} onChange={this.changeRuleParams.bind(this, 'columnName')}>
                                    {
                                        sourceColumn.map((item) => {
                                            return <Option key={item.key} value={item.key}>
                                                {item.key}
                                            </Option>
                                        })
                                    }
                                </Select>
                            )
                        }
                        </div>
                    )
                }
            }

            case 'functionId': {
                return (
                    <div>
                    {
                        getFieldDecorator('functionId', {
                            rules: [{
                                required: true, message: '统计函数不可为空！',
                            }],
                            initialValue: data.functionId
                        })(
                            <Select style={{ width: '100%' }} onChange={this.changeRuleParams.bind(this, 'functionId')}>
                                {
                                    monitorFunction.map((item) => {
                                        return <Option key={item.id} value={item.id.toString()}>
                                            {item.nameZc}
                                        </Option>
                                    })
                                }
                            </Select>
                        )
                    }
                    </div>
                )
            }

            case 'filter': {
                return <div>
                    {
                        getFieldDecorator('filter', {
                            rules: [{
                                required: true, message: '过滤条件不可为空！',
                            }],
                            initialValue: data.filter
                        })(
                            <Input onChange={this.changeRuleParams.bind(this, 'filter')}/>
                        )
                    }
                </div>
            }

            case 'verifyType': {
                return <div>
                    {
                        getFieldDecorator('verifyType', {
                            rules: [{
                                required: true, message: '校验方法不可为空！',
                            }],
                            initialValue: data.verifyType
                        })(
                            <Select style={{ width: '100%' }} onChange={this.changeRuleParams.bind(this, 'verifyType')}>
                                {
                                    allDict.verifyType.map((item) => {
                                        return <Option key={item.value} value={item.value.toString()}>
                                            {item.name}
                                        </Option>
                                    })
                                }
                            </Select>
                        )
                    }
                </div>
            }

            case 'threshold': {
                return <div>
                    {
                        getFieldDecorator('operator', {
                            rules: [{
                                required: true, message: '阈值配置不可为空！',
                            }],
                            initialValue: data.operator
                        })(
                            <Select style={{ width: 70, marginRight: 10 }} onChange={this.changeRuleParams.bind(this, 'operator')}>
                                <Option value=">"> {`>`} </Option>
                                <Option value=">="> {`>=`} </Option>
                                <Option value="="> {`=`} </Option>
                                <Option value="<"> {`<`} </Option>
                                <Option value="<="> {`<=`} </Option>
                                <Option value="!="> {`!=`} </Option>
                            </Select>
                        )
                    }
                    {
                        getFieldDecorator('threshold', {
                            rules: [{
                                required: true, message: '阈值不可为空！',
                            }],
                            initialValue: data.threshold
                        })(
                            <InputNumber
                              min={0}
                              max={100}
                              style={{ marginRight: 10 }}
                              onChange={this.changeRuleParams.bind(this, 'threshold')}
                            /> 
                        )
                    }
                    {
                        data.verifyType != 1
                        &&
                        <span>%</span>
                    }
                </div>
            }
        }
    }

    changeRuleParams = (type, value) => {
        const { changeCurrentRule } = this.props;
        let obj = {};

        obj[type] = value.target ? value.target.value : value;
        changeCurrentRule(obj);
    }

    // 固定的值
    renderTD = () => {
        const { monitorFunction } = this.props.ruleConfig;
        const { allDict } = this.props.common;
        const { value, record, type } = this.props;

        switch (type) {
            case 'columnName': {
                if (record.isCustomizeSql) {
                    return record.customizeSql
                } else {
                    return value
                }
            }
            case 'functionId': {
                return monitorFunction[value-1].nameZc || undefined
            }
            case 'verifyType': {
                
                return allDict.verifyType[value-1].name || undefined
            }
            case 'threshold': {
                return value ? `${record.operator}  ${value}` : 0
            }
            default:
                return value
        }
    }

    render() {
        const { editable } = this.props;
        return (
            <div className="rule-editable-cell">
            {
                editable ?
                this.renderEditTD()
                :
                this.renderTD()
            }
            </div>
        );
    }
}
RuleEditTD = Form.create()(RuleEditTD);

