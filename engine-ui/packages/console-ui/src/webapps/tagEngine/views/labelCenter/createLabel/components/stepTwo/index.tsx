import * as React from 'react';
import { Form, Select, Button, notification } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import shortid from 'shortid';
import TagValues from '../tagValues';
import { cloneDeep } from 'lodash';
import PanelSelect from '../panelSelect';
import TagTypeOption from '../../../../../consts/tagTypeOption';
import { API } from '../../../../../api/apiMap';
import './style.scss';

const { Option } = Select;

interface IProps extends FormComponentProps {
    onNext: Function;
    onPrev: Function;
    isShow: boolean;
    entityId: string|number;
    data: any;
    tagId: string;
}
interface IState {
    entityList: any[];
    relationList: any[];
    atomTagList: any[];
    activeTag: '';
    tags: any[];
    initConfig: any;
    initRowValue: any;
    tagConfigData: any;
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
        atomTagList: [],
        activeTag: currentId,
        initConfig: {},
        initRowValue: {
            'dataType': '',
            'entityAttr': '',
            'lValue': '',
            'rValue': '',
            'tagId': '',
            'timeType': '',
            'type': '',
            'value': '',
            'values': []
        },
        tagConfigData: {},
        tags: [{
            tagValueId: null,
            label: '标签值1',
            value: currentId,
            valid: true,
            params: {}
        }]
    };
    componentDidMount () {
        this.loadMainData();
    }
    loadMainData () {
        this.getEntityList();
        this.getRelationList();
        const { tagId } = this.props;
        !tagId && this.getEntityAtomTagList(null);
    }
    componentDidUpdate (preProps) {
        const { data } = this.props;
        if (data != preProps.data) {
            const { relationId, tags } = data;
            this.props.form.setFieldsValue({ relationId });
            let newtags = tags.map(item => {
                return {
                    tagValueId: item.tagValueId,
                    label: item.tagValue,
                    value: shortid(),
                    valid: true,
                    params: JSON.parse(item.param)
                }
            });
            if (newtags && newtags.length) {
                let { params } = newtags[0];
                const { children = [] } = params;
                children.forEach(item => {
                    this.getAtomTagList(item.entityId)
                })
            }
            this.setState({
                tags: newtags,
                activeTag: newtags.length ? newtags[0].value : ''
            })
        }
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
        const { tags } = this.state;
        API.getEntityAtomTagList({
            entityId,
            relationId
        }).then(res => {
            const { code, data = [] } = res;
            if (code === 1) {
                let params = {
                    key: '0',
                    type: 'OP_AND', //  OP_OR|OP_AND
                    name: '且',
                    children: data.map((item, index) => Object.assign({}, item, {
                        key: '0-' + index,
                        type: 'OP_AND',
                        name: '且',
                        children: []
                    }))
                }
                data.forEach(item => {
                    this.getAtomTagList(item.entityId);
                })
                this.setState({
                    initConfig: params,
                    tags: tags.map(item => {
                        return Object.assign(item, { params })
                    })
                })
            }
        })
    }
    getAtomTagList = (entityId) => { // 获取原子标签列表
        const { initRowValue, tagConfigData } = this.state;
        API.getAtomTagList({
            entityId
        }).then(res => {
            const { code, data } = res;
            if (code === 1) {
                if (data && data.length) {
                    let { dataType, entityAttr, tagId } = data[0];
                    let params = {
                        atomTagList: data,
                        initRowValue: Object.assign({}, initRowValue, { dataType, entityAttr, tagId, type: TagTypeOption[dataType][0].value })
                    }
                    tagConfigData[entityId] = params;
                    this.setState({
                        tagConfigData: tagConfigData
                    })
                }
            }
        })
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
        let currentId = shortid()
        this.setState({
            tags: [{
                tagValueId: null,
                label: '标签值1',
                value: currentId,
                valid: true,
                params: {}
            }],
            activeTag: currentId,
            initConfig: {}
        }, () => {
            this.getEntityAtomTagList(value);
        });
    }
    onChangeSelectTag=(value) => { // 选择标签节点
        this.setState({
            activeTag: value
        })
    }
    onChangeTags = (value) => {
        this.setState({
            tags: value
        })
    }
    onHandleChangeNode = (key, node) => { // 改变节点值
        this.onHandleTreeNode(key, 'changeNode', node)
    }
    onHandleChangeType = (key, type) => { // 改变节点状态
        this.onHandleTreeNode(key, 'changeNode', { type: type == 'OP_OR' ? 'OP_AND' : 'OP_OR', name: type == 'OP_OR' ? '且' : '或' })
    }
    onHandleDeleteCondition = (key) => {
        this.onHandleTreeNode(key, 'remove')
    }
    onHandleAddCondition = (key, entityId) => {
        const { tagConfigData } = this.state;
        const { atomTagList, initRowValue } = tagConfigData[entityId]
        if (atomTagList && atomTagList.length) {
            this.onHandleTreeNode(key, 'append', {}, initRowValue);
        } else {
            notification.warning({
                message: '原子标签',
                description: '无原子标签，请先创建原子标签！'
            });
        }
    }
    onHandleTreeNode =(key: string, op: string, node?: any, initRowValue?: any) => {
        const { activeTag, tags } = this.state;
        const newTags = tags.map(item => {
            if (activeTag == item.value) {
                const currentConf = item.params;
                if (op == 'append') {
                    this.appendTreeNode(currentConf, key, initRowValue);
                } else if (op === 'remove') {
                    this.removeTreeNode(currentConf, key);
                } else if (op == 'changeNode') {
                    this.changeNode(currentConf, key, node)
                }
            }
            return item;
        });
        this.setState({
            tags: cloneDeep(newTags)
        })
    }
    changeNode = (treeNode, key, node) => { // 改变节点
        if (treeNode.key === key) {
            treeNode = Object.assign(treeNode, node);
            return;
        }
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                this.changeNode(children[i], key, node)
            }
        }
    }
    appendTreeNode = (treeNode: any, key: any, initRowValue: any) => { // 添加节点
        let level = key.split('-');
        if (treeNode.key == key) {
            let newKey = key + '-' + shortid();
            treeNode.children.push(Object.assign({}, initRowValue, { key: newKey }));
            return
        }
        if (treeNode.children) {
            const children = treeNode.children
            for (let i = 0; i < children.length; i += 1) {
                if (children[i].key === key) {
                    if (level.length == 2) { // 二级目录
                        let newKey = key + '-' + shortid();
                        children[i].children.push(Object.assign({}, initRowValue, { key: newKey }))
                    } else if (level.length == 3) { // 三级目录
                        let current = children[i]; // 转换节点数据结构，如果为三级目录则，改变数据结构，变为children数组关系
                        children[i] = { key: current.key, type: 'OP_AND', name: '且', children: [Object.assign({}, current, { key: current.key + '-' + shortid() }), Object.assign({}, current, initRowValue, { key: current.key + '-1' })] };
                    } else { // 四级目录
                        let newKey = key + '-' + shortid();
                        treeNode.children.push(Object.assign({}, initRowValue, { key: newKey }))
                    }
                    break;
                }
                if (children[i].children) {
                    this.appendTreeNode(children[i], key, initRowValue)
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
    onValidateLabelRule = () => {
        const { tags, activeTag } = this.state;
        const that = this;
        this.panelForm.validateFields((err, values) => {
            if (err) {
                let current = tags.find(item => item.value == activeTag);
                notification.warning({
                    message: current.label,
                    description: '请填写有效标签规则'
                });
            }
            let isRepeat = [];
            for (let i = 0; i < tags.length; i++) {
                if (isRepeat.includes(tags[i].label)) {
                    notification.error({
                        message: tags[i].label,
                        description: '标签名称重复，请检查！'
                    });
                    return false;
                }
                if (tags[i].params == this.state.initConfig) {
                    notification.error({
                        message: tags[i].label,
                        description: '请填写标签！'
                    });
                    return false;
                }
                isRepeat.push(tags[i].label);
            }
            let newTag = tags.map(item => Object.assign(item, { valid: !err }));
            that.setState({
                tags: newTag
            })
        });
    }
    onHandleNext = (e: any) => {
        const { tags, activeTag } = this.state;
        const that = this;
        if (!tags || tags.length < 1) {
            notification.error({
                message: '标签值',
                description: '请添加标签值'
            });
            return false;
        }
        let isRepeat = [];
        for (let i = 0; i < tags.length; i++) {
            if (isRepeat.includes(tags[i].label)) {
                notification.error({
                    message: tags[i].label,
                    description: '标签名称重复，请检查！'
                });
                return false;
            }
            if (tags[i].params == this.state.initConfig) {
                notification.error({
                    message: tags[i].label,
                    description: '请添加标签规则！'
                });
                return false;
            }
            isRepeat.push(tags[i].label);
        }
        this.panelForm.validateFields((err, values) => {
            if (err) {
                let current = tags.find(item => item.value == activeTag);
                notification.error({
                    message: current.label,
                    description: '请填写有效标签规则'
                });
            } else {
                that.props.form.validateFields((err, values) => {
                    if (!err) {
                        that.props.onNext(Object.assign({}, values, { tags }));
                    }
                });
            }
        });
    }
    onHandlePrev = () => {
        this.props.onPrev();
    }
    render () {
        const { form, isShow, entityId } = this.props;
        const { entityList, activeTag, tags, relationList, tagConfigData, initConfig } = this.state;
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
                    <TagValues config={initConfig} select={activeTag} value={tags} onChange={(value) => this.onChangeTags(value)} onSelect={this.onChangeSelectTag} />
                </Form.Item>
                {
                    currentTag && (<PanelSelect tagConfigData={tagConfigData} ref={(node) => this.panelForm = node} treeData={treeData} currentTag={currentTag} onChangeLabel={this.onChangeLabel} onChangeNode={this.onHandleChangeNode} onHandleAddCondition={this.onHandleAddCondition} onHandleChangeType={this.onHandleChangeType} onHandleDeleteCondition={this.onHandleDeleteCondition}/>)
                }
                <div className="wrap_btn_content"><Button onClick={this.onHandlePrev}>上一步</Button><Button type="primary" onClick={this.onHandleNext}>下一步</Button></div>
            </div>
        );
    }
}

export default Form.create()(StepTwo);
