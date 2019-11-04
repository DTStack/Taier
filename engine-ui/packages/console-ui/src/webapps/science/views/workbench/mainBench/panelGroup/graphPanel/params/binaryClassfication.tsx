import * as React from 'react';
import { Form, Select, Input, InputNumber, message, Spin } from 'antd';
import { debounce, isNumber } from 'lodash';
import api from '../../../../../../api/experiment';
import { TASK_ENUM, COMPONENT_TYPE } from '../../../../../../consts';
import { formItemLayout } from './index';
const FormItem = Form.Item;
const Option = Select.Option;
// 表选择
class FieldSetting extends React.PureComponent<any, any> {
    state: any = {
        originalColumns: [],
        fetching: false
    }
    getColumns = () => {
        const { currentTab, componentId } = this.props;
        const targetEdge = currentTab.graphData.find((o: any) => {
            return o.edge && o.target.data.id == componentId
        })
        if (targetEdge) {
            this.setState({
                fetching: true
            });
            api.getInputTableColumns({ taskId: componentId, inputType: targetEdge.inputType }).then((res: any) => {
                if (res.code === 1) {
                    let originalColumns: any = [];
                    for (const key in res.data) {
                        if (res.data.hasOwnProperty(key)) {
                            const element = res.data[key];
                            originalColumns.push({
                                key,
                                type: element
                            })
                        }
                    }
                    this.setState({
                        originalColumns
                    })
                }
                this.setState({
                    fetching: false
                });
            })
        }
    }
    handleChange = (value: any) => {
        const { originalColumns } = this.state;
        const object = originalColumns.find((o: any) => o.key === value);
        if (object) {
            this.props.handleSaveComponent('oldLabel', object);
        }
    }
    render () {
        const { getFieldDecorator } = this.props.form;
        const { originalColumns, fetching } = this.state;
        return (
            <Form className="params-form">
                <FormItem
                    label='原始标签列列名'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('oldLabel', {
                        rules: [{ required: true, message: '请选择原始标签列列名' }]
                    })(
                        <Select
                            notFoundContent={fetching ? <Spin size="small" /> : '未找到数据表'}
                            onFocus={this.getColumns}
                            onChange={this.handleChange}>
                            {
                                originalColumns.map((item: any, index: any) => {
                                    return <Option key={item.key} value={String(item.key)}>{item.key}</Option>
                                })
                            }
                        </Select>
                    )}
                </FormItem>
                <FormItem
                    label='明细列列名'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('detailCol', {
                        rules: [{ required: true, message: '请输入明细列列名' }]
                    })(
                        <Input placeholder="请输入明细列列名" />
                    )}
                </FormItem>
                <FormItem
                    label='正样本的标签值'
                    colon={false}
                    {...formItemLayout}
                >
                    {getFieldDecorator('pos', {
                        rules: [{ required: true, message: '请输入正样本的标签值' }]
                    })(
                        <Input placeholder="请输入正样本的标签值" />
                    )}
                </FormItem>
                <FormItem
                    colon={false}
                    label='计算KS,PR等指标时按等频分成多少个桶'
                    {...formItemLayout}
                >
                    {getFieldDecorator('bin', {
                        initialValue: 100,
                        rules: [
                            { required: true, message: '请填写桶数' },
                            { max: 1000, message: '不可超过1000个桶', type: 'number' }
                        ]
                    })(
                        <InputNumber
                            parser={(value: any) => isNumber(value) ? ~~value : value}
                            formatter={(value: any) => isNumber(value) ? ~~value : value}
                            style={{ width: '100%' }} />
                    )}
                </FormItem>
            </Form>
        );
    }
}

/* main页面 */
class BinaryClassfication extends React.PureComponent<any, any> {
    constructor (props: any) {
        super(props);
        this.handleSaveComponent = debounce(this.handleSaveComponent, 800);
    }
    handleSaveComponent = (field: any, filedValue: any) => {
        const { data, currentTab, componentId, changeContent } = this.props;
        const fieldName = TASK_ENUM[COMPONENT_TYPE.DATA_EVALUATE.CONFUSION_MATRIX];
        const currentComponentData = currentTab.graphData.find((o: any) => o.vertex && o.data.id === componentId);
        const params: any = {
            ...currentComponentData.data,
            [fieldName]: {
                ...data
            }
        }
        if (field) {
            params[fieldName][field] = filedValue
        }
        api.addOrUpdateTask(params).then((res: any) => {
            if (res.code == 1) {
                currentComponentData.data = { ...params, ...res.data };
                changeContent({}, currentTab);
            } else {
                message.warning('保存失败');
            }
        })
    }
    render () {
        const { data, currentTab, componentId } = this.props;
        const WrapFieldSetting = Form.create({
            onFieldsChange: (props: any, changedFields: any) => {
                for (const key in changedFields) {
                    if (changedFields.hasOwnProperty(key)) {
                        if (key === 'oldLabel') {
                            // label是下拉菜单，在组件里自己触发onChange函数,对数据封装过后再请求
                            continue;
                        }
                        const element = changedFields[key];
                        if (!element.validating && !element.dirty && !element.errors) {
                            props.handleSaveComponent(key, element.value)
                        }
                    }
                }
            },
            mapPropsToFields: (props: any) => {
                const { data } = props;
                const values: any = {
                    oldLabel: { value: !data.oldLabel ? '' : data.oldLabel.key },
                    detailCol: { value: data.detailCol },
                    pos: { value: data.pos },
                    bin: { value: data.bin }
                }
                return values;
            }
        })(FieldSetting);
        return (
            <div className="params-single-tab">
                <div className="c-panel__siderbar__header">
                    字段设置
                </div>
                <div className="params-single-tab-content">
                    <WrapFieldSetting data={data} handleSaveComponent={this.handleSaveComponent} currentTab={currentTab} componentId={componentId} />
                </div>
            </div>
        );
    }
}

export default BinaryClassfication;
