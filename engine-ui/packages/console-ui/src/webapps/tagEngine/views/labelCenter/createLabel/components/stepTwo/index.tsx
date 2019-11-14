import * as React from 'react';
import { Form, Select, Button, Input, Icon } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import { debounce } from 'lodash';
import classnames from 'classnames';
import SelectLabelRow from '../selectLabelRow';
import './style.scss';
import TagValues from '../tagValues';
import Collapse from '../collapse/index';
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
    treeData: any;
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
        indexList: [],
        keyList: [],
        index: '',
        select: '',
        tags: [],
        treeData: {
            key: '1',
            type: '且', //  且|或
            children: [
                {
                    key: '1-1',
                    type: '或',
                    name: '实体-用户信息',
                    children: [
                        {
                            key: '1-1-1',
                            type: '或',
                            children: [
                                {
                                    key: '1-1-1-1',
                                    selectName: '活跃度',
                                    selectvalue: '活跃度id',
                                    filterType: '字符串', // 字符串|数值|日期|字典
                                    conditionName: '等于',
                                    conditionId: '等于',
                                    filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值或者区间值，延续数值结构，name为空
                                },
                                {
                                    key: '1-1-1-2',
                                    selectName: '活跃度',
                                    selectvalue: '活跃度id',
                                    filterType: '字符串', // 字符串|数值|日期|字典
                                    conditionName: '等于',
                                    conditionId: '等于',
                                    filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值或者区间值，延续数值结构，name为空
                                }
                            ]
                        },
                        {
                            key: '1-1-2',
                            selectName: '活跃度',
                            selectvalue: '活跃度id',
                            filterType: '字符串', // 字符串|数值|日期|字典
                            conditionName: '等于',
                            conditionId: '等于',
                            filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值或者区间值，延续数值结构，name为空
                        }
                    ]
                },
                {
                    key: '1-2',
                    type: '或',
                    name: '实体-活动',
                    children: [
                        {
                            key: '1-2-1',
                            type: '或',
                            children: [
                                {
                                    key: '1-2-1-1',
                                    selectName: '活跃度',
                                    selectvalue: '活跃度id',
                                    filterType: '字符串', // 字符串|数值|日期|字典
                                    conditionName: '等于',
                                    conditionId: '等于',
                                    filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值或者区间值，延续数值结构，name为空
                                }
                            ]
                        },
                        {
                            key: '1-2-2',
                            selectName: '活跃度',
                            selectvalue: '活跃度id',
                            filterType: '字符串', // 字符串|数值|日期|字典
                            conditionName: '等于',
                            conditionId: '等于',
                            filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值或者区间值，延续数值结构，name为空
                        }
                    ]
                },
                {
                    key: '1-3',
                    type: '或',
                    name: '实体-产品',
                    children: [
                        {
                            key: '1-3-2',
                            selectName: '活跃度',
                            selectvalue: '活跃度id',
                            filterType: '字符串', // 字符串|数值|日期|字典
                            conditionName: '等于',
                            conditionId: '等于',
                            filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值或者区间值，延续数值结构，name为空
                        }
                    ]
                }
            ]
        }

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
    onHandleCondition = (key, item, type) => { }
    renderConditionChildren = (data) => {
        return data.map(item => {
            if (item.children && item.children.length) {
                return (
                    <div key={item.key} className={classnames('select_wrap', {
                        active: item.children.length > 1
                    })}>
                        {
                            this.renderConditionChildren(item.children)
                        }
                        <span className="condition" onClick={(e) => this.onHandleCondition(item.key, item, item.type)}>{item.type}</span>
                    </div>
                );
            }
            return <SelectLabelRow data={data} key={data.key} extra={<div>
                <Icon type="plus-circle" className="icon"/>
                <Icon type="minus-circle-o" className="icon"/>
            </div>}/>
        });
    }
    renderCondition = data => {
        if (data.children && data.children.length) {
            return <div className={classnames('select_wrap', {
                active: data.children.length > 1
            })}>
                {
                    this.renderConditionChildren(data.children)
                }
                <span className="condition">{data.type}</span>
            </div>
        }
        return <SelectLabelRow data={data} key={data.key} extra={<div>
            <Icon type="plus-circle" className="icon"/>
            <Icon type="minus-circle-o" className="icon"/>
        </div>}/>
    }

    render () {
        const { form, isShow } = this.props;
        const { indexList, select, treeData } = this.state;
        const { getFieldDecorator } = form;
        return (
            <div className="stepTwo" style={{ display: isShow ? 'block' : 'none' }}>
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
                <Form.Item {...formItemLayout} label="选择关系">
                    {getFieldDecorator('entityIndex', {
                        rules: [
                            {
                                message: '请选择关系'
                            }
                        ]
                    })(
                        <Select placeholder="请选择关系" showSearch onChange={(value) => this.onChangeSelect(value, 'index')} style={{ width: '100%' }}>
                            {
                                indexList.map(item => <Option value={item} key={item}>{item}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="已选标签">
                    {getFieldDecorator('tags', {
                        rules: [
                            {
                                required: true,
                                message: '请选择标签值'
                            }
                        ]
                    })(<TagValues select={select} onSelect={(value) => this.onChangeSelect(value, 'tags')} />)}
                </Form.Item>
                <div className="panel_select">
                    <div className="edit_Wrap"><Input className="edit_value" /><i className="iconfont iconbtn_edit"></i></div>
                    <div className="panel_wrap">
                        <div className="select_wrap active">
                            {
                                treeData.children && treeData.children.map(item => {
                                    return (<Collapse title={item.name} key={item.key} extra={<Icon className="add_icon" type="plus-circle" />}>
                                        {
                                            this.renderCondition(item)
                                        }
                                    </Collapse>)
                                })
                            }
                            <span className="condition">{treeData.type}</span>
                        </div>
                    </div>
                </div>
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>退出</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
