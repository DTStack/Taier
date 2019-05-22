import React from 'react';

import { Form, Select, Radio } from 'antd';
import { isEmpty } from 'lodash';

import CatalogueSelect from '../../components/catalogueSelect';
import LifeCycleSelect from '../../components/lifeCycleSelect';

import {
    formItemLayout,
    ENGINE_SOURCE_TYPE,
    ENGINE_SOURCE_TYPE_OPTIONS
} from '../../comm/const';

const FormItem = Form.Item;
const Option = Select.Option;
const RadioGroup = Radio.Group;

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
    componentDidMount () {
        const formData = this.props.formData;
        if (!isEmpty(formData) && formData.type) { // 初始化 engineType
            let initialState = {
                engineType: formData.type
            }
            this.setState(initialState);
        }
    }

    getProjectOptions () {
        const { projectList = [] } = this.props;
        return projectList.map((project) => {
            return <Option key={project}>{project}</Option>
        });
    }

    initialTypeRadios () {
        const { engineType } = this.state;
        switch (engineType) {
            case ENGINE_SOURCE_TYPE.SPARK_THRIFT_SERVER: {
                return [
                    <Radio value={1} key={1}>创建</Radio>,
                    <Radio value={2} key={2}>对接已有Spark Thrift Server</Radio>
                ]
            }
            case ENGINE_SOURCE_TYPE.LIBRA: {
                return [
                    <Radio value={1} key={1}>创建Schema</Radio>,
                    <Radio value={2} key={2}>对接已有LibrA Schema</Radio>
                ]
            }
            default: return '对接未知类型';
        }
    }

    onTypeChange = (value) => {
        this.setState({ engineType: parseInt(value, 10) })
        this.props.form.resetFields();
    }

    render () {
        const {
            onPreviewMetaData,
            targets,
            disabledEngineTypes,
            disabledTargets
        } = this.props;
        const { getFieldDecorator } = this.props.form;

        const sourceTypeList = ENGINE_SOURCE_TYPE_OPTIONS.map(
            item => (
                <Option
                    key={item.value}
                    disabled={disabledEngineTypes && disabledEngineTypes.indexOf(item.value) > -1} // 禁用指定引擎类型选项
                    value={item.value.toString()}
                >
                    {item.name}
                </Option>
            )
        )

        const addTargets = targets && targets.map(
            item => (
                <Option
                    key={item.value}
                    disabled={disabledTargets && disabledTargets.indexOf(item.value) > -1} // 禁用指定目标
                    value={item.value.toString()}
                >
                    {item.name}
                </Option>
            )
        )

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
                        <Select
                            onChange={this.onTypeChange}
                            placeholder="请选择引擎类型"
                        >
                            { sourceTypeList }
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='初始化方式'
                    {...formItemLayout}
                >
                    {getFieldDecorator('initialType', {
                        initialValue: 1,
                        rules: [{
                            required: false,
                            message: '请选择初始化方式'
                        }]
                    })(
                        <RadioGroup>
                            {this.initialTypeRadios()}
                        </RadioGroup>
                    )}
                </FormItem>
                <FormItem
                    label='对接目标'
                    {...formItemLayout}
                >
                    {getFieldDecorator('target', {
                        rules: [{
                            required: true,
                            message: '请选择对接目标'
                        }]
                    })(
                        <Select
                            placeholder="请选择对接目标"
                        >
                            { addTargets }
                        </Select>
                    )}
                    <a onClick={onPreviewMetaData}>预览元数据</a>
                </FormItem>
                <FormItem
                    label='所属类目'
                    {...formItemLayout}
                >
                    {getFieldDecorator('catalogueId', {
                        rules: [{
                            required: true,
                            message: '请选择所属类目'
                        }]
                    })(
                        <CatalogueSelect
                            showSearch
                            placeholder="请选择所属类目"
                        />
                    )}
                </FormItem>
                <FormItem
                    label='生命周期'
                    {...formItemLayout}
                >
                    {getFieldDecorator('lifecycle', {
                        rules: [{
                            required: true,
                            message: '生命周期不可为空'
                        }]
                    })(
                        <LifeCycleSelect width={100} inputWidth={175} />
                    )}
                </FormItem>
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
