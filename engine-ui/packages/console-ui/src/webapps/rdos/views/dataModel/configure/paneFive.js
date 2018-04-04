import React, { Component } from 'react';
import { connect } from 'react-redux';

import {
    Table, Row, Col, Select, Form, Card,
    Input, Button, message, Icon,
} from 'antd';

import utils from 'utils';
import { 
    formItemLayout, 
    tableModelRules, 
    TABLE_MODEL_RULE 
} from '../../../comm/const';

const Option = Select.Option;
const FormItem = Form.Item;

class ModelDefineRule extends Component {

    state = {
        tbNameRules: []
    }

    componentDidMount() {
        this.setState({
            tbNameRules: [tableModelRules[0]]
        })
    }

    submit = () => {
    }

    changeTbNameRule = (valueOption, index) => {
        const optionIndex = valueOption.props.index;
        const newArrs = [...this.state.tbNameRules];
        console.log('arguments:', optionIndex, index)
        newArrs[index] = tableModelRules[optionIndex];
        this.setState({
            tbNameRules: newArrs
        });
    }

    insertTbNameRule = (index) => {
        const originArr = this.state.tbNameRules;
        console.log('index:', index); 
        const start = index + 1;
        let arrOne = originArr.slice(0, start);
        const arrTwo = originArr.slice(start, originArr.length);

        // Insert a default object to array.
        arrOne.push(tableModelRules[0]);

        arrOne = arrOne.concat(arrTwo);
        console.log('after insert,', arrOne)

        this.setState({
            tbNameRules: [...arrOne]
        });
    }

    removeTbNameRule = (index) => {
        const originArr = [...this.state.tbNameRules];
        originArr.splice(index, 1)
        this.setState({
            tbNameRules: originArr
        });
    }

    renderTableNames = () => {
        const { tbNameRules } = this.state;
        const length = tbNameRules.length;

        const options = tableModelRules.map((rule, index) => <Option 
            key={rule.value}
            index={index}
            value={rule.value}
        >
            {rule.text}
        </Option>);
        

        return tbNameRules && tbNameRules.map((rule, index) => <span
            style={{display: 'inline-block', marginBottom: '5px'}} 
            key={index}>
                <Select
                    placeholder="请选择"
                    value={rule.value}
                    style={{ width: 100, marginRight: '5px' }}
                    onSelect={(value, option) => this.changeTbNameRule(option, index)}
                >
                    {options}
                </Select>
                {
                    (index == length - 1) && length > 1 ? <Button 
                        icon="minus" 
                        title="移除规则"
                        style={{marginRight: '5px'}}
                        onClick={() => this.removeTbNameRule(index)}
                    /> :
                    <Button 
                        icon="plus" 
                        title="添加规则"
                        style={{marginRight: '5px'}}
                        onClick={() => this.insertTbNameRule(index)}
                    />
                }
            </span>
        );
    }

    renderExample = () => {
        const { tbNameRules } = this.state;
        const names = [];
        for (let i = 0; i < tbNameRules.length; i++) {
            const rule = tbNameRules[i];
            switch(rule.value) {
                case TABLE_MODEL_RULE.LEVEL: {
                    names.push('ODS'); continue;
                }
                case TABLE_MODEL_RULE.THEME: {
                    names.push('sales'); continue;
                }
                case TABLE_MODEL_RULE.INCREMENT: {
                    names.push('i'); continue;
                }
                case TABLE_MODEL_RULE.FREQUENCY: {
                    names.push('M'); continue;
                }
                case TABLE_MODEL_RULE.CUSTOM: {
                    names.push('custom'); continue;
                }
            }
        }
        return names.join('_');
    }

    render() {

        return (
            <div className="m-card">
                <Card
                    noHovering
                    bordered={false}
                    loading={false}
                    title="表命名规则生成配置:"
                >
                    <div>
                        <Form style={{marginTop: '24px'}}>
                            <FormItem
                                {...formItemLayout}
                                label="表名"
                                hasFeedback
                            >
                                {this.renderTableNames()}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                label="生成示例"
                            >
                            <span>{this.renderExample()}</span>
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