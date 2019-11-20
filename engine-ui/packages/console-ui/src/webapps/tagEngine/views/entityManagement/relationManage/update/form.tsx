import * as React from 'react';
import { Form, Input, Tooltip, Icon, Select, Button } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { get } from 'lodash';

import { IDataSource } from '../../../../model/dataSource';
import { formItemLayout } from '../../../../comm/const';

interface IProps extends FormComponentProps {
    dataSourceList: IDataSource[];
    onCreateRelationEntity: (e: React.FormEvent) => void;
    mode: 'edit' | 'create';
    formData?: any;
};

const FormItem = Form.Item;
const Option = Select.Option;

class CreateRelationEntityForm extends React.Component<IProps, any> {
    render () {
        const { form, dataSourceList, onCreateRelationEntity, formData } = this.props;
        const { getFieldDecorator } = form;
        return (
            <Form>
                <FormItem
                    {...formItemLayout}
                    label={(<span>
                        关系名称&nbsp;
                        <Tooltip title="仅支持同类型数据源的实体间创建关系模型">
                            <Icon type="question-circle-o" />
                        </Tooltip>
                    </span>)}
                    hasFeedback
                >
                    {getFieldDecorator('relationName', {
                        rules: [{
                            required: true, message: '请输入关系名称!'
                        }, {
                            pattern: /^[\u4e00-\u9fa5]+$/,
                            message: '实体名称仅支持中文字符!'
                        }, {
                            max: 20,
                            message: '实体名称20字以内的中文字符!'
                        }],
                        initialValue: get(formData, 'name', '')
                    })(
                        <Input placeholder="请输入实体中文名称，20字以内的中文字符"/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="请选择数据类型"
                    hasFeedback
                >
                    {getFieldDecorator('dataSource', {
                        rules: [{
                            required: true, message: '请选择数据类型!'
                        }],
                        initialValue: get(formData, 'dataSource', '')
                    })(
                        <Select
                            placeholder="请选择数据类型"
                            style={{ width: 200 }}
                        >
                            { dataSourceList && dataSourceList.map((o: IDataSource) => {
                                return <Option key={o.name} value={o.value}>{o.name}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="关系描述"
                    hasFeedback
                >
                    {getFieldDecorator('description', {
                        rules: [],
                        initialValue: get(formData, 'description', '')
                    })(
                        <Input.TextArea
                            placeholder="请输入描述信息，长度限制在255个字符以内"
                            rows={4}
                        />
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="配置关联实体"
                    required
                    hasFeedback
                >
                    <Button type="primary" icon="plus" onClick={onCreateRelationEntity}>新增实体</Button>
                </FormItem>
            </Form>
        )
    }
}

const WrappedCreateRelationEntityForm = Form.create()(CreateRelationEntityForm);

export default WrappedCreateRelationEntityForm
