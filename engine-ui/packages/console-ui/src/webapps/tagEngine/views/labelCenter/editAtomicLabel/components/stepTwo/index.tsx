import * as React from 'react';
import { Form, Select, Button, Radio, Input } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import SetDictionary from '../../../../../components/setDictionary';
import { debounce } from 'lodash';
import './style.scss';
import { Link } from 'react-router';
const { Option } = Select;

interface IProps extends FormComponentProps {
    onNext: Function;
    onPrev: Function;
    isShow: boolean;
}
interface IState {
    indexList: any[];
    keyList: any[];
    index: string | number;
    select: '';
    tags: any[];
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
        indexList: ['name'],
        keyList: [],
        index: '',
        select: '',
        tags: []
    };
    componentDidMount () {
        this.loadMainData(false);
    }
    loadMainData (isClear: boolean) {
        if (isClear) {
            // 清除一些过滤条件
        }
        // API.indexListUsingGet({}).then(res => { // 获取索引列表
        //     const { success, data, message } = res;
        //     if (success) {
        //         this.setState({
        //             indexList: data
        //         })
        //     } else {
        //         Message.error(message)
        //     }
        // })
    }
    getKeyList = (index: number) => {
        // API.keyListUsingGet({
        //     index
        // }).then(res => { // 获取主键列表
        //     const { success, data, message } = res;
        //     if (success) {
        //         this.setState({
        //             keyList: data
        //         })
        //     } else {
        //         Message.error(message)
        //     }
        // })
    }
    onChangeSelect = (value, type) => {
        if (type == 'tags') {
            this.setState({
                select: value
            })
        }
    }
    onHandleNext = (e: any) => {
        this.props.form.validateFields((err, values) => {
            console.log(err, values)
            if (!err) {
                this.props.onNext(values);
            }
        });
    }
    onHandlePrev = () => {
        this.props.onPrev();
    }
    validateName = debounce((rule, value, callback) => {
        // if (value) {
        //     let text = '标签名称不可以重复'
        //     API.entityDistinctUsingPost({
        //         entityCode: '',
        //         entityName: value
        //     }).then(res => {
        //         const { success, data } = res;
        //         if (success) {
        //             if (!data) {
        //                 callback(text)
        //             } else {
        //                 callback()
        //             }
        //         } else {
        //             callback(text)
        //         }
        //     })
        // }
        callback()
    }, 800)
    render () {
        const { form, isShow } = this.props;
        const { indexList } = this.state;
        const { getFieldDecorator, getFieldValue } = form;
        return (
            <div className="atom_stepTwo" style={{ display: isShow ? 'block' : 'none' }}>
                <Form.Item {...formItemLayout} label="选择实体">
                    {getFieldDecorator('entityIndex', {
                        rules: [
                            {
                                required: true,
                                message: '请选择实体'
                            }
                        ]
                    })(
                        <Select placeholder="请选择实体" showSearch onChange={(value) => this.onChangeSelect(value, 'index')} style={{ width: '100%' }}>
                            {
                                indexList.map(item => <Option value={item} key={item}>{item}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="选择维度">
                    {getFieldDecorator('entityIndex', {
                        rules: [
                            {
                                message: '请选择维度'
                            }
                        ]
                    })(
                        <Select placeholder="请选择维度" showSearch onChange={(value) => this.onChangeSelect(value, 'index')} style={{ width: '100%' }}>
                            {
                                indexList.map(item => <Option value={item} key={item}>{item}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="数据类型">
                    {getFieldDecorator('tags', {
                        rules: [
                            {
                                required: true,
                                message: '请选择数据类型'
                            }
                        ]
                    })(
                        <Select placeholder="请选择维度" showSearch onChange={(value) => this.onChangeSelect(value, 'index')} style={{ width: '100%' }}>
                            {
                                indexList.map(item => <Option value={item} key={item}>{item}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="查看标签值">
                    <Link to="#">标签值详情</Link>
                </Form.Item>
                <Form.Item {...formItemLayout} label="字典类型">
                    {getFieldDecorator('dictType', {
                        initialValue: 1,
                        rules: [
                            {
                                required: true,
                                message: '请选择字典类型'
                            }
                        ]
                    })(
                        <Radio.Group>
                            <Radio value={1}>自定义</Radio>
                            <Radio value={0}>引用</Radio>
                        </Radio.Group>
                    )}
                </Form.Item>
                {getFieldValue('dictType') ? (
                    <React.Fragment>
                        <Form.Item {...formItemLayout} label="字典名称">
                            {getFieldDecorator('dictName', {
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
                            {getFieldDecorator('dictDataList', {
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
                        {getFieldDecorator('dictId', {
                            initialValue: undefined,
                            rules: [
                                {
                                    required: true,
                                    message: '请选择引用字典'
                                }
                            ]
                        })(
                            <Select placeholder="请选择引用字典" showSearch onChange={(value) => this.onChangeSelect(value, 'index')} style={{ width: '100%' }}>
                                {
                                    indexList.map(item => <Option value={item} key={item}>{item}</Option>)
                                }
                            </Select>
                        )}
                    </Form.Item>
                )}
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>退出</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
