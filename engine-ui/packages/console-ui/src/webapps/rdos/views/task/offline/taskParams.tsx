import * as React from 'react';
import {
    Form,
    Collapse,
    Input
} from 'antd';
import { debounce } from 'lodash'

import HelpDoc from '../../helpDoc';

const FormItem = Form.Item;
const Panel = Collapse.Panel;

const formItemLayout: any = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

// 匹配规则：$[函数]或$[a-z0-9+-两个字符]或随意输入几个字符
// 原来的正则：/(^\$\[(\S+\(\S*\)|[a-z0-9\+\-\:\s\/\\\*]{2,})\]$)|(^(?!\$)\S+$)/i;
/* eslint-disable-next-line */
export const paramsRegPattern = /^\$\[(\S+\((.*)\)|.+)\]$|^(?!\$)\S+$/i;

class TaskParams extends React.Component<any, any> {
    onChange = (index: any, value: any) => {
        const { tabData, onChange } = this.props;

        if (!value || paramsRegPattern.test(value)) {
            const taskVariables: any = [...tabData.taskVariables];
            taskVariables[index].paramCommand = value;
            onChange({ taskVariables })
        }
    }

    debounceChange = debounce(this.onChange, 300, { 'maxWait': 2000 })

    removeParams = (index: any) => {
        const { tabData, onChange } = this.props;
        const taskVariables: any = [...tabData.taskVariables];
        taskVariables.splice(index, 1);
        onChange({ taskVariables })
    }

    getFormItems = () => {
        const { getFieldDecorator } = this.props.form;
        const { taskVariables } = this.props.tabData;
        const sysArr: any = [];
        const customArr: any = [];
        const getFormItem = (index: any, param: any) => (
            <FormItem
                key={param.paramName}
                {...formItemLayout}
                label={param.paramName}
            >
                {getFieldDecorator(param.paramName, {
                    rules: [{
                        pattern: paramsRegPattern,
                        message: '参数格式不正确'
                    }],
                    initialValue: param.paramCommand
                })(
                    <Input
                        disabled={param.type === 0}
                        onChange={(e: any) => { this.debounceChange(index, e.target.value) }}
                    />
                )}
            </FormItem>
        )
        if (taskVariables) {
            for (let i = 0; i < taskVariables.length; i++) {
                const param = taskVariables[i];
                const formItem = getFormItem(i, param)
                if (param.type === 0) { // 系统参数
                    sysArr.push(formItem)
                } else if (param.type === 1) { // 自定义参数
                    customArr.push(formItem)
                }
            }
        }
        return {
            sysItems: sysArr,
            customItems: customArr
        }
    }
    renderNothing (text: any) {
        return (
            <p style={{
                textAlign: 'center',
                fontSize: '14px',
                color: '#a1a1a1'

            }}>{text || '无参数'}</p>
        )
    }
    render () {
        const { tabData, couldEdit } = this.props;
        const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock
        const formItems = this.getFormItems()

        return (
            <Form style={{ position: 'relative' }}>
                {isLocked || !couldEdit ? <div className="cover-mask"></div> : null}
                <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                    <Panel key="1" header={<span>
                            系统参数配置 <HelpDoc style={{ position: 'inherit' }} doc="customSystemParams" />
                    </span>
                    }>
                        {formItems.sysItems.length ? formItems.sysItems : this.renderNothing('无系统参数')}
                    </Panel>
                    <Panel key="2" header={
                        <span>自定义参数配置 <HelpDoc style={{ position: 'inherit' }} doc="customParams" />
                        </span>
                    }>
                        {formItems.customItems.length ? formItems.customItems : this.renderNothing('无自定义参数')}
                    </Panel>
                </Collapse>
            </Form>
        )
    }
}

const FormWrapper = Form.create<any>()(TaskParams);

export default FormWrapper;
