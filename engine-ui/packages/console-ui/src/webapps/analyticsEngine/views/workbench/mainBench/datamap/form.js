import React, { Component } from "react";

import {
    Input,
    Form,
    Radio,
    Select,
    Checkbox,
    InputNumber,
} from "antd";

import Editor from 'widgets/editor';

import HelpDoc, { relativeStyle } from '../../../../components/helpDoc';

const RadioGroup = Radio.Group;

const FormItem = Form.Item;
const Option = Select.Option;

const DATAMAP_TYPE = {
    PRE_SUM: 0,
    TIME_SEQUENCE: 1,
    FILTER: 2,
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
            width: 500,
        }
    },
};

const editorOptions = {
    minimap: {
        enabled: false,
    },
}

class DataMapForm extends Component {

    state = {
        datamapType: DATAMAP_TYPE.PRE_SUM,
    };

    onDataMapTypeChange = (e) => {
        this.setState({
            datamapType: e.target.value,
        });
    }

    onQueryTextChange = (value) => {
        this.props.form.setFieldsValue({
            'configJSON.selectSql': value,
        });
    }

    dynamicRender = (timestampColumns) => {
        const { datamapType } = this.state;
        const { form, data, tableData } = this.props;
        const { getFieldDecorator } = form;

        const config = data.config ? JSON.parse(data.config) : undefined;
        const defaultQueryText = '-- 支持对字段进行SUM、AVG、MAX、MIN、COUNT函数的预聚合处理';

        const editorInput = <Editor
            style={{
                height: '200px',
                width: '100%',
                border: '1px solid #dddddd',
            }}
            language="sql"
            options={editorOptions}
            onChange={this.onQueryTextChange}
            value={config ? config.selectSql : defaultQueryText}
        />
    
        const tableColumns = tableData ? tableData.columns : [];

        const timeColumnsOptions = tableColumns && tableColumns.map(opt => (
            <Option key={`${opt.name}`} value={`${opt.name}`}>{opt.name}</Option>
        ))

        const timeColumnsOptionsForSeq = timestampColumns && timestampColumns.map(opt => 
            <Option key={`${opt.name}`} value={`${opt.name}`}>{opt.name}</Option>
        )

        switch(datamapType) {
            case DATAMAP_TYPE.TIME_SEQUENCE: {

                return ([
                    <FormItem {...formItemLayout} label="时间字段" hasFeedback>
                        {getFieldDecorator("configJSON.timeColumn", {
                            rules: [
                                {
                                    required: true,
                                    message: "时间字段不可为空！"
                                }
                            ],
                            initialValue: config ? config.timeColumn : '',
                        })(
                            <Select placeholder="请选择时间字段">
                                {timeColumnsOptionsForSeq}
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="时间粒度" hasFeedback>
                        {getFieldDecorator("configJSON.timeType", { 
                            rules: [
                                {
                                    required: true,
                                    message: "时间字段不可为空！"
                                }
                            ],
                            initialValue: config ? config.time : 1,
                        })(
                            <Checkbox.Group>
                                <Checkbox value="4">年</Checkbox>
                                <Checkbox value="3">月</Checkbox>
                                <Checkbox value="2">日</Checkbox>
                                <Checkbox value="1">小时</Checkbox>
                                <Checkbox value="0">分钟</Checkbox>
                            </Checkbox.Group>
                        )}
                    </FormItem>,
                    <FormItem {...formItemLayout} label="主表查询">
                        {getFieldDecorator("configJSON.selectSql", {
                            rules: [
                                {
                                    required: true,
                                    message: "主表查询不可为空！"
                                }
                            ],
                            initialValue: config ? config.selectSql : undefined,
                        })(
                            <Input type="hidden" />
                        )}
                        { editorInput }
                    </FormItem>
                ])
            }
            case DATAMAP_TYPE.FILTER: {
                return ([
                    <FormItem key="timeColumn" {...formItemLayout} label="时间字段" hasFeedback>
                        {getFieldDecorator("configJSON.timeColumn", {
                            rules: [
                                {
                                    required: true,
                                    message: "时间字段不可为空！"
                                }
                            ],
                            initialValue: config ? config.timeColumn : tableColumns.length > 0 ? tableColumns[0].name : '',
                        })(
                            <Select placeholder="请选择时间字段"> 
                                {timeColumnsOptions} 
                            </Select>
                        )}
                    </FormItem>,
                    <FormItem key="bloomSize" {...formItemLayout} label="Bloom Size" hasFeedback>
                        {getFieldDecorator("configJSON.bloomSize", {
                            rules: [],
                            initialValue: config ? config.bloomSize : 32000,
                        })(
                            <InputNumber min={32000} style={{width: '100%'}}/>
                        )}
                        <HelpDoc doc="bloomSizeSummary" />
                    </FormItem>,
                    <FormItem key="bloomFPP" {...formItemLayout} label="Bloom FPP" hasFeedback>
                        {getFieldDecorator("configJSON.bloomFP", {
                            rules: [],
                            initialValue: config ? config.bloomFP : 1,
                        })(
                            <InputNumber min={0} max={100} style={{width: '100%'}}/>
                        )}
                        <HelpDoc doc="bloomFPPSummary" />
                    </FormItem>,
                    <FormItem key="bloomConpress" {...formItemLayout} label="是否压缩索引文件">
                        {getFieldDecorator("configJSON.bloomConpress", {
                            rules: [],
                            initialValue: config ? config.bloomConpress : true,
                        })(
                            <RadioGroup>
                                <Radio value={true}>是</Radio>
                                <Radio value={false}>否</Radio>
                            </RadioGroup>
                        )}
                        <HelpDoc style={relativeStyle} doc="isCompressIndex" />
                    </FormItem>
                ])
            }
            case DATAMAP_TYPE.PRE_SUM:
            default: {
                return (
                    <FormItem {...formItemLayout} label="主表查询">
                        {getFieldDecorator("configJSON.selectSql", {
                            rules: [
                                {
                                    required: true,
                                    message: "主表查询不可为空！"
                                }
                            ],
                            initialValue: config ? config.selectSql : undefined,
                        })(
                            <Input type="hidden" />
                        )}
                        {editorInput}
                    </FormItem>
                )
            }
        }
    }

    render() {
        const { datamapType } = this.state;
        const { form, data, onGenerateCreateSQL, tableData, isCreate } = this.props;
        const { getFieldDecorator } = form;

        const tableColumns = tableData ? tableData.columns : [];
        const timestampColumns = tableColumns.filter(item => item.timestamp);
        const isDisable = timestampColumns && timestampColumns.length > 0 
        ? false : true;

        return (
            <Form>
                <FormItem style={{ margin: 0 }}>
                    {getFieldDecorator("databaseId", {
                        initialValue: tableData ? tableData.databaseId : undefined
                    })(
                        <Input type="hidden" />
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="主表">
                    {getFieldDecorator("tableId", {
                        rules: [],
                        initialValue: tableData ? tableData.id : undefined
                    })(
                        <Input type="hidden" />
                    )}
                    <span>
                        <span style={{ marginRight: 10, marginLeft: 6 }}>{tableData ? tableData.tableName : ""}</span>
                        <a onClick={() => {
                            onGenerateCreateSQL(tableData ? tableData.id : null)
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
                    })(
                        <Input autoComplete="off" placeholder="请输入DataMap名称"/>
                    )}
                </FormItem>
                <FormItem {...formItemLayout} label="DataMap类型">
                    {getFieldDecorator("type", {
                        rules: [
                            {
                                required: true,
                                message: "DataMap类型不可为空！"
                            }
                        ],
                        initialValue: (data && data.datamapType) || datamapType,
                    })(
                        <RadioGroup onChange={this.onDataMapTypeChange} disabled={!isCreate}>
                            <Radio value={DATAMAP_TYPE.PRE_SUM}>预聚合</Radio>
                            <Radio value={DATAMAP_TYPE.TIME_SEQUENCE} disabled={isDisable}>时间序列</Radio>
                            <Radio value={DATAMAP_TYPE.FILTER}>布隆过滤器</Radio>
                        </RadioGroup>
                    )}
                    <HelpDoc style={relativeStyle} doc="dataMapTypeSummary" />
                </FormItem>
                {this.dynamicRender(timestampColumns)}
            </Form>
        );
    }
}

const FormWrapper = Form.create()(DataMapForm);

export default FormWrapper;
