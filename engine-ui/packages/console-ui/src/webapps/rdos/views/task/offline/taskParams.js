import React from 'react';
import { connect } from 'react-redux';
import assign from 'object-assign';
import { Row,
    Col,
    Form,
    Collapse,
    Input,
    message
 } from 'antd';

 import HelpDoc from '../../helpDoc';

const FormItem = Form.Item;
const Panel = Collapse.Panel;

const formItemLayout = { // 表单正常布局
    labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
    },
}

class TaskParams extends React.Component {

    onChange = (index, value) => {
        const { tabData, onChange } = this.props;
        const reg = /(^\$\[(\S+\(\S*\)|[a-z0-9\+\-]{2,})\]$)|(^(?!\$)\S+$)/i;
        if (reg.test(value)) {
            console.log('value:', value);
            const taskVariables = [...tabData.taskVariables];
            taskVariables[index].paramCommand = value;
            onChange({taskVariables})
        }
    
    }

    getFormItems = () => {
        const { getFieldDecorator } = this.props.form;
        const { taskVariables } = this.props.tabData;
        const sysArr = [], customArr = [];
        const getFormItem = (index, param) => (
            <FormItem
                key={param.paramName}
                {...formItemLayout}
                label={param.paramName}
            >
                {getFieldDecorator(param.paramName, {
                    rules: [{
                        //匹配规则：$[函数]或$[a-z0-9+-两个字符]或随意输入几个字符
                        pattern: /(^\$\[(\S+\(\S*\)|[a-z0-9\+\-]{2,})\]$)|(^(?!\$)\S+$)/i,
                        message: '参数格式不正确',
                    }],
                    initialValue: param.paramCommand
                })(
                    <Input 
                        disabled={param.type === 0}
                        onChange={(e) => { this.onChange(index, e.target.value) }}
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

    render() {
        const {tabData} = this.props;
        const isLocked = tabData.readWriteLockVO && !tabData.readWriteLockVO.getLock
        const formItems = this.getFormItems()
        
        return (
            <Form style={{position:"relative"}}>
                {isLocked?<div className="cover-mask"></div>:null} 
                <Collapse bordered={false} defaultActiveKey={['1', '2']}>
                    <Panel key="1" header={<span>
                            系统参数配置 <HelpDoc style={{position: 'inherit'}} doc="customSystemParams" />
                        </span>
                    }>
                       
                        {formItems.sysItems}
                    </Panel>
                    <Panel key="2" header={
                        <span>自定义参数配置 <HelpDoc style={{position: 'inherit'}} doc="customParams" />
                        </span>
                    }>
                        {formItems.customItems}
                    </Panel>
                </Collapse>
            </Form>
        )
    }
}

const FormWrapper = Form.create()(TaskParams);

export default FormWrapper;
