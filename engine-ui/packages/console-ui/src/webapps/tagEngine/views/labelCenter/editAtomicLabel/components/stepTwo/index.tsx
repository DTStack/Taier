import * as React from 'react';
import { Form, Select, Button, Radio, Input } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import SetDictionary from '../../../../../components/setDictionary';
import { API } from '../../../../../api/apiMap';

import './style.scss';
import { Link } from 'react-router';
const { Option } = Select;

interface IProps extends FormComponentProps {
    onNext: Function;
    onPrev: Function;
    isShow: boolean;
    entityId: string|number;
    data: any;
}
interface IState {
    entityList: any[];
    dimensionList: any[];
    dataTypeList: any[];
    dictList: any[];
    select: '';
    tags: any[];
    radio: any;
}
const formItemLayout = {
    labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
    },
    wrapperCol: {
        xs: { span: 24 },
        sm: { span: 12 }
    }
};
class StepTwo extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        entityList: [],
        dimensionList: [],
        dataTypeList: [],
        dictList: [],
        select: '',
        tags: [],
        radio: 0
    };
    componentDidMount () {
        this.loadMainData(false);
    }
    componentDidUpdate (preProps) {
        const { data } = this.props;
        if (data != preProps.data) {
            const { entityId, dimensionalityId, dateType, tagDictId, tagDictName, dictValueVoList = [] } = data;
            if (tagDictId) {
                this.setState({
                    radio: 0
                });
                this.props.form.setFieldsValue({ entityId, dimensionalityId, dateType, tagDictId })
            } else {
                this.props.form.setFieldsValue({
                    entityId,
                    dimensionalityId,
                    dateType,
                    tagDictId,
                    tagDictName,
                    dictValueVoList: dictValueVoList.map(item => {
                        return {
                            key: item.id,
                            id: item.id,
                            name: item.valueName,
                            value: item.value
                        }
                    }) })
            }
        }
    }
    loadMainData (isClear: boolean) {
        this.getEntityList();
        this.selectEntityAttrs();
        this.getDataType();
        this.getDictListByType()
    }
    getEntityList = () => { // 获取实体
        API.selectEntity().then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    entityList: data
                });
            }
        })
    }
    selectEntityAttrs = () => { // 获取所属维度
        const { entityId } = this.props;
        API.selectEntityAttrs({ entityId }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    dimensionList: data
                });
            }
        })
    }
    getDataType = () => { // 获取数据类型
        API.getDataType().then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    dataTypeList: data
                });
            }
        })
    }
    getDictListByType = () => { // 获取字典引用
        API.getDictListByType({ dictType: 0 }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    dictList: data
                });
            }
        })
    }
    onHandleNext = (e: any) => {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                let dictValueVoList = []
                if (values.dictValueVoList && values.dictValueVoList.length) {
                    dictValueVoList = values.dictValueVoList.map(item => {
                        return {
                            id: item.id,
                            value: item.value,
                            valueName: item.name
                        }
                    })
                }
                this.props.onNext(Object.assign({}, values, { dictValueVoList }));
            }
        });
    }
    onHandlePrev = () => {
        this.props.onPrev();
    }
    onSelectRadio = (e) => {
        const value = e.target.value
        this.setState({
            radio: value
        })
    }
    render () {
        const { form, isShow } = this.props;
        const { entityList, dimensionList, dataTypeList, dictList, radio } = this.state;
        const { getFieldDecorator } = form;
        return (
            <div className="atom_stepTwo" style={{ display: isShow ? 'block' : 'none' }}>
                <Form.Item {...formItemLayout} label="选择实体">
                    {getFieldDecorator('entityId', {
                        rules: [
                            {
                                required: true,
                                message: '请选择实体'
                            }
                        ]
                    })(
                        <Select placeholder="请选择实体" disabled showSearch style={{ width: '100%' }}>
                            {
                                entityList.map(item => <Option value={item.id} key={item.id}>{item.entityName}</Option>)

                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="选择维度">
                    {getFieldDecorator('dimensionalityId', {
                        rules: [
                            {
                                message: '请选择维度'
                            }
                        ]
                    })(
                        <Select placeholder="请选择维度" disabled showSearch style={{ width: '100%' }}>
                            {
                                dimensionList.map(item => <Option value={item.id} key={item.id}>{item.entityAttrCn}</Option>)

                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="数据类型">
                    {getFieldDecorator('dateType', {
                        rules: [
                            {
                                required: true,
                                message: '请选择数据类型'
                            }
                        ]
                    })(
                        <Select placeholder="请选择数据类型" disabled showSearch style={{ width: '100%' }}>
                            {
                                dataTypeList.map(item => <Option value={item.val} key={item.val}>{item.desc}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="查看标签值">
                    <Link to="#">标签值详情</Link>
                </Form.Item>
                <Form.Item {...formItemLayout} label="字典类型">
                    <Radio.Group value={radio} onChange={this.onSelectRadio}>
                        <Radio value={1}>自定义</Radio>
                        <Radio value={0}>引用</Radio>
                    </Radio.Group>
                </Form.Item>
                { radio ? (
                    <React.Fragment>
                        <Form.Item {...formItemLayout} label="字典名称">
                            {getFieldDecorator('tagDictName', {
                                initialValue: [],
                                rules: [
                                    {
                                        required: true,
                                        message: '请设置字典名称'
                                    }
                                ]
                            })(<Input placeholder="请设置字典名称"/>)}
                        </Form.Item>
                        <Form.Item {...formItemLayout} label="字典值设置">
                            {getFieldDecorator('dictValueVoList', {
                                initialValue: [],
                                rules: [
                                    {
                                        required: true,
                                        message: '请设置字典值'
                                    }
                                ]
                            })(<SetDictionary isEdit={true} />)}
                        </Form.Item>
                    </React.Fragment>
                ) : (
                    <Form.Item {...formItemLayout} label="字典引用">
                        {getFieldDecorator('tagDictId', {
                            rules: [
                                {
                                    required: true,
                                    message: '请选择引用字典'
                                }
                            ]
                        })(
                            <Select placeholder="请选择引用字典" showSearch style={{ width: '100%' }}>
                                {
                                    dictList.map(item => <Option value={item.id} key={item.id}>{item.name}</Option>)
                                }
                            </Select>
                        )}
                    </Form.Item>
                )}
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>上一步</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
