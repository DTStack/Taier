import * as React from 'react'
import { isEmpty } from 'lodash';
import {
    Form, Input as mInput, Select,
    Modal, Button as mButton, message
} from 'antd'

import Api from '../../../api/dataModel';
import {
    formItemLayout
} from '../../../comm/const';

const FormItem = Form.Item
const Option: any = Select.Option;
const Button: any = mButton;
const Input: any = mInput;
class DeriveIndexModal extends React.Component<any, any> {
    state: any = {
        indexNames: [],
        automIndexs: [],
        columnTypes: []
    }

    componentDidMount () {
        // 加载原子指标
        this.loadAtomIndex();
        this.getColumnType();
    }

    // eslint-disable-next-line
	UNSAFE_componentWillReceiveProps(nextProps: any) {
        if (!this.props.visible && nextProps.visible) {
            const isEdit = nextProps.data && !isEmpty(nextProps.data);
            this.loadAtomIndex(isEdit);
            if (isEdit) {
                const indexNames = nextProps.data.columnName.split('_');
                this.setState({
                    indexNames: indexNames
                })
            }
        }
    }

    getColumnType () {
        Api.getColumnType().then((res: any) => {
            if (res.code === 1) {
                this.setState({
                    columnTypes: res.data || []
                })
            }
        })
    }

    loadAtomIndex = (isEdit?: any) => {
        Api.getModelIndexs({
            type: 1,
            pageSize: 1000,
            currentPage: 1
        }).then((res: any) => {
            if (res.code === 1) {
                const automIndexs = res.data ? res.data.data : []
                const initalState: any = { automIndexs }
                if (!isEdit) {
                    initalState.indexNames = automIndexs && automIndexs.length > 0 ? [automIndexs[0].columnName] : [];
                }
                this.setState(initalState)
            }
        });
    }

    submit = (e: any) => {
        e.preventDefault()
        const { handOk, form, data } = this.props;
        const { indexNames } = this.state;

        if (indexNames.length === 0) {
            message.error('请配置您的指标名称！')
            return false;
        }

        const formData = this.props.form.getFieldsValue()
        formData.type = 2; // 1 - 原子指标, 2 - 衍生指标,
        formData.columnName = indexNames.length > 0 ? indexNames.join('_') : '';
        formData.isEdit = data && !isEmpty(data) ? true : undefined;
        formData.id = formData.isEdit ? data.id : undefined;

        this.props.form.validateFields((err: any) => {
            if (!err) {
                setTimeout(() => {
                    form.resetFields()
                }, 200)
                handOk(formData)
            }
        });
    }

    changeIndexName = (value: any, index: any) => {
        const newArrs: any = [...this.state.indexNames];
        newArrs[index] = value;
        this.setState({
            indexNames: newArrs
        });
    }

    insertIndexName = (index: any) => {
        const { automIndexs } = this.state;
        const originArr = this.state.indexNames;

        const start = index + 1;
        let arrOne = originArr.slice(0, start);
        const arrTwo = originArr.slice(start, originArr.length);

        // Insert a default object to array.
        arrOne.push(automIndexs[0].columnName);

        arrOne = arrOne.concat(arrTwo);

        this.setState({
            indexNames: [...arrOne]
        });
    }

    removeIndexName = (index: any) => {
        const originArr: any = [...this.state.indexNames];
        originArr.splice(index, 1)
        this.setState({
            indexNames: originArr
        });
    }

    cancle = () => {
        const { handCancel, form } = this.props
        this.setState({ }, () => {
            handCancel()
            form.resetFields()
        })
    }

    renderIndexNames = () => {
        const { indexNames, automIndexs } = this.state;
        const length = indexNames.length;

        const options = automIndexs && automIndexs.map((atomIndex: any, index: any) => <Option
            key={atomIndex.id}
            index={index}
            value={atomIndex.columnName}
        >
            {atomIndex.columnName}
        </Option>);

        return indexNames && indexNames.map((indexName: any, index: any) => <span
            style={{ display: 'inline-block', marginBottom: '5px' }}
            key={index}>
            <Select
                showSearch
                placeholder="请选择"
                value={indexName}
                style={{ width: 126, marginRight: '5px' }}
                onSelect={(value: any, option: any) => this.changeIndexName(value, index)}
            >
                {options}
            </Select>
            {
                (index == length - 1) && length > 1 ? <Button
                    icon="minus"
                    title="移除规则"
                    style={{ marginRight: '5px' }}
                    onClick={() => this.removeIndexName(index)}
                />
                    : <Button
                        icon="plus"
                        title="添加规则"
                        style={{ marginRight: '5px' }}
                        onClick={() => this.insertIndexName(index)}
                    />
            }
        </span>
        );
    }

    render () {
        const {
            form, visible, data
        } = this.props;

        const { indexNames, automIndexs, columnTypes } = this.state;

        const { getFieldDecorator } = form

        const isEdit = data && !isEmpty(data);
        const title = isEdit ? '编辑衍生指标' : '创建衍生指标'
        // const TYPES =[
        //     "TINYINT", "SMALLINT", "INT", "BIGINT", "BOOLEAN",
        //     "FLOAT", "DOUBLE", "STRING", "BINARY", "TIMESTAMP",
        //     "DECIMAL", "DATE", "VARCHAR", "CHAR"
        // ];

        return (
            <Modal
                title={title}
                visible={visible}
                onOk={this.submit}
                onCancel={this.cancle}
                maskClosable={false}
            >
                <Form>
                    <FormItem
                        {...formItemLayout}
                        label="衍生指标名称"
                        hasFeedback
                    >
                        {getFieldDecorator('columnNameZh', {
                            rules: [{
                                required: true, message: '衍生指标名称不可为空！'
                            }, {
                                max: 64,
                                message: '衍生指标名称不得超过64个字符！'
                            }],
                            initialValue: data ? data.columnNameZh : ''
                        })(
                            <Input />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标口径(描述)"
                        hasFeedback
                    >
                        {getFieldDecorator('modelDesc', {
                            rules: [{
                                max: 200,
                                message: '指标口径请控制在200个字符以内！'
                            }],
                            initialValue: data ? data.modelDesc : ''
                        })(
                            <Input type="textarea" rows={4} />
                        )}
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标命名"
                        hasFeedback
                    >
                        {
                            automIndexs && automIndexs.length > 0 ? this.renderIndexNames() : <span style={{ color: '#f00' }}>请先创建原子指标</span>
                        }
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="当前命名"
                        style={{ wordBreak: 'break-all' }}
                    >
                        {
                            indexNames && indexNames.length > 0 ? indexNames.join('_') : <span style={{ color: '#f00' }}>请先创建原子指标</span>
                        }
                    </FormItem>
                    <FormItem
                        {...formItemLayout}
                        label="指标类型"
                        hasFeedback
                    >
                        {getFieldDecorator('dataType', {
                            rules: [],
                            initialValue: data ? data.dataType : 'STRING'
                        })(
                            <Select>
                                {columnTypes.map((str: any) => <Option key={str} value={str}>{str}</Option>)}
                            </Select>
                        )}
                    </FormItem>
                </Form>
            </Modal>
        )
    }
}
const wrappedForm = Form.create<any>()(DeriveIndexModal);
export default wrappedForm
