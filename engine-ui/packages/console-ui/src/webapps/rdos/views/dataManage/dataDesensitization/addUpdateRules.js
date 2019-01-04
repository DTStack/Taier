import React, { Component } from 'react';
import { Modal, Form, Input, Radio, InputNumber, Button, message } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
class AddUpdateRules extends Component {
    state = {
        // 部分脱敏参数
        beginPos: '',
        endPos: '',
        replaceStr: '',
        newReplaceData: ''
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const dataSource = nextProps.dataSource;
        if (this.props.dataSource != dataSource) {
            this.setState({
                beginPos: dataSource.beginPos,
                endPos: dataSource.endPos,
                replaceStr: dataSource.replaceStr
            })
        }
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
    // 效果预览
    preview = () => {
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('example');
        const maskType = getFieldValue('maskType');
        const { beginPos, endPos, replaceStr } = this.state;
        console.log(beginPos, endPos, replaceStr)
        console.log(sampleData);
        if (maskType === 1) {
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
        this.setState({
            beginPos: '',
            endPos: '',
            replaceStr: '',
            newReplaceData: ''
        })
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
        if (ruleData.maskType === 0) {
            params = Object.assign({ repeatStr: '*' }, ruleData)
        } else {
            params = Object.assign({ ...partData, ...ruleData })
        }
        const isPartDes = ruleData.maskType === 1;
        this.props.form.validateFields((err) => {
            if (!err && (isPartDes ? (beginPos && endPos && replaceStr) : true)) {
                this.props.form.resetFields();
                onOk(params)
            } else if (isPartDes ? (beginPos == '' || endPos == '' || replaceStr == '') : false) {
                message.warning('请正确输入脱敏替换配置');
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
                width='600'
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
                            rules: [],
                            initialValue: dataSource.maskType || 0
                        })(
                            <RadioGroup>
                                <Radio value={0}>全部脱敏</Radio>
                                <Radio value={1}>部分脱敏</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    { maskType === 1 ? <FormItem>
                        <div style={{ margin: '-10 0 0 150' }}>
                            将从
                            <InputNumber
                                style={{ width: '70px' }}
                                min={1}
                                value={this.state.beginPos}
                                onChange={this.changeBeginPos}
                            />位至第<InputNumber
                                style={{ width: '70px' }}
                                min={Number(this.state.beginPos) || 1 }
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
                    {/* {
                        maskType === 1 && <div className='partDataForm' style={{ marginLeft: '130px' }}>
                            <FormItem
                                {...formItemLayout}
                                // label="beginPos"
                            >
                                {getFieldDecorator('beginPos', {
                                    initialValue: dataSource.beginPos ? dataSource.beginPos : ''
                                })(
                                    <InputNumber />
                                )}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                // label="endPos"
                            >
                                {getFieldDecorator('endPos', {
                                    initialValue: dataSource.endPos ? dataSource.endPos : ''
                                })(
                                    <InputNumber />
                                )}
                            </FormItem>
                            <FormItem
                                {...formItemLayout}
                                // label="replaceStr"
                            >
                                {getFieldDecorator('replaceStr', {
                                    initialValue: dataSource.replaceStr ? dataSource.replaceStr : ''
                                })(
                                    <Input />
                                )}
                            </FormItem>
                        </div>
                    } */}
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
