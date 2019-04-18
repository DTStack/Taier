import React from 'react';

import { Form, Input } from 'antd';

const FormItem = Form.Item;

class TaskVariables extends React.Component {
    render () {
        const { data } = this.props;
        const customParams = data.filter((param) => {
            return param.type == 1;
        })
        const sysParams = data.filter((param) => {
            return param.type == 0;
        })
        return (
            <div className='c-task-variables'>
                <Form>
                    <header className='c-panel__siderbar__header'>
                        系统参数配置
                    </header>
                    <div className='c-panel__siderbar__form'>
                        {sysParams.map((param) => {
                            return (
                                <FormItem
                                    key={param.paramName}
                                    label={param.paramName}
                                >
                                    <Input disabled value={param.paramCommand} />
                                </FormItem>
                            )
                        })}
                    </div>
                    <header className='c-panel__siderbar__header'>
                        自定义参数配置
                    </header>
                    <div className='c-panel__siderbar__form'>
                        {customParams.map((param) => {
                            return (
                                <FormItem
                                    key={param.paramName}
                                    label={param.paramName}
                                >
                                    <Input value={param.paramCommand} onChange={(e) => {
                                        this.props.changeVariable(param.paramName, e.target.value);
                                    }} />
                                </FormItem>
                            )
                        })}
                    </div>
                </Form>
            </div>
        )
    }
}
export default TaskVariables;
