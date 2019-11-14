import * as React from 'react';
import { Input, Form, Select, Button } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { debounce } from 'lodash';

import './style.scss';

const { TextArea } = Input;
const { Option } = Select;

interface IProps extends FormComponentProps {
    onNext: Function;
    onPrev: Function;
    isShow: boolean;
}
interface IState {
    indexList: any[];
    keyList: any[];
    index: string|number;
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
class StepOne extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        indexList: [],
        keyList: [],
        index: ''
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
    getKeyList =(index: number) => {
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
        const { getFieldDecorator } = form;
        return (
            <div className="stepOne" style={{ display: isShow ? 'block' : 'none' }}>
                <Form.Item {...formItemLayout} label="标签名称">
                    {getFieldDecorator('entityName', {
                        rules: [
                            {
                                required: true,
                                max: 20,
                                pattern: /^[\u4e00-\u9fa5]{0,}$/,
                                message: '请输入实体中文名称，20字以内的中文字符'
                            }, {
                                validator: this.validateName
                            }
                        ]
                    })(<Input placeholder="请输入实体中文名称，20字以内的中文字符" />)}
                </Form.Item>
                <Form.Item {...formItemLayout} label="选择目录">
                    {getFieldDecorator('entityIndex', {
                        rules: [
                            {
                                message: '请选择目录'
                            }
                        ]
                    })(
                        <Select placeholder="请选择目录" showSearch onChange={(value) => this.onChangeSelect(value, 'index')} style={{ width: '100%' }}>
                            {
                                indexList.map(item => <Option value={item} key={item}>{item}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="标签描述">
                    {getFieldDecorator('description', {
                        rules: [
                            {
                                required: false,
                                message: '请输入标签描述'
                            }
                        ]
                    })(<TextArea rows={4} maxLength={255} placeholder="请输入标签描述信息，长度限制在255个字符以内" />)}
                </Form.Item>
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>退出</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepOne);
