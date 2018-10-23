import React, { Component } from "react";

import {
    Input,
    Form,
    Radio,
    Select,
    Checkbox,
} from "antd";

import HelpDoc, { relativeStyle } from '../../../../components/helpDoc';

const RadioGroup = Radio.Group;

const FormItem = Form.Item;
const Option = Select.Option;

const DATAMAP_TYPE = {
    PRE_SUM: 1,
    TIME_SEQUENCE: 2,
    FILTER: 3,
}

export const formItemLayout = { // 表单常用布局
    labelCol: {
        style: {
            width: 130,
            float: 'left',
        }
    },
    wrapperCol: {
        style: {
            float: 'left',
            width: 400,
        }
    },
};

class DataMapForm extends Component {

    state = {
        datamapType: DATAMAP_TYPE.PRE_SUM,
    };

    onDataMapTypeChange = (e) => {
        console.log('radio checked', e.target.value);
        this.setState({
            datamapType: e.target.value,
        });
    }

    dynamicRender = () => {
        const { datamapType } = this.state;
        const { form, data } = this.props;
        const { getFieldDecorator } = form;

        const config = data.configJSON ? JSON.parse(data.configJSON) : undefined;

        switch(datamapType) {
            case DATAMAP_TYPE.TIME_SEQUENCE: {
                return ([
                    <FormItem {...formItemLayout} label="时间字段" hasFeedback>
                        {getFieldDecorator("configJSON.time", {
                            rules: [
                                {
                                    required: true,
                                    message: "时间字段不可为空！"
                                }
                            ],
                            initialValue: config ? config.time : undefined,
                        })(
                            <Select>
                                <Option value={0}>单选下拉列表</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="时间粒度" hasFeedback>
                        {getFieldDecorator("configJSON.timeAccuracy", { 
                            rules: [
                                {
                                    required: true,
                                    message: "时间字段不可为空！"
                                }
                            ],
                            initialValue: config ? config.time : [],
                        })(
                            <Checkbox.Group>
                                <Checkbox value="year">年</Checkbox>
                                <Checkbox value="month">月</Checkbox>
                                <Checkbox value="day">日</Checkbox>
                                <Checkbox value="hour">小时</Checkbox>
                                <Checkbox value="minute">分钟</Checkbox>
                            </Checkbox.Group>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="主表查询" hasFeedback>
                        {getFieldDecorator("configJSON.query", {
                            rules: [
                                {
                                    required: true,
                                    message: "主表查询不可为空！"
                                }
                            ],
                            initialValue: config ? config.query : undefined,
                        })(
                            <Input 
                                type="textarea"
                                placeholder="支持对字段进行SUM、AVG、MAX、MIN、COUNT函数的预聚合处理" 
                                autosize={{ minRows: 10, maxRows: 400 }}
                            />
                        )}
                    </FormItem>
                ])
            }
            case DATAMAP_TYPE.FILTER: {
                return ([
                    <FormItem {...formItemLayout} label="时间字段" hasFeedback>
                        {getFieldDecorator("configJSON.time", {
                            rules: [
                                {
                                    required: true,
                                    message: "时间字段不可为空！"
                                }
                            ],
                            initialValue: config ? config.time : undefined,
                        })(
                            <Select>
                                <Option value="0">单选下拉列表</Option>
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="Bloom Size" hasFeedback>
                        {getFieldDecorator("configJSON.bloomSize", {
                            rules: [{
                                min: 32000,
                                message: 'BloomSize应该是 32000 * #noOfPagesInBlocklet, 且必须填写整数'
                            }],
                            initialValue: config ? config.bloomSize : undefined,
                        })(
                            <Input />
                        )}
                        <HelpDoc doc="bloomSizeSummary" />
                    </FormItem>,
                    <FormItem {...formItemLayout} label="Bloom FPP" hasFeedback>
                        {getFieldDecorator("configJSON.bloomFPP", {
                            rules: [
                                {
                                    min: 0,
                                    max: 100,
                                    message: 'bloomFPP值的范围应该在 (0, 100) 之间的整数'
                                }
                            ],
                            initialValue: config ? config.bloomFPP : undefined,
                        })(
                            <Input />
                        )}
                        <HelpDoc doc="bloomFPPSummary" />
                    </FormItem>,
                    <FormItem {...formItemLayout} label="是否压缩索引文件">
                        {getFieldDecorator("configJSON.isCompressIndex", {
                            rules: [],
                            initialValue: config ? config.isCompressIndex : 1,
                        })(
                            <RadioGroup>
                                <Radio value={1}>是</Radio>
                                <Radio value={0}>否</Radio>
                            </RadioGroup>
                        )}
                        <HelpDoc style={relativeStyle} doc="isCompressIndex" />
                    </FormItem>
                ])
            }
            case DATAMAP_TYPE.PRE_SUM:
            default: {
                return (
                    <FormItem {...formItemLayout} label="主表查询" hasFeedback>
                        {getFieldDecorator("configJSON.query", {
                            rules: [
                                {
                                    required: true,
                                    message: "主表查询不可为空！"
                                }
                            ],
                            initialValue: config ? config.query : undefined,
                        })(
                            <Input 
                                type="textarea" 
                                placeholder="支持对字段进行SUM、AVG、MAX、MIN、COUNT函数的预聚合处理" 
                                autosize={{ minRows: 10, maxRows: 400 }}
                            />
                        )}
                    </FormItem>
                )
            }
        }
    }

    render() {
        const { datamapType } = this.state;
        const { form, data, onGenerateCreateSQL } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Form>
                <FormItem>
                    {getFieldDecorator("databaseId", {
                        initialValue: data && data.databaseId ? data.databaseId.id : undefined
                    })(
                        <Input type="hidden" />
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="主表" hasFeedback>
                    {getFieldDecorator("tableId", {
                        rules: [],
                        initialValue: data && data.table ? data.table.id : undefined
                    })(
                        <Input type="hidden" />
                    )}
                    <span>
                        <span style={{ marginRight: 10 }}>{data && data.table ? data.table.name : ""}</span>
                        <a onClick={() => {
                            onGenerateCreateSQL(data.table ? data.table.id : null)
                        }}>生成建表语句</a>
                    </span>
                </FormItem>
                <FormItem {...formItemLayout} label="DataMap名称" hasFeedback>
                    {getFieldDecorator("name", {
                        rules: [
                            {
                                required: true,
                                message: "DataMap名称不可为空！"
                            },
                            {
                                max: 20,
                                message: "DataMap名称不得超过20个字符！"
                            },
                            {
                                pattern: /^[A-Za-z0-9_]+$/,
                                message:
                                    "DataMap名称只能由字母与数字、下划线组成"
                            }
                        ],
                        initialValue: data ? data.name : ""
                    })(<Input autoComplete="off" />)}
                </FormItem>
                <FormItem {...formItemLayout} label="DataMap类型">
                    {getFieldDecorator("datamapType", {
                        rules: [
                            {
                                required: true,
                                message: "DataMap类型不可为空！"
                            }
                        ],
                        initialValue: (data && data.datamapType) || datamapType,
                    })(
                        <RadioGroup onChange={this.onDataMapTypeChange}>
                            <Radio value={DATAMAP_TYPE.PRE_SUM}>预聚合</Radio>
                            <Radio value={DATAMAP_TYPE.TIME_SEQUENCE}>时间序列</Radio>
                            <Radio value={DATAMAP_TYPE.FILTER}>布隆过滤器</Radio>
                        </RadioGroup>
                    )}
                    <HelpDoc style={relativeStyle} doc="dataMapTypeSummary" />
                </FormItem>
                {this.dynamicRender()}
            </Form>
        );
    }
}

const FormWrapper = Form.create()(DataMapForm);

export default FormWrapper;
