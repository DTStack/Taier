import * as React from 'react';

import { Form } from 'antd';
import { isEmpty } from 'lodash';

import EngineConfigItem from './configItem';
import EngineSelect from '../engineSelect';

import {
    formItemLayout,
    ENGINE_SOURCE_TYPE
} from '../../comm/const';

const FormItem = Form.Item;
// const Option = Select.Option;

export const customItemLayout: any = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 2 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

class EngineForm extends React.Component<any, any> {
    state: any = {
        engineType: ENGINE_SOURCE_TYPE.HADOOP // Default is spark
    }

    static getDerivedStateFromProps (props: any, state: any) {
        const formData = props.formData;
        if (!isEmpty(formData) && formData.type) { // 初始化 engineType
            return { engineType: formData.type }
        }
        return null
    }
    onTypeChange = (value: any) => {
        this.setState({ engineType: parseInt(value, 10) })
        this.props.form.resetFields([{ initialType: 1 }]);
    }

    render () {
        const {
            disabledEngineTypes, engineList = [], targetDb
        } = this.props;
        const { getFieldDecorator } = this.props.form;

        return (
            <Form>
                <FormItem
                    label='引擎类型'
                    {...formItemLayout}
                >
                    {getFieldDecorator('engineType', {
                        // initialValue: `${ENGINE_SOURCE_TYPE.HADOOP}`,
                        rules: [{
                            required: true,
                            message: '请选择引擎类型'
                        }]
                    })(
                        <EngineSelect
                            disabledEngineTypes={disabledEngineTypes}
                            onChange={this.onTypeChange}
                            tableTypes={engineList}
                            placeholder="请选择引擎类型"
                        />
                    )}
                </FormItem>
                <EngineConfigItem
                    {...this.props}
                    formItemLayout={formItemLayout}
                    engineType={this.state.engineType}
                    // formParentField={this.state.engineType == ENGINE_SOURCE_TYPE.HADOOP ? 'hadoop' : 'libra'}
                    targetDb={targetDb}
                />
            </Form>
        )
    }
}
export default Form.create({
    mapPropsToFields: (props: any) => {
        const formData = props.formData;
        return formData ? {
            engineType: {
                value: formData.engineType
            },
            initialType: {
                value: formData.initialType
            },
            target: {
                value: formData.target
            },
            catalogueId: {
                value: formData.catalogueId
            },
            lifecycle: {
                value: formData.lifecycle
            }
        } : {}
    },
    onValuesChange: (props: any, values: any) => {
        if (props.onChange) props.onChange(values);
    }
})(EngineForm);
