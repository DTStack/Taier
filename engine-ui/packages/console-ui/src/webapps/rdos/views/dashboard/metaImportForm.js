import React from 'react';

import { formItemLayout } from '../../comm/const';

import { Form, Select, Input } from 'antd';
import MetaDataTable from './metaDataTable';
import CatalogueSelect from '../../components/catalogueSelect';
import LifeCycleSelect from '../../components/lifeCycleSelect';

const FormItem = Form.Item;
const Option = Select.Option;
const TextArea = Input.TextArea;

class MetaDataForm extends React.Component {
    getProjectOptions () {
        const { projectList = [] } = this.props;
        return projectList.map((project) => {
            return <Option key={project}>{project}</Option>
        });
    }
    render () {
        const { projectName } = this.props;
        const { getFieldDecorator } = this.props.form;
        return (
            <Form>
                <FormItem
                    label='项目标识'
                    {...formItemLayout}
                >
                    {getFieldDecorator('projectName', {
                        rules: [{
                            required: true,
                            message: '请选择项目标识'
                        }]
                    })(
                        <Select
                            style={{ width: '350px' }}
                            placeholder="请选择项目标识"
                        >
                            {this.getProjectOptions()}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='项目显示名称'
                    {...formItemLayout}
                >
                    {getFieldDecorator('projectAlias', {
                        rules: [{
                            required: false,
                            message: '请输入项目显示名称'
                        }]
                    })(
                        <Input
                            style={{ width: '350px' }}
                            placeholder="请输入项目显示名称"
                        />
                    )}
                </FormItem>
                <FormItem
                    label='项目描述'
                    {...formItemLayout}
                >
                    {getFieldDecorator('projectDesc', {
                        rules: [{
                            required: false,
                            message: '项目描述请控制在200个字符以内'
                        }, {
                            max: 200,
                            message: '项目描述不得超过200个字符！'
                        }]
                    })(
                        <TextArea
                            style={{ width: '350px' }}
                            placeholder="请输入项目描述"
                            autosize={{ minRows: 2, maxRows: 7 }}
                        />
                    )}
                </FormItem>
                <MetaDataTable key={projectName} database={projectName} />
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
                            style={{ width: '350px' }}
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
                        <LifeCycleSelect width={350} />
                    )}
                </FormItem>
            </Form>
        )
    }
}
export default Form.create({
    mapPropsToFields: (props) => {
        return {
            projectName: {
                value: props.projectName
            },
            projectAlias: {
                value: props.projectAlias
            },
            projectDesc: {
                value: props.projectDesc
            },
            catalogueId: {
                value: props.catalogueId
            },
            lifecycle: {
                value: props.lifecycle
            }
        }
    },
    onValuesChange: (props, values) => {
        props.onChange(values);
    }
})(MetaDataForm);
