import React, { Component } from 'react';
import { Modal, Form, Input, Radio, InputNumber, Button, message } from 'antd';
import { formItemLayout } from '../../../comm/const';

const FormItem = Form.Item;
const RadioGroup = Radio.Group;
class AddUpdateRules extends Component {
    state = {
        // 部分脱敏参数
        beginPos: 1,
        endPos: 1,
        replaceStr: '*',
        newReplaceData: '',
        clickTimes: 1 // 解决编辑首次不显示部分脱敏内容
    }
    /* eslint-disable-next-line */
    componentWillReceiveProps (nextProps) {
        const dataSource = nextProps.dataSource;
        if (this.props.dataSource != dataSource) {
            this.setState({
                beginPos: Number(dataSource.beginPos),
                endPos: Number(dataSource.endPos),
                replaceStr: dataSource.replaceStr
            })
        }
    }
    // 改变开始值
    changeBeginPos = (value) => {
        const numVal = value ? value.toString().replace(/[^\d]/g, '') : '';
        this.setState({
            beginPos: numVal
        })
    }
    changeEndPos = (value) => {
        const numVal = value ? value.toString().replace(/[^\d]/g, '') : '';
        this.setState({
            endPos: numVal
        })
    }
    changeReplaceStr = (e) => {
        this.setState({
            replaceStr: e.target.value.length == 1 ? e.target.value : ''
        })
    }
    changeRadio = (e) => {
        const { status, dataSource } = this.props;
        let { clickTimes } = this.state;
        const isInlay = dataSource.tenantId === -1; // 内置规则
        if ((status === 'edit') && e.target.value === 1 && dataSource.beginPos === 0 && dataSource.endPos === 0) {
            this.setState({
                beginPos: 1,
                endPos: 1,
                replaceStr: '*'
            })
        }
        if (status === 'edit' && !isInlay && dataSource.maskType === 1) {
            this.setState({
                clickTimes: clickTimes + 1
            })
        }
    }
    // 判断是否输入正确脱敏配置
    isPassConfig = (sampleDataArr, beginPos, endPos) => {
        const firstCase = (endPos >= sampleDataArr.length && sampleDataArr.length >= beginPos) ||
        (endPos > sampleDataArr.length && sampleDataArr.length >= beginPos) ||
        (endPos >= sampleDataArr.length && sampleDataArr.length > beginPos) ||
        (endPos > sampleDataArr.length && sampleDataArr.length > beginPos);

        const secondCase = (endPos <= sampleDataArr.length && endPos >= beginPos) ||
        (endPos < sampleDataArr.length && endPos >= beginPos) ||
        (endPos <= sampleDataArr.length && endPos > beginPos) ||
        (endPos < sampleDataArr.length && endPos > beginPos);

        const thirdCase = (sampleDataArr.length < beginPos && beginPos < endPos) ||
        (sampleDataArr.length < beginPos && beginPos <= endPos) ||
        (sampleDataArr.length <= beginPos && beginPos < endPos) ||
        (sampleDataArr.length <= beginPos && beginPos <= endPos);
        return {
            firstCase,
            secondCase,
            thirdCase
        }
    }
    /** 设置newReplaceData
     * sampleDataArr 样例数据
     * beginPos 开始输入值
     * endPos结束输入值
     * newStr 替换新字符
    */
    setNewReplaceData = (sampleDataArr, beginPos, endPos, newStr) => {
        sampleDataArr.splice(beginPos - 1, (endPos - beginPos + 1), newStr).join('');
        this.setState({
            newReplaceData: sampleDataArr
        })
    }
    // 字符串替换
    repeatStr = (str, n) => {
        console.log(n, str)
        return new Array(n + 1).join(str)
    }
    // 效果预览
    /* eslint-disable */
    preview = () => {
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('example');
        const maskType = getFieldValue('maskType');
        let { replaceStr } = this.state;
        let { beginPos, endPos } = {
            beginPos: Number(this.state.beginPos),
            endPos: Number(this.state.endPos)
        }
        if (maskType === 1) {
            this.setState({
                newReplaceData: ''
            })
            let sampleDataArr = sampleData.split('');
            console.log(sampleDataArr.length, beginPos, endPos)
            let repeatCount = 0;
            const cases = this.isPassConfig(sampleDataArr, beginPos, endPos);
            console.log('----------')
            console.log(cases);
            console.log(cases.firstCase);
            console.log(cases.secondCase);
            console.log(cases.thirdCase);
            if (cases.firstCase) {
                repeatCount = sampleDataArr.length - beginPos + 1;
                const newStr = this.repeatStr(replaceStr, repeatCount);
                this.setNewReplaceData(sampleDataArr, beginPos, endPos, newStr);
            } else if (cases.secondCase) {
                repeatCount = endPos - beginPos + 1;
                const newStr = this.repeatStr(replaceStr, repeatCount);
                this.setNewReplaceData(sampleDataArr, beginPos, endPos, newStr);
            } else if (cases.thirdCase) {
                repeatCount = 0;
                const newStr = this.repeatStr(replaceStr, repeatCount);
                this.setNewReplaceData(sampleDataArr, beginPos, endPos, newStr);
            } else {
                // message.warning(sampleDataArr.length > 0 ? '请正确输入脱敏替换配置' : '请输入样例数据');
                message.warning('请正确输入脱敏替换配置');
            }
        } else {
            if (sampleData) {
                let sampleDataAllArr = sampleData.split('');
                const newStr = this.repeatStr('*', sampleDataAllArr.length);
                sampleDataAllArr.splice(0, sampleDataAllArr.length, newStr).join('');
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
        const { replaceStr } = this.state;
        let { beginPos, endPos } = {
            beginPos: Number(this.state.beginPos),
            endPos: Number(this.state.endPos)
        }
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
        const { getFieldValue } = this.props.form;
        let sampleData = getFieldValue('example');
        let sampleDataArr = sampleData.split('') || '';
        const cases = this.isPassConfig(sampleDataArr, beginPos, endPos); // 是否正确脱敏输入配置
        const isPartDes = ruleData.maskType === 1;
        this.props.form.validateFields((err) => {
            if (!err && (isPartDes ? (beginPos && endPos && replaceStr && (cases.firstCase || cases.secondCase || cases.thirdCase)) : true)) {
                this.props.form.resetFields();
                onOk(params)
            } else if (!err) {
                message.warning('请正确输入脱敏替换配置');
            }
        });
    }
    render () {
        const { getFieldDecorator, getFieldValue } = this.props.form;
        const { status, dataSource } = this.props;
        const isEdit = status === 'edit';
        const maskType = getFieldValue('maskType');
        const { newReplaceData, clickTimes } = this.state;
        const isInlay = dataSource.tenantId === -1; // 内置规则
        return (
            <Modal
                visible={this.props.visible}
                width='600'
                title={isEdit ? '编辑规则' : '创建规则'}
                onCancel={this.cancel}
                onOk={this.submit}
                footer={
                    isEdit && isInlay ? <Button type="primary" size="large" onClick={this.cancel}>关闭</Button>
                        : [
                            <Button key="cancel" size="large" onClick={this.cancel}>取消</Button>,
                            <Button key="submit" type="primary" size="large" onClick={this.submit}>确定</Button>
                        ]
                }
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
                            <Input disabled={isInlay} />
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
                            <Input type="textarea" rows={4} disabled={isInlay} />
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
                            <Input disabled={isInlay} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="脱敏效果"
                    >
                        {getFieldDecorator('maskType', {
                            rules: [{
                                required: true,
                                message: '脱敏效果不可为空！'
                            }],
                            initialValue: (dataSource && dataSource.maskType) || 0
                        })(
                            <RadioGroup onChange={this.changeRadio} disabled={isInlay}>
                                <Radio value={0}>全部脱敏</Radio>
                                <Radio value={1}>部分脱敏</Radio>
                            </RadioGroup>
                        )}
                    </FormItem>
                    { maskType === 1 || (isEdit && dataSource.maskType === 1 && clickTimes === 1) ? <FormItem>
                        <div style={{ margin: '-10 0 0 150' }}>
                            将从
                            <InputNumber
                                disabled={isInlay}
                                style={{ width: '70px' }}
                                min={1}
                                value={this.state.beginPos}
                                onChange={this.changeBeginPos}
                            />位至第<InputNumber
                                disabled={isInlay}
                                style={{ width: '70px' }}
                                min={1}
                                value={this.state.endPos}
                                onChange={this.changeEndPos}
                            />位替换为:<Input
                                disabled={isInlay}
                                style={{ width: '50px' }}
                                value={this.state.replaceStr}
                                onChange={this.changeReplaceStr}
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
