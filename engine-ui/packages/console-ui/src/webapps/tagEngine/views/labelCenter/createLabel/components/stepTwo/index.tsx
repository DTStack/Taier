import * as React from 'react';
import { Form, Select, Button } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import shortid from 'shortid';
import TagValues from '../tagValues';
import { cloneDeep } from 'lodash';
import './style.scss';
import PanelSelect from '../panelSelect';
import { API } from '../../../../../api/apiMap';

const { Option } = Select;

interface IProps extends FormComponentProps {
    onNext: Function;
    onPrev: Function;
    isShow: boolean;
    entityId: string|number;
}
interface IState {
    entityList: any[];
    relationList: any[];
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
    panelForm: any;
    state: IState = {
        entityList: [],
        relationList: [],
        activeTag: currentId,
        tags: [{
            id: '',
            label: '标签值1',
            value: currentId,
            valid: false,
            params: {
                key: '0',
                type: 'or', //  or|and
                name: '或',
                children: [
                    {
                        key: '0-0',
                        type: 'and',
                        name: '且',
                        entityName: '实体-用户信息',
                        children: [
                            {
                                key: '0-0-0',
                                type: 'and',
                                name: '且',
                                children: [
                                    {
                                        key: '0-0-0-0',
                                        selectName: '活跃度',
                                        selectvalue: '活跃度id',
                                        filterType: '字符串', // 字符串|数值|日期|字典
                                        conditionName: '等于',
                                        conditionId: '等于',
                                        filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值and者区间值，延续数值结构，name为空
                                    },
                                    {
                                        key: '0-0-0-1',
                                        selectName: '活跃度',
                                        selectvalue: '活跃度id',
                                        filterType: '字符串', // 字符串|数值|日期|字典
                                        conditionName: '等于',
                                        conditionId: '等于',
                                        filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值and者区间值，延续数值结构，name为空
                                    }
                                ]
                            },
                            {
                                key: '0-0-1',
                                selectName: '活跃度',
                                selectvalue: '活跃度id',
                                filterType: '字符串', // 字符串|数值|日期|字典
                                conditionName: '等于',
                                conditionId: '等于',
                                filterValue: [{ name: '休眠用户', value: '休眠用户id' }] // 若为数值and者区间值，延续数值结构，name为空
                            }
                        ]
                    },
                    {
                        key: '0-1',
                        type: 'and',
                        name: '且',
                        entityName: '实体-活动',
                        children: []
                    },
                    {
                        key: '0-2',
                        type: 'and',
                        name: '且',
                        entityName: '实体-产品',
                        children: []
                    }
                ]
            }
        }]
    };
    componentDidMount () {
        this.loadMainData();
    }
    loadMainData () {
        this.getEntityList();
        this.getRelationList();
        this.getEntityAtomTagList(null);
        this.getAtomTagList();
    }
    getEntityList = () => {
        API.selectEntity().then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    entityList: data
                });
            }
        })
    }
    getRelationList = () => { // 获取关系列表
        const { entityId } = this.props;
        API.getRelationList({
            entityId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                this.setState({
                    relationList: data
                })
            }
        })
    }
    getEntityAtomTagList = (relationId) => { // 衍生标签用来获取实体列表与其对应的原子标签列表
        const { entityId } = this.props;
        API.getEntityAtomTagList({
            entityId,
            relationId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                console.log(data);
            }
        })
    }
    getAtomTagList = () => { // 获取原子标签列表
        const { entityId } = this.props;
        API.getAtomTagList({
            entityId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                console.log(data);
            }
        })
    }
    getAtomTagValueList = () => { // 获取原子标签列表

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
    onChangeSelect = (value) => {
        this.setState({
            tags: [],
            activeTag: ''
        })
        this.getEntityAtomTagList(value);
    }
    onChangeSelectTag=(value) => {
        this.setState({
            activeTag: value
        })
    }
    onChangeTags = (value) => {
        this.setState({
            tags: value
        })
    }
    onHandleChangeType = (key, type) => { // 改变节点状态
        this.onHandleTreeNode(key, 'changeType', type)
    }
    onHandleDeleteCondition = (key) => {
        this.onHandleTreeNode(key, 'remove')
    }
    onHandleAddCondition = (key) => {
        this.onHandleTreeNode(key, 'append');
    }
    onHandleTreeNode =(key: string, op: string, type?: string) => {
        const { activeTag, tags } = this.state;
        const newTags = tags.map(item => {
            if (activeTag == item.value) {
                const currentConf = item.params;
                if (op == 'append') {
                    this.appendTreeNode(currentConf, key);
                } else if (op === 'remove') {
                    this.removeTreeNode(currentConf, key);
                } else if (op == 'changeType') {
                    this.changeNodeType(currentConf, key, type)
                }
            }
            return item;
        });
        this.setState({
            tags: cloneDeep(newTags)
        })
    }
    changeNodeType = (treeNode, key, type) => { // 改变节点类型
        if (treeNode.key === key) {
            treeNode = Object.assign(treeNode, { type: type == 'or' ? 'and' : 'or', name: type == 'or' ? '且' : '或' });
            return;
        }
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                this.changeNodeType(children[i], key, type)
            }
        }
    }
    appendTreeNode = (treeNode: any, key: any) => { // 添加节点
        let level = key.split('-');
        if (treeNode.key == key) {
            let newKey = key + '-' + shortid();
            treeNode.children.push({ key: newKey });
            return
        }
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                if (children[i].key === key) {
                    if (level.length == 2) { // 二级目录
                        let newKey = key + '-' + shortid();
                        children[i].children.push({ key: newKey })
                    } else if (level.length == 3) { // 三级目录
                        let current = children[i]; // 转换节点数据结构，如果为三级目录则，改变数据结构，变为children数组关系
                        children[i] = { key: current.key, type: 'and', name: '且', children: [Object.assign({}, current, { key: current.key + '-' + shortid() }), Object.assign({}, current, { key: current.key + '-1' })] };
                    } else { // 四级目录
                        let newKey = key + '-' + shortid();
                        treeNode.children.push({ key: newKey })
                    }
                    break;
                }
                if (children[i].children) {
                    this.appendTreeNode(children[i], key)
                }
            }
        }
    }
    removeTreeNode = (treeNode: any, key: any) => { // 移除节点
        let level = key.split('-');
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                if (children[i].key === key) {
                    treeNode.children.splice(i, 1)
                    if (level.length > 3 && treeNode.children.length == 1) {
                        let newChild = treeNode.children[0];
                        delete treeNode.children;
                        delete treeNode.type;
                        treeNode = Object.assign(treeNode, newChild, { key: treeNode.key });
                    }
                    break;
                }
                if (children[i].children) {
                    this.removeTreeNode(children[i], key)
                }
            }
        }
    }
    onHandleNext = (e: any) => {
        // const { tags } = this.state;
        this.panelForm.validateFields((err, values) => {
            if (!err) {
                console.log(values);
            }
        });
        // this.props.form.validateFields((err, values) => {
        //     if (!err) {
        //         this.props.onNext(Object.assign({}, values, { tags }));
        //     }
        // });
    }
    onHandlePrev = () => {
        this.props.onPrev();
    }
    render () {
        const { form, isShow, entityId } = this.props;
        const { entityList, activeTag, tags, relationList } = this.state;
        const { getFieldDecorator } = form;
        const currentTag = activeTag ? tags.find(item => item.value == activeTag) : '';
        const treeData = currentTag ? currentTag.params : '';
        return (
            <div className="stepTwo" style={{ display: isShow ? 'block' : 'none' }}>
                <Form.Item {...formItemLayout} label="选择实体">
                    {getFieldDecorator('entityId', {
                        initialValue: entityId,
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
                <Form.Item {...formItemLayout} label="选择关系">
                    {getFieldDecorator('relationId', {
                        rules: [
                            {
                                message: '请选择关系'
                            }
                        ]
                    })(
                        <Select placeholder="请选择关系" allowClear showSearch onChange={this.onChangeSelect} style={{ width: '100%' }}>
                            {
                                relationList.map(item => <Option value={item.relId} key={item.relId}>{item.relName}</Option>)
                            }
                        </Select>
                    )}
                </Form.Item>
                <Form.Item {...formItemLayout} label="已选标签" required>
                    <TagValues select={activeTag} value={tags} onChange={(value) => this.onChangeTags(value)} onSelect={this.onChangeSelectTag} />
                </Form.Item>
                {
                    currentTag && (<PanelSelect ref={(node) => this.panelForm = node} treeData={treeData} currentTag={currentTag} onChangeLabel={this.onChangeLabel} onHandleAddCondition={this.onHandleAddCondition} onHandleChangeType={this.onHandleChangeType} onHandleDeleteCondition={this.onHandleDeleteCondition}/>)
                }
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>上一步</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
