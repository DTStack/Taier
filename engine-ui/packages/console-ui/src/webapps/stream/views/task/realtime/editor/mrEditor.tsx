import * as React from 'react'
import {
    Form, Input, Radio
} from 'antd'
import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { formItemLayout } from '../../../../comm/const'
import * as BrowserAction from '../../../../store/modules/realtimeTask/browser'
import { getTaskTypes as realtimeGetTaskTypes } from '../../../../store/modules/realtimeTask/comm';

const FormItem = Form.Item
const RadioGroup = Radio.Group

class MrEditor extends React.Component<any, any> {
    // eslint-disable-next-line
	UNSAFE_componentWillMount () {
        this.props.realtimeGetTaskTypes();
    }

    render () {
        const { form, currentPage, taskTypes } = this.props
        const { getFieldDecorator } = form
        const rowFix = {
            rows: 4
        }
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
                                required: true, message: '任务名称不可为空！'
                            }, {
                                max: 64,
                                message: '任务名称不得超过64个字符！'
                            }],
                            initialValue: currentPage ? currentPage.name : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="任务类型"
                    >
                        {getFieldDecorator('taskType', {
                            rules: [],
                            initialValue: currentPage ? currentPage.taskType : 0
                        })(
                            <RadioGroup disabled>
                                {taskTypes.map((item: any) =>
                                    <Radio key={item.key} value={item.key}>{item.value}</Radio>
                                )}
                            </RadioGroup>
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="资源"
                        hasFeedback
                    >
                        {currentPage && currentPage.resourceList &&
                            currentPage.resourceList.length > 0
                            ? currentPage.resourceList[0].resourceName : ''}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="mainClass"
                    >
                        {getFieldDecorator('mainClass', {
                            rules: [{}],
                            initialValue: currentPage && currentPage.mainClass
                        })(
                            <Input placeholder="请输入mainClass" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="参数"
                    >
                        {getFieldDecorator('exeArgs', {
                            rules: [{}],
                            initialValue: currentPage && currentPage.exeArgs
                        })(
                            <Input placeholder="请输入任务参数" />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="描述"
                        hasFeedback
                    >
                        {getFieldDecorator('taskDesc', {
                            rules: [],
                            initialValue: currentPage ? currentPage.taskDesc : ''
                        })(
                            <Input type="textarea" {...rowFix}/>
                        )}
                    </FormItem>
                </Form>
            </div>
        )
    }
}

const taskValueChange = (props: any, values: any) => {
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
    onValuesChange: taskValueChange
})(MrEditor);
export default connect((state: any) => {
    return {
        taskTypes: state.realtimeTask.comm.taskTypes
    }
}, (dispatch: any) => {
    return bindActionCreators({
        realtimeGetTaskTypes
    }, dispatch);
})(wrappedForm)
