import * as React from 'react';
import { Menu, Icon, notification } from 'antd';
import classnames from 'classnames';
import shortid from 'shortid';
import { DragDropContext, DropTarget } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import update from 'immutability-helper'
import Card from './card';
import './style.scss';

interface IProps{
    value?: {
        label: string;
        value: string;
        tagValueId: null;
        params: any; // 规则配置
        valid: boolean; // 校验状态
    }[];
    config: any;
    onChange?: Function;
    select?: string;
    connectDropTarget?: any;
    onSelect?: Function;
}
const cardTarget = {
    drop () {}
}
@DragDropContext(HTML5Backend)
@DropTarget('tag', cardTarget, connect => ({
    connectDropTarget: connect.dropTarget()
}))
class TagValues extends React.Component<IProps, {}> {
    onHandleClick = (item: any) => {
        this.props.onSelect(item.value)
    }
    onHandleAdd = () => {
        const { value = [], config } = this.props;
        let id = shortid();
        this.props.onChange([...value, {
            label: '标签值' + (value.length + 1),
            value: id,
            valid: false,
            tagValueId: null,
            params: config
        }]);
        this.props.onSelect(id);
    }
    onHandleMenu = ({ item, key, keyPath }, data, index) => {
        const { value = [], select } = this.props;
        if (key == '0') {
            let id = shortid()
            this.props.onChange([...value, {
                label: '标签值' + (value.length + 1),
                value: id,
                tagValueId: null,
                params: data.params
            }]);
            this.props.onSelect(id);
            notification.success({
                message: '复制标签成功!',
                description: ''
            })
        } else {
            value.splice(index, 1);
            this.props.onChange(value);
            if (data.value == select) {
                this.props.onSelect('');
            }
            notification.success({
                message: '删除标签成功!',
                description: ''
            })
        }
    }
    moveCard = (id: string, atIndex: number) => {
        const { value } = this.props;
        const { card, index } = this.findCard(id);
        this.props.onChange(update(value, {
            $splice: [[index, 1], [atIndex, 0, card]]
        }))
    }
    findCard = (id: string) => {
        const { value } = this.props;
        const card = value.filter(c => `${c.value}` === id)[0]
        return {
            card,
            index: value.indexOf(card)
        }
    }
    renderMenu = (data: any, index: number) => {
        return (
            <Menu onClick={({ item, key, keyPath }) => this.onHandleMenu({ item, key, keyPath }, data, index)}>
                <Menu.Item key="0">
                    <Icon type="copy" className="tagValues_copy"/>复制
                </Menu.Item>
                <Menu.Item key="1">
                    <Icon type="delete" className="tagValues_delete"/>删除
                </Menu.Item>
            </Menu>
        )
    }
    render () {
        const { value = [], select, connectDropTarget } = this.props;
        return (
            <div>
                {
                    connectDropTarget &&
                     connectDropTarget(
                         <div className="tagValues view">
                             {
                                 value.map((item, index) => (<Card
                                     key={item.value}
                                     id={`${item.value}`}
                                     canDrag={false}
                                     moveCard={this.moveCard}
                                     findCard={this.findCard}>
                                     <div key={item.value} className={classnames('tag-item', { error: !item.valid, active: item.value == select })} onClick={ () => this.onHandleClick(item) }>
                                         <span>{item.label}</span>
                                     </div>
                                 </Card>)
                                 )
                             }
                         </div>
                     )
                }
            </div>)
    }
}
export default TagValues;
