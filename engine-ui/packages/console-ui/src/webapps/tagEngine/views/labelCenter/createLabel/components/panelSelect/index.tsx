import * as React from 'react';
import { Form, Input, Icon } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import classnames from 'classnames';
import SelectLabelRow from '../selectLabelRow';
import Collapse from '../collapse/index';
interface IProps extends FormComponentProps{
    treeData: any;
    currentTag: any;
    onHandleDeleteCondition: any;
    onHandleAddCondition: any;
    onHandleChangeType: any;
    onChangeLabel: any;
}

interface IState {
    visible: boolean;
}

class AreaDate extends React.PureComponent<
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
    renderConditionChildren = (data) => {
        const { getFieldDecorator } = this.props.form;
        return data.map((item, index) => {
            if (item.children && item.children.length) {
                return (
                    <div key={item.key} className={classnames('select_wrap', {
                        active: item.children.length > 1
                    })}>
                        {
                            this.renderConditionChildren(item.children)
                        }
                        <span className="condition" onClick={(e) => this.props.onHandleChangeType(item.key, item.type)}>{item.type}</span>
                    </div>
                );
            }
            return <SelectLabelRow getFieldDecorator={getFieldDecorator} data={item} key={item.key} extra={<div>
                <Icon type="minus-circle-o" className="icon" onClick={(e) => this.props.onHandleDeleteCondition(item.key)}/>
                {
                    (((data.length - 1) == index) || (item.key.split('-').length < 4)) && (<Icon type="plus-circle" className="icon" onClick={(e) => this.props.onHandleAddCondition(item.key)}/>)
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
                <span className="condition" onClick={(e) => this.props.onHandleChangeType(data.key, data.type)}>{data.type}</span>
            </div>
        }
    }
    render () {
        const { treeData, currentTag } = this.props
        return (
            <div className="panel_select">
                <div className="edit_Wrap"><Input className="edit_value" value={currentTag.label} onChange={this.props.onChangeLabel}/><i className="iconfont iconbtn_edit"></i></div>
                <div className="panel_wrap">
                    <div className={classnames('select_wrap', {
                        active: treeData.children && treeData.children.length > 1
                    })}>
                        {
                            treeData && treeData.children && treeData.children.map(item => {
                                return (<Collapse title={item.name} key={item.key} active={item.children.length} extra={<Icon className="add_icon" onClick={(e) => this.props.onHandleAddCondition(item.key)} type="plus-circle" />}>
                                    {
                                        this.renderCondition(item)
                                    }
                                </Collapse>)
                            })
                        }
                        <span className="condition" onClick={(e) => this.props.onHandleChangeType(treeData.key, treeData.type)}>{treeData.type}</span>
                    </div>
                </div>
            </div>
        );
    }
}
export default Form.create()(AreaDate)
