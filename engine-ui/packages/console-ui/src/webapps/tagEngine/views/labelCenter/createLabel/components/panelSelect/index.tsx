import * as React from 'react';
import { Form, Input, Icon } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import classnames from 'classnames';
import SelectLabelRow from './selectLabelRow';
import Collapse from './collapse/index';
import './style.scss';
interface IProps extends FormComponentProps{
    treeData: any;
    currentTag: any;
    onHandleDeleteCondition: any;
    onHandleAddCondition: any;
    onHandleChangeType: any;
    onChangeNode: any;
    onChangeLabel: any;
    tagConfigData: any;
}

interface IState {
    visible: boolean;
}

class PanelSelect extends React.PureComponent<
IProps,
IState
> {
    constructor (props: IProps) {
        super(props);
    }

    state: IState = {
        visible: false
    };
    componentDidMount () { }
    renderConditionChildren = (data, entityId) => {
        const { tagConfigData, form, onChangeNode } = this.props;
        let atomTagList = [];
        if (tagConfigData[entityId]) {
            atomTagList = tagConfigData[entityId].atomTagList
        }

        return data.map((item, index) => {
            if (item.children && item.children.length) {
                return (
                    <div key={item.key} className={classnames('select_wrap', {
                        active: item.children.length > 1
                    })}>
                        {
                            this.renderConditionChildren(item.children, entityId)
                        }
                        <span className="condition" onClick={(e) => this.props.onHandleChangeType(item.key, item.type)}>{item.name}</span>
                    </div>
                );
            }
            return <SelectLabelRow onChangeNode={onChangeNode} form={form} atomTagList={atomTagList} data={item} key={item.key} extra={<div>
                <Icon type="minus-circle-o" className="icon" onClick={(e) => this.props.onHandleDeleteCondition(item.key)}/>
                {
                    (((data.length - 1) == index) || (item.key.split('-').length < 4)) && (<Icon type="plus-circle" className="icon" onClick={(e) => this.props.onHandleAddCondition(item.key, entityId)}/>)
                }

            </div>}/>
        });
    }
    renderCondition = (data, entityId) => {
        if (data.children && data.children.length) {
            return <div className={classnames('select_wrap', {
                active: data.children.length > 1
            })}>
                {
                    this.renderConditionChildren(data.children, entityId)
                }
                <span className="condition" onClick={(e) => this.props.onHandleChangeType(data.key, data.type)}>{data.name}</span>
            </div>
        }
    }
    render () {
        const { treeData, currentTag, form } = this.props;
        const { getFieldDecorator } = form;
        return (
            <div className="panel_select">
                <div className="edit_Wrap">
                    <Form.Item>
                        {
                            getFieldDecorator('labelName', {
                                initialValue: currentTag.label,
                                rules: [
                                    {
                                        required: true,
                                        max: 80,
                                        pattern: /^[\u4E00-\u9FA5A-Za-z0-9_]+$/,
                                        message: '姓名只能包括汉字，字母、下划线、数字'
                                    }
                                ]
                            })(<Input className="edit_value" onChange={this.props.onChangeLabel}/>)
                        }
                    </Form.Item>
                    <i className="iconfont iconbtn_edit"></i>
                </div>
                <div className="panel_wrap">
                    <div className={classnames('select_wrap', {
                        active: treeData.children && treeData.children.length > 1
                    })}>
                        {
                            treeData && treeData.children && treeData.children.map(item => {
                                return (<Collapse title={item.entityName} key={item.key} active={item.children.length} extra={<Icon className="add_icon" onClick={(e) => this.props.onHandleAddCondition(item.key, item.entityId)} type="plus-circle" />}>
                                    {
                                        this.renderCondition(item, item.entityId)
                                    }
                                </Collapse>)
                            })
                        }
                        <span className="condition" onClick={(e) => this.props.onHandleChangeType(treeData.key, treeData.type)}>{treeData.name}</span>
                    </div>
                </div>
            </div>
        );
    }
}
export default Form.create()(PanelSelect)
