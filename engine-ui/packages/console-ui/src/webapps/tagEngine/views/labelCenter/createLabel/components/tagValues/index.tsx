import * as React from 'react';
import { Dropdown, Menu, Icon, notification } from 'antd';
import classnames from 'classnames';
import shortid from 'shortid';
import { DragDropContext, DropTarget } from 'react-dnd';
import HTML5Backend from 'react-dnd-html5-backend';
import Card from './card';
import './style.scss';

interface IProps{
    value?: {
        label: string;
        value: string;
        config: any[];
    }[];
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
        const { value = [] } = this.props;
        this.props.onChange([...value, {
            label: '标签值' + (value.length + 1),
            value: shortid(),
            config: []
        }])
    }
    onHandleMenu = ({ item, key, keyPath }, data, index) => {
        const { value = [] } = this.props;
        if (key == '0') {
            this.props.onChange([...value, {
                label: '标签值' + (value.length + 1),
                value: shortid(),
                config: data.config
            }]);
            notification.success({
                message: '复制标签成功!',
                description: ''
            })
        } else {
            value.splice(index, 1);
            this.props.onChange(value);
            notification.success({
                message: '删除标签成功!',
                description: ''
            })
        }
    }
    moveCard = (id: string, atIndex: number) => {
        const { value } = this.props;
        const { card, index } = this.findCard(id)
        console.log(card, index)
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
                    复制
                </Menu.Item>
                <Menu.Item key="1">
                    删除
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
                         <div className="tagValues">
                             {
                                 value.map((item, index) => (<Card
                                     key={item.value}
                                     id={`${item.value}`}
                                     moveCard={this.moveCard}
                                     findCard={this.findCard}>
                                     <div key={item.value} onClick={ () => this.onHandleClick(item) } className={classnames('tag-item', { active: item.value == select })}>
                                         <span>{item.label}</span>
                                         <Dropdown overlay={this.renderMenu(item, index)} placement="bottomLeft">
                                             <i className='iconfont iconmenu-pl'></i>
                                         </Dropdown>
                                     </div>
                                 </Card>)
                                 )
                             }
                             <div><Icon type="plus-circle-o" className="plus-circle" onClick={this.onHandleAdd} /></div>
                         </div>
                     )
                }
            </div>)
    }
}
export default TagValues;
