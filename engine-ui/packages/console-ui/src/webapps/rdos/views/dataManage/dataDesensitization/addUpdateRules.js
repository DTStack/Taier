import React, { Component } from 'react';
import { Modal, Form, Input, Radio, InputNumber, Button } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const RadioGroup = Radio.Group
class AddUpdateRules extends Component {
    state = {
        // 部分脱敏参数
        partData: {
            startInputValue: '',
            finishInputValue: '',
            repalceValue: ''
        },
        newReplaceData: ''
    }
    // 改变开始值
    changeStartValue = (value) => {
        const { partData } = this.state;
        this.setState({
            partData: Object.assign(partData, { startInputValue: value })
        })
    }
    changeFinishValue = (value) => {
        const { partData } = this.state;
        this.setState({
            partData: Object.assign(partData, { finishInputValue: value })
        })
    }
    changeReplaceValue = (e) => {
        const { partData } = this.state;
        this.setState({
            partData: Object.assign(partData, { repalceValue: e.target.value.length == 1 ? e.target.value : '' })
        })
    }
    // 字符串替换
    repeatStr = (str, n) => {
        return new Array(n + 1).join(str)
    }
    // 切换radio
    changedesenType = () => {
        this.setState({
            newReplaceData: '',
            partData: {
                startInputValue: '',
                finishInputValue: '',
                repalceValue: ''
            }
        })
    }
    // 效果预览
    preview = () => {
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('sampleData');
        const desensitizationType = getFieldValue('desensitizationType');
        const { startInputValue, finishInputValue, repalceValue } = this.state.partData;
        console.log(startInputValue, finishInputValue, repalceValue)
        console.log(sampleData);
        if (desensitizationType === 0) {
            if (sampleData) {
                let sampleDataArr = sampleData.split('');
                console.log(sampleData.split(''));
                let repeatCount = 0;
                if ((finishInputValue >= sampleDataArr.length) && (sampleDataArr.length >= startInputValue)) {
                    repeatCount = sampleDataArr.length - startInputValue
                } else if (finishInputValue < sampleDataArr.length && (sampleDataArr.length >= startInputValue)) {
                    repeatCount = finishInputValue - startInputValue;
                } else if (sampleDataArr.length < startInputValue) {
                    repeatCount = 0;
                }
                // const repeatCount = finishInputValue >= sampleDataArr.length ? sampleDataArr.length - startInputValue : finishInputValue - startInputValue;
                const newStr = this.repeatStr(repalceValue, repeatCount);
                console.log(newStr);
                sampleDataArr.splice(startInputValue, (finishInputValue - startInputValue), newStr).join('');
                console.log(sampleDataArr);
                this.setState({
                    newReplaceData: sampleDataArr
                })
            }
        } else {
            if (sampleData) {
                let sampleDataAllArr = sampleData.split('');
                const newStr = this.repeatStr('*', sampleDataAllArr.length);
                sampleDataAllArr.splice(0, sampleDataAllArr.length, newStr).join('');
                console.log(newStr);
                console.log(sampleDataAllArr);
                this.setState({
                    newReplaceData: sampleDataAllArr
                })
            } else {
                this.setState({
                    newReplaceData: ''
                })
            }
        }
    }
    cancel = () => {
        const { onCancel } = this.props;
        onCancel();
    }
    submit = () => {
        const { onOk } = this.props;
        const { partData } = this.state;
        const ruleData = this.props.form.getFieldsValue();
        this.props.form.validateFields((err) => {
            if (!err) {
                this.props.form.resetFields()
                onOk({ ...ruleData, partData })
            }
        });
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { status, dataSource } = this.props;
        const isEdit = status === 'edit';
        const desensitizationType = getFieldValue('desensitizationType');
        // const sampleData = getFieldValue('sampleData');
        // const sampleDataLength = sampleData.split('').length || 1;
        // const sampleData = getFieldValue('sampleData'); // 样例数据
        const { newReplaceData } = this.state;
        return (
            <Modal
                visible={this.props.visible}
                width='650'
                title={isEdit ? '编辑规则' : '创建规则'}
                onCancel={this.cancel}
                onOk={this.submit}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="规则名称"
                    >
                        {getFieldDecorator('ruleName', {
                            rules: [{
                                required: true,
                                message: '规则名称不可为空！'
                            }],
                            initialValue: dataSource.ruleName ? dataSource.ruleName : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="样例数据"
                    >
                        {getFieldDecorator('sampleData', {
                            rules: [{
                                max: 200,
                                message: '样例数据请控制在200个字符以内！'
                            }]
                            // initialValue: dataSource.ruleName ? dataSource.ruleName : ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏效果"
                    >
                        {getFieldDecorator('desensitizationType', {
                            rules: [{
                                required: true
                            }],
                            initialValue: 1
                        })(
                            <RadioGroup
                                onChange={this.changedesenType}
                            >
                                <Radio value={1}>全部脱敏</Radio>
                                <Radio value={0}>部分脱敏</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    { desensitizationType === 0 ? <FormItem>
                        <div style={{ margin: '-10 0 0 150' }}>
                            将从
                            <InputNumber
                                style={{ width: '70px' }}
                                min={1}
                                value={this.state.partData.startInputValue}
                                onChange={this.changeStartValue}
                            />位至第<InputNumber
                                style={{ width: '70px' }}
                                min={Number(this.state.partData.startInputValue)}
                                // max={sampleDataLength}
                                value={this.state.partData.finishInputValue}
                                onChange={this.changeFinishValue}
                            />位替换为:<Input
                                style={{ width: '50px' }}
                                value={this.state.partData.repalceValue}
                                onChange={this.changeReplaceValue}
                            />
                        </div>
                    </FormItem> : ''}
                    <FormItem
                        {...formItemLayout}
                    >
                        <div style={{ margin: '-10 0 0 150' }}>
                            <Button
                                type="primary"
                                onClick={this.preview}
                            >效果预览</Button>
                            <div>{newReplaceData}</div>
                        </div>
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
export default Form.create()(AddUpdateRules);
