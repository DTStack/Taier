import * as React from 'react';
import { connect } from 'react-redux';

import {
    Select, Form, Card,
    Button, message
} from 'antd';

import {
    formItemLayout,
    tableModelRules,
    TABLE_MODEL_RULE
} from '../../../comm/const';

import Api from '../../../api/dataModel';

const Option = Select.Option;
const FormItem = Form.Item;

const defaultRule: any = {
    value: TABLE_MODEL_RULE.CUSTOM,
    name: '自定义'
};

@(connect((state: any) as any) => {
    return {
        project: state.project
    }
})
class ModelDefineRule extends React.Component<any, any> {
    state: any = {
        tbNameRules: []
    }

    componentDidMount () {
        this.loadTbNameRules();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        const project = nextProps.project
        const oldProj = this.props.project
        if (oldProj && project && oldProj.id !== project.id) {
            this.loadTbNameRules();
        }
    }

    loadTbNameRules = () => {
        Api.getTableNameRules().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    tbNameRules: res.data.length > 0 ? res.data : [tableModelRules[0]]
                })
            }
        })
    }

    submit = (e: any) => {
        e.preventDefault()
        const formData: any = {
            rule: this.state.tbNameRules
        }
        Api.createModelRule(formData).then((res: any) => {
            if (res.code === 1) {
                message.success('保存规则成功！')
            }
        })
    }

    changeTbNameRule = (valueOption: any, index: any) => {
        const optionIndex = valueOption.props.index;
        const newArrs: any = [...this.state.tbNameRules];

        newArrs[index] = tableModelRules[optionIndex];
        this.setState({
            tbNameRules: newArrs
        });
    }

    insertTbNameRule = (index: any) => {
        const originArr = this.state.tbNameRules;
        const start = index + 1;
        let arrOne = originArr.slice(0, start);
        const arrTwo = originArr.slice(start, originArr.length);

        // Insert a default object to array.
        arrOne.push(defaultRule);

        arrOne = arrOne.concat(arrTwo);

        this.setState({
            tbNameRules: [...arrOne]
        });
    }

    appendTbNameRule = (index: any) => {
        const originArr: any = [...this.state.tbNameRules];
        // Append new one.
        originArr.push(defaultRule)
        this.setState({
            tbNameRules: originArr
        });
    }

    removeTbNameRule = (index: any) => {
        const originArr: any = [...this.state.tbNameRules];
        originArr.splice(index, 1)
        this.setState({
            tbNameRules: originArr
        });
    }

    isDisabled = (rule: any) => {
        const tbNameRules = this.state.tbNameRules;
        return rule.value !== TABLE_MODEL_RULE.CUSTOM && tbNameRules.find((item: any) => item.value === rule.value)
    }

    renderTableNameRules = () => {
        const { tbNameRules } = this.state;

        const options = tableModelRules.map((rule: any, index: any) => <Option
            key={rule.value}
            index={index}
            disabled={this.isDisabled(rule)}
            value={rule.value}
        >
            {rule.name}
        </Option>);

        return tbNameRules && tbNameRules.map((rule: any, index: any) => <span
            style={{ display: 'inline-block', marginBottom: '5px' }}
            key={index}>
            <Select
                placeholder="请选择"
                value={rule.value}
                style={{ width: 126, marginRight: '5px' }}
                onSelect={(value: any, option: any) => this.changeTbNameRule(option, index)}
            >
                {options}
            </Select>
        </span>
        );
    }

    renderTableName = () => {
        const { tbNameRules } = this.state;
        const names: any = [];
        for (let i = 0; i < tbNameRules.length; i++) {
            const rule = tbNameRules[i];
            switch (rule.value) {
                case TABLE_MODEL_RULE.LEVEL: {
                    names.push('ods'); continue;
                }
                case TABLE_MODEL_RULE.SUBJECT: {
                    names.push('sales'); continue;
                }
                case TABLE_MODEL_RULE.INCREMENT: {
                    names.push('i'); continue;
                }
                case TABLE_MODEL_RULE.FREQUENCY: {
                    names.push('m'); continue;
                }
                case TABLE_MODEL_RULE.CUSTOM: {
                    names.push('custom'); continue;
                }
            }
        }
        return names.join('_');
    }

    render () {
        const { tbNameRules } = this.state;
        return (
            <div className="m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title="表名生成规则配置:"
                >
                    <div>
                        <Form style={{ marginTop: '24px' }}>
                            <FormItem
                                {...formItemLayout}
                                label="表名"
                                hasFeedback
                            >
                                {this.renderTableNameRules()}
                                <div style={{ display: 'inline-block', marginBottom: '5px', lineHeight: 1.5 }}>
                                    <Button
                                        icon="plus"
                                        title="添加规则"
                                        size="normal"
                                        style={{ marginRight: '5px' }}
                                        onClick={this.appendTbNameRule}
                                    />
                                    {
                                        tbNameRules.length > 1 && <Button
                                            icon="minus"
                                            title="移除规则"
                                            size="normal"
                                            style={{ marginRight: '5px' }}
                                            onClick={() => this.removeTbNameRule(tbNameRules.length - 1)}
                                        />
                                    }
                                </div>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="生成示例"
                            >
                                <span>{this.renderTableName()}</span>
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                className="txt-center"
                            >
                                <Button type="primary" onClick={this.submit}>保存</Button>
                            </FormItem>
                        </Form>
                    </div>
                </Card>
            </div>
        )
    }
}

export default ModelDefineRule;
