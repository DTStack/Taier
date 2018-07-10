import React, { Component } from 'react'
import {
    Form, Input, Radio,
 } from 'antd'
 
import { formItemLayout } from '../../../../comm/const'
import * as BrowserAction from '../../../../store/modules/realtimeTask/browser'

const FormItem = Form.Item
const RadioGroup = Radio.Group

class MrEditor extends Component {

    render() {
        const { form, currentPage } = this.props
        const { getFieldDecorator } = form
        return (
            <div style={{ padding: '60px' }}>
            <Form>
                <FormItem
                  {...formItemLayout}
                  label="任务名称"
                  hasFeedback
                >
                    {getFieldDecorator('name', {
                        rules: [{
                            required: true, message: '任务名称不可为空！',
                        }, {
                            max: 64,
                            message: '任务名称不得超过64个字符！',
                        }],
                        initialValue: currentPage ? currentPage.name : '',
                    })(
                        <Input />,
                    )}
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label="任务类型"
                >
                    {getFieldDecorator('taskType', {
                        rules: [],
                        initialValue: currentPage ? currentPage.taskType : 0,
                    })(
                        <RadioGroup disabled>
                            <Radio value={0}>SQL任务</Radio>
                            <Radio value={1}>MR任务</Radio>
                        </RadioGroup>,
                    )}
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label="资源"
                  hasFeedback
                >
                    {currentPage && currentPage.resourceList
                    && currentPage.resourceList.length > 0 ?
                    currentPage.resourceList[0].resourceName : ''}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="mainClass"
                >
                    {getFieldDecorator('mainClass', {
                        rules: [{}],
                        initialValue: currentPage && currentPage.mainClass,
                    })(
                        <Input placeholder="请输入mainClass" />,
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="参数"
                >
                    {getFieldDecorator('exeArgs', {
                        rules: [{}],
                        initialValue: currentPage && currentPage.exeArgs,
                    })(
                        <Input placeholder="请输入任务参数" />,
                    )}
                </FormItem>
                <FormItem
                  {...formItemLayout}
                  label="描述"
                  hasFeedback
                >
                    {getFieldDecorator('taskDesc', {
                        rules: [],
                        initialValue: currentPage ? currentPage.taskDesc : '',
                    })(
                        <Input type="textarea" rows={4} />,
                    )}
                </FormItem>
            </Form>
            </div>
        )
    }
}

const taskValueChange = (props, values) => {
    const { dispatch, currentPage } = props

    let invalid = false
    if (values.name === '') { // 任务名称不可为空
         invalid = true
    }

    values.invalid = invalid
    const page = Object.assign(currentPage, values)
    dispatch(BrowserAction.setCurrentPage(page))
    dispatch(BrowserAction.updatePage(page))
}

const wrappedForm = Form.create({
    onValuesChange: taskValueChange,
})(MrEditor);
export default wrappedForm
