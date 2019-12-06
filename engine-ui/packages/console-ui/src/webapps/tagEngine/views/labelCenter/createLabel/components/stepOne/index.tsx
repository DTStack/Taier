import * as React from 'react';
import { Input, Form, Button, TreeSelect } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { API } from '../../../../../api/apiMap';

import './style.scss';

const { TextArea } = Input;
const TreeNode: any = TreeSelect;
interface IProps extends FormComponentProps {
    onNext: Function;
    onPrev: Function;
    isShow: boolean;
    entityId: string|number;
    data: any;
}
interface IState {
    cateOption: any[];
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
        cateOption: [],
        index: ''
    };
    componentDidMount () {
        this.loadMainData();
    }
    componentDidUpdate (preProps) {
        const { data } = this.props;
        if (data != preProps.data) {
            const { tagName, cateId, tagDesc } = data;
            this.props.form.setFieldsValue({ tagName, cateId, tagDesc })
        }
    }
    loadMainData () {
        const { entityId } = this.props;
        API.getTagCate({
            entityId
        }).then(res => { // 获取主键列表
            const { code, data } = res;
            if (code) {
                this.setState({
                    cateOption: data
                });
            }
        })
    }
    onHandleNext = (e: any) => {
        this.props.form.validateFields((err, values) => {
            if (!err) {
                this.props.onNext(values);
            }
        });
    }
    onHandlePrev = () => {
        this.props.onPrev();
    }
    renderTreeNode = (data) => {
        return data.map((item: any) => {
            if (item.children && item.children.length) {
                return (
                    <TreeNode value={item.tagCateId} title={item.cateName} key={item.tagCateId}>

                        {
                            this.renderTreeNode(item.children)
                        }
                    </TreeNode>
                )
            }
            return <TreeNode value={item.tagCateId} title={item.cateName} key={item.tagCateId} />
        })
    }

    render () {
        const { form, isShow } = this.props;
        const { cateOption } = this.state;
        const { getFieldDecorator } = form;
        return (
            <div className="stepOne" style={{ display: isShow ? 'block' : 'none' }}>
                <Form.Item {...formItemLayout} label="标签名称">
                    {getFieldDecorator('tagName', {
                        rules: [
                            {
                                required: true,
                                max: 80,
                                pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/,
                                message: '姓名只能包括汉字，字母、下划线、数字'
                            }
                        ]
                    })(<Input placeholder="请输入标签名称" />)}
                </Form.Item>
                <Form.Item {...formItemLayout} label="选择目录">
                    {getFieldDecorator('cateId', {
                        rules: [
                            {
                                required: true,
                                message: '请选择目录'
                            }
                        ]
                    })(
                        <TreeSelect
                            showSearch
                            style={{ width: '100%' }}
                            dropdownStyle={{ maxHeight: 400, overflow: 'auto' }}
                            placeholder="请选择目录"
                            treeDefaultExpandAll
                        >
                            {
                                this.renderTreeNode(cateOption)
                            }
                        </TreeSelect>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="标签描述">
                    {getFieldDecorator('tagDesc', {
                        rules: [
                            {
                                required: false,
                                message: '请输入标签描述'
                            }
                        ]
                    })(<TextArea rows={4} maxLength={500} placeholder="请输入标签描述信息，长度限制在500个字符以内" />)}
                </Form.Item>
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>取消</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepOne);
