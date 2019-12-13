import * as React from 'react';
import { Form } from 'antd';
import { FormComponentProps } from 'antd/lib/form/Form';
import classnames from 'classnames';
import SelectLabelRow from './selectLabelRow';
import Collapse from './collapse/index';
import './style.scss';
interface IProps extends FormComponentProps{
    treeData: any;
    tagConfigData: any;
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
    renderConditionChildren = (data, entityId) => {
        const { form, tagConfigData } = this.props;
        const { getFieldDecorator } = form;
        let atomTagList = tagConfigData[entityId] ? tagConfigData[entityId] : []
        return data.map((item, index) => {
            if (item.children && item.children.length) {
                return (
                    <div key={item.key} className={classnames('select_wrap', {
                        active: item.children.length > 1
                    })}>
                        {
                            this.renderConditionChildren(item.children, entityId)
                        }
                        <span className="condition">{item.name}</span>
                    </div>
                );
            }
            return <SelectLabelRow form={form} atomTagList={atomTagList} getFieldDecorator={getFieldDecorator} data={item} key={item.key}/>
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
                <span className="condition">{data.name}</span>
            </div>
        }
    }
    render () {
        const { treeData } = this.props
        return (
            <div className="panel_select_disabled">
                <div className="panel_wrap">
                    <div className={classnames('select_wrap', {
                        active: treeData.children && treeData.children.length > 1
                    })}>
                        {
                            treeData && treeData.children && treeData.children.map(item => {
                                return (<Collapse title={item.entityName} key={item.key} active={item.children.length} extra={''}>
                                    {
                                        this.renderCondition(item, item.entityId)
                                    }
                                </Collapse>)
                            })
                        }
                        <span className="condition">{treeData.name}</span>
                    </div>
                </div>
            </div>
        );
    }
}
export default Form.create()(AreaDate)
