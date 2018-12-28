import React from 'react';
import {
    Form,
    Collapse,
    Input
} from 'antd';

import HelpDoc from '../../helpDoc';

const FormItem = Form.Item;
const Panel = Collapse.Panel;

const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

class TaskParams extends React.Component {
    onChange = (index, value) => {
        const { tabData, onChange } = this.props;

        /* eslint-disable-next-line */
        const reg = /(^\$\[(\S+\(\S*\)|[a-z0-9\+\-\:\s\/\\\*]{2,})\]$)|(^(?!\$)\S+$)/i;

        if (reg.test(value)) {
            const taskVariables = [...tabData.taskVariables];
            taskVariables[index].paramCommand = value;
            onChange({ taskVariables })
        }
    }

    removeParams = (index) => {
        const { tabData, onChange } = this.props;
        const taskVariables = [...tabData.taskVariables];
        taskVariables.splice(index, 1);
        onChange({ taskVariables })
    }

    getFormItems = () => {
        const { getFieldDecorator } = this.props.form;
        const { taskVariables } = this.props.tabData;
        const sysArr = []; const customArr = [];

        const getFormItem = (index, param) => (
            <FormItem
                key={param.paramName}
                {...formItemLayout}
                label={param.paramName}
            >
                {getFieldDecorator(param.paramName, {
                    rules: [{
                        // 匹配规则：$[函数]或$[a-z0-9+-两个字符]或随意输入几个字符
                        /* eslint-disable-next-line */
                        pattern: /(^\$\[(\S+\(\S*\)|[a-z0-9\+\-\:\s\/\\\*]{2,})\]$)|(^(?!\$)\S+$)/i,
                        message: '参数格式不正确'
                    }],
                    initialValue: param.paramCommand
                })(
                    <Input
                        disabled={param.type === 0}
                        onChange={(e) => { this.onChange(index, e.target.value) }}
                    />
                )}
                {/* <Tooltip placement="top" title="移除变量">
                    <Icon type="minus-circle-o" style={removeIcon} onClick={() => this.removeParams(index)}/>
                </Tooltip> */}
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
    renderNothing (text) {
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

const FormWrapper = Form.create()(TaskParams);

export default FormWrapper;
