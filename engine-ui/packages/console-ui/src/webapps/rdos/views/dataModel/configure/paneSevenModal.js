import React, { Component } from 'react'
import { isArray, isNumber, isEmpty } from 'lodash';
import {
    Form, Input, Icon, Select,
    Radio, Modal, Button,
} from 'antd'

import Api from '../../../api';
import { 
    formItemLayout,
    tableModelRules,
    TABLE_MODEL_RULE,
} from '../../../comm/const';
import LifeCycle from '../../dataManage/lifeCycle';

const FormItem = Form.Item
const RadioGroup = Radio.Group
const Option = Select.Option;

class DeriveIndexModal extends Component {

    state = { 
        tbNameRules: [tableModelRules[0]]
    }

    submit = (e) => {
        e.preventDefault()
        const { handOk, form } = this.props

        const formData = this.props.form.getFieldsValue()
  
        this.props.form.validateFields((err) => {
            if (!err) {
                setTimeout(() => {
                    form.resetFields()
                }, 200)
                handOk(formData)
            }
        });
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

    cancle = () => {
        const { handCancel, form } = this.props
        this.setState({ }, () => {
            handCancel()
            form.resetFields()
        })
    }


    renderIndexNames = () => {

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

    render() {

        const {
            form, visible, data
        } = this.props

        const { getFieldDecorator } = form

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑衍生指标': '创建衍生指标'

        return (
            <Modal
                title={title}
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="衍生指标名称"
                        hasFeedback
                    >
                        {getFieldDecorator('indexAlias', {
                            rules: [{
                                required: true, message: '衍生指标名称不可为空！',
                            }],
                            initialValue: data ? data.indexAlias : '',
                        })(
                            <Input />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标口径"
                        hasFeedback
                    >
                        {getFieldDecorator('indexDesc', {
                            rules: [{
                                max: 200,
                                message: '指标口径请控制在200个字符以内！',
                            }],
                            initialValue: data ? data.indexDesc : '',
                        })(
                            <Input type="textarea" rows={4} />,
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标命名"
                        hasFeedback
                    >
                        {this.renderIndexNames()}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标类型"
                        hasFeedback
                    >
                        {getFieldDecorator('indexType', {
                            rules: [],
                            initialValue: data ? data.indexType : '1',
                        })(
                            <Select>
                                <Option value="1">原子指标</Option>
                                <Option value="2">修饰词</Option>
                                <Option value="3">衍生指标</Option>
                            </Select>,
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create()(DeriveIndexModal);
export default wrappedForm
