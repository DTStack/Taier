import React, { Component } from 'react';
import { Modal, Form, Input, Radio, InputNumber, Button } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const RadioGroup = Radio.Group
class AddUpdateRules extends Component {
    state = {
        // 部分脱敏参数
        beginPos: '',
        endPos: '',
        replaceStr: '',
        newReplaceData: ''
    }
    // 改变开始值
    changeBeginPos = (value) => {
        this.setState({
            beginPos: value
        })
    }
    changeEndPos = (value) => {
        this.setState({
            endPos: value
        })
    }
    changeReplaceStr = (e) => {
        this.setState({
            replaceStr: e.target.value.length == 1 ? e.target.value : ''
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
            beginPos: '',
            endPos: '',
            replaceStr: ''
        })
    }
    // 效果预览
    preview = () => {
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('sampleData');
        const maskType = getFieldValue('maskType');
        const { beginPos, endPos, replaceStr } = this.state;
        console.log(beginPos, endPos, replaceStr)
        console.log(sampleData);
        if (maskType === 0) {
            if (sampleData) {
                let sampleDataArr = sampleData.split('');
                console.log(sampleData.split(''));
                let repeatCount = 0;
                if ((endPos >= sampleDataArr.length) && (sampleDataArr.length >= beginPos)) {
                    repeatCount = sampleDataArr.length - beginPos
                } else if (endPos < sampleDataArr.length && (sampleDataArr.length >= beginPos)) {
                    repeatCount = endPos - beginPos;
                } else if (sampleDataArr.length < beginPos) {
                    repeatCount = 0;
                }
                // const repeatCount = endPos >= sampleDataArr.length ? sampleDataArr.length - beginPos : endPos - beginPos;
                const newStr = this.repeatStr(replaceStr, repeatCount);
                console.log(newStr);
                sampleDataArr.splice(beginPos, (endPos - beginPos), newStr).join('');
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
        const { beginPos, endPos, replaceStr } = this.state;
        const ruleData = this.props.form.getFieldsValue();
        const partData = {
            beginPos,
            endPos,
            replaceStr
        }
        let params = {};
        if (ruleData.maskType === 1) {
            params = Object.assign({}, ruleData)
        } else {
            params = Object.assign({ ...partData, ...ruleData })
        }
        this.props.form.validateFields((err) => {
            if (!err) {
                this.props.form.resetFields();
                onOk(params)
            }
        });
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { status, dataSource } = this.props;
        const isEdit = status === 'edit';
        const maskType = getFieldValue('maskType');
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
                        {getFieldDecorator('name', {
                            rules: [{
                                required: true,
                                message: '规则名称不可为空！'
                            }],
                            initialValue: dataSource.name ? dataSource.name : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="样例数据"
                    >
                        {getFieldDecorator('example', {
                            rules: [{
                                max: 200,
                                message: '样例数据请控制在200个字符以内！'
                            }],
                            initialValue: dataSource.example ? dataSource.example : ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="id"
                        style={{ display: 'none' }}
                    >
                        {getFieldDecorator('id', {
                            initialValue: dataSource.id ? dataSource.id : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏效果"
                    >
                        {getFieldDecorator('maskType', {
                            rules: [{
                                required: true
                            }],
                            initialValue: dataSource.maskType || 1
                        })(
                            <RadioGroup
                                onChange={this.changedesenType}
                            >
                                <Radio value={1}>全部脱敏</Radio>
                                <Radio value={0}>部分脱敏</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    { maskType === 0 ? <FormItem>
                        <div style={{ margin: '-10 0 0 150' }}>
                            将从
                            <InputNumber
                                style={{ width: '70px' }}
                                min={1}
                                value={this.state.beginPos}
                                onChange={this.changeBeginPos}
                            />位至第<InputNumber
                                style={{ width: '70px' }}
                                min={Number(this.state.beginPos)}
                                // max={sampleDataLength}
                                value={this.state.endPos}
                                onChange={this.changeEndPos}
                            />位替换为:<Input
                                style={{ width: '50px' }}
                                value={this.state.replaceStr}
                                onChange={this.changeReplaceStr}
                            />
                            {/* <div style={{ color: 'red' }}>开始位置不能大于等于结束位置!</div> */}
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
