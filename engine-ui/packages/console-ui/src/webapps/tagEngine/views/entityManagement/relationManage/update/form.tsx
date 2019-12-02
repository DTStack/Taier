import * as React from 'react';
import { Form, Input, Tooltip, Icon, Select, Button } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { get } from 'lodash';

import { IDataSource } from '../../../../model/dataSource';
import { formItemLayout } from '../../../../comm/const';
import { IRelation } from '../../../../model/relation';
import { GRAPH_MODE } from '../../../../components/relationGraph';

interface IProps {
    dataSourceList: IDataSource[];
    onCreateRelationEntity: (e: React.FormEvent) => void;
    mode: GRAPH_MODE;
    formData?: IRelation;
    loadDataSource: (query?: string) => void;
    wrappedComponentRef?: (inst?: any) => void;
    onFormValuesChange?: (values: IRelation) => void;
};

const FormItem = Form.Item;
const Option = Select.Option;

class CreateRelationEntityForm extends React.Component<IProps & FormComponentProps, any> {
    render () {
        const { form, dataSourceList = [], onCreateRelationEntity, formData = {} } = this.props;
        const { getFieldDecorator } = form;
        const selectedSource = get(formData, 'dataSourceId') || form.getFieldValue('dataSourceId');
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
                            pattern: /^[\w\u4e00-\u9fa5]+$/,
                            message: '实体名称仅支持中文字符!'
                        }, {
                            max: 20,
                            message: '实体名称20字以内的中文字符!'
                        }],
                        initialValue: get(formData, 'relationName', '')
                    })(
                        <Input placeholder="请输入实体中文名称，20字以内的中文字符"/>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="请先选择数据源"
                    hasFeedback
                >
                    {getFieldDecorator('dataSourceId', {
                        rules: [{
                            required: true, message: '请先选择数据源!'
                        }],
                        initialValue: get(formData, 'dataSourceId')
                    })(
                        <Select
                            showSearch
                            optionFilterProp="title"
                            placeholder="请选择数据类型"
                            style={{ width: 200 }}
                        >
                            { dataSourceList && dataSourceList.map((o: IDataSource) => {
                                return <Option key={o.id} value={o.id} title={o.dataName}>{o.dataName}</Option>
                            })}
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    {...formItemLayout}
                    label="关系描述"
                    hasFeedback
                >
                    {getFieldDecorator('relationDesc', {
                        rules: [],
                        initialValue: get(formData, 'relationDesc', '')
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
                    <Button type="primary" icon="plus"
                        disabled={!selectedSource}
                        onClick={onCreateRelationEntity}
                    >
                        {selectedSource ? '新增实体' : '请先选择数据源' }
                    </Button>
                </FormItem>
            </Form>
        )
    }
}

const WrappedCreateRelationEntityForm = Form.create<IProps>({
    onValuesChange: (props: IProps, values) => {
        props.onFormValuesChange(values);
    }
})(CreateRelationEntityForm);

export default WrappedCreateRelationEntityForm
