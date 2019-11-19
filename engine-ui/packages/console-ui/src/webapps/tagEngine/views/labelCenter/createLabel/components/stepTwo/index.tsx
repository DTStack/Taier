import * as React from 'react';
import { Form, Select, Button, Input, Icon } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import classnames from 'classnames';
import shortid from 'shortid';
import SelectLabelRow from '../selectLabelRow';
import TagValues from '../tagValues';
import Collapse from '../collapse/index';
import './style.scss';

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
    activeTag: '';
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
const currentId = shortid();
class StepTwo extends React.PureComponent<IProps, IState> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        indexList: ['name'],
        keyList: [],
        index: '',
        activeTag: currentId,
        tags: [{
            label: '标签值1',
            value: currentId,
            valid: false,
            config: {
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
        }]
    };
    componentDidMount () {
        this.loadMainData(false);
    }
    loadMainData (isClear: boolean) {
        if (isClear) {
            // 清除一些过滤条件
        }
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
    onChangeLabel = (e) => {
        const value = e.target.value;
        const { activeTag, tags } = this.state;
        const newTags = tags.map(item => {
            if (activeTag == item.value) {
                return Object.assign(item, { label: value })
            }
            return item;
        });
        this.setState({
            tags: newTags
        })
    }
    onChangeSelect = (value, type) => {
        if (type == 'tags') {
            this.setState({
                activeTag: value
            })
        }
    }
    onChangeTags = (value) => {
        this.setState({
            tags: value
        })
    }
    onHandleChangeType = (key, type) => { // 改变节点状态
        const { activeTag, tags } = this.state;
        const newTags = tags.map(item => {
            if (activeTag == item.value) {
                const currentConf = item.config;
                this.transformNodeType(currentConf, key, type)
            }
            return item;
        });
        this.setState({
            tags: newTags
        })
    }
    transformNodeType = (treeNode, key, type) => { // 改变节点类型
        if (treeNode.key === key) {
            treeNode = Object.assign(treeNode, { type: type == '且' ? '或' : '且' });
            return;
        }
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                this.transformNodeType(children[i], key, type)
            }
        }
    }
    appendTreeNode = (treeNode: any, key: any, type: any) => { // 添加节点
        if (treeNode.key == key) {
            treeNode.children.push({});
            return
        }
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                if (children[i].key === key) {
                    if (type == 'top') {
                        children[i].children.push({})
                    }else {
                        treeNode.children.push({})
                    }
                    break;
                }
                if (children[i].children) {
                    this.appendTreeNode(children[i], key, type)
                }
            }
        }
    }
    removeTreeNode = (treeNode: any, key: any) => { // 移除节点
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                if (children[i].key === key) {
                    treeNode.children.splice(i, 1)

                    if (treeNode.children.length == 1) {
                        let newChild = treeNode.children[0];
                        delete treeNode.children;
                        delete treeNode.type;
                        treeNode = Object.assign(treeNode, newChild);
                    }
                    break;
                }
                if (children[i].children) {
                    this.removeTreeNode(children[i], key)
                }
            }
        }
    }
    onHandleDeleteCondition = (key, type) => {
        const { activeTag, tags } = this.state;
        const newTags = tags.map(item => {
            if (activeTag == item.value) {
                const currentConf = item.config;
                this.removeTreeNode(currentConf, key);
            }
            return item;
        });
        this.setState({
            tags: newTags
        })
    }
    onHandleAddCondition = (key, type) => {
        const { activeTag, tags } = this.state;
        const newTags = tags.map(item => {
            if (activeTag == item.value) {
                const currentConf = item.config;
                this.appendTreeNode(currentConf, key, type);
            }
            return item;
        });
        this.setState({
            tags: newTags
        })
    }
    renderConditionChildren = (data) => {
        return data.map((item, index) => {
            if (item.children && item.children.length) {
                return (
                    <div key={item.key} className={classnames('select_wrap', {
                        active: item.children.length > 1
                    })}>
                        {
                            this.renderConditionChildren(item.children)
                        }
                        <span className="condition" onClick={(e) => this.onHandleChangeType(item.key, item.type)}>{item.type}</span>
                    </div>
                );
            }
            return <SelectLabelRow data={item} key={item.key} extra={<div>
                <Icon type="minus-circle-o" className="icon" onClick={(e) => this.onHandleDeleteCondition(item.key, item.type)}/>
                {
                    (data.length - 1) == index && (<Icon type="plus-circle" className="icon" onClick={(e) => this.onHandleAddCondition(item.key, item.type)}/>)
                }

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
                <span className="condition" onClick={(e) => this.onHandleChangeType(data.key, data.type)}>{data.type}</span>
            </div>
        }
        return <SelectLabelRow data={data} key={data.key} extra={<div>
            <Icon type="minus-circle-o" onClick={(e) => this.onHandleDeleteCondition(data.key, data.type)} className="icon"/>
            <Icon type="plus-circle" onClick={(e) => this.onHandleAddCondition(data.key, data.type)} className="icon"/>
        </div>}/>
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
    render () {
        const { form, isShow } = this.props;
        const { indexList, activeTag, tags } = this.state;
        const { getFieldDecorator } = form;
        const currentTag = activeTag ? tags.find(item => item.value == activeTag) : '';
        const treeData = currentTag ? currentTag.config : '';
        return (
            <div className="stepTwo" style={{ display: isShow ? 'block' : 'none' }}>
                <Form.Item {...formItemLayout} label="选择实体">
                    {getFieldDecorator('entityName', {
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
                <Form.Item {...formItemLayout} label="已选标签" required>
                    <TagValues select={activeTag} value={tags} onChange={(value) => this.onChangeTags(value)} onSelect={(value) => this.onChangeSelect(value, 'tags')} />
                </Form.Item>
                {
                    currentTag && (<div className="panel_select">
                        <div className="edit_Wrap"><Input className="edit_value" value={currentTag.label} onChange={this.onChangeLabel}/><i className="iconfont iconbtn_edit"></i></div>
                        <div className="panel_wrap">
                            <div className={classnames('select_wrap', {
                                active: treeData.children && treeData.children.length > 1
                            })}>
                                {
                                    treeData && treeData.children && treeData.children.map(item => {
                                        return (<Collapse title={item.name} key={item.key} active extra={<Icon className="add_icon" onClick={(e) => this.onHandleAddCondition(item.key, 'top')} type="plus-circle" />}>
                                            {
                                                this.renderCondition(item)
                                            }
                                        </Collapse>)
                                    })
                                }
                                <span className="condition" onClick={(e) => this.onHandleChangeType(treeData.key, treeData.type)}>{treeData.type}</span>
                            </div>
                        </div>
                    </div>)
                }
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>上一步</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
