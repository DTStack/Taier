import React from 'react';

import { Form, Select } from 'antd';
import { isEmpty } from 'lodash';

import EngineConfigItem from './configItem';
import EngineSelect from '../engineSelect';

import {
    formItemLayout,
    ENGINE_SOURCE_TYPE
} from '../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;

export const customItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 2 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 }
    }
}

class EngineForm extends React.Component {
    state = {
        engineType: ENGINE_SOURCE_TYPE.SPARK_THRIFT_SERVER // Default is spark
    }

    static getDerivedStateFromProps (props, state) {
        const formData = props.formData;
        if (!isEmpty(formData) && formData.type) { // 初始化 engineType
            return { engineType: formData.type }
        }
        return null
    }

    getProjectOptions () {
        const { projectList = [] } = this.props;
        return projectList.map((project) => {
            return <Option key={project}>{project}</Option>
        });
    }

    onTypeChange = (value) => {
        this.setState({ engineType: parseInt(value, 10) })
        this.props.form.resetFields([{ initialType: 1 }]);
    }

    render () {
        const {
            disabledEngineTypes
        } = this.props;
        const { getFieldDecorator } = this.props.form;

        return (
            <Form>
                <FormItem
                    label='引擎类型'
                    {...formItemLayout}
                >
                    {getFieldDecorator('engineType', {
                        initialValue: `${ENGINE_SOURCE_TYPE.SPARK_THRIFT_SERVER}`,
                        rules: [{
                            required: true,
                            message: '请选择引擎类型'
                        }]
                    })(
                        <EngineSelect
                            disabledEngineTypes={disabledEngineTypes}
                            onChange={this.onTypeChange}
                            placeholder="请选择引擎类型"
                        />
                    )}
                </FormItem>
                <EngineConfigItem
                    {...this.props}
                    formItemLayout={formItemLayout}
                    engineType={this.state.engineType}
                />
            </Form>
        )
    }
}
export default Form.create({
    mapPropsToFields: (props) => {
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
    onValuesChange: (props, values) => {
        if (props.onChange) props.onChange(values);
    }
})(EngineForm);
