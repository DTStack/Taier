import React from 'react';
import ajax from '../../../../api';
import { debounceEventHander } from '../../../../comm';
import {
    message
} from 'antd';

class TaskSelector extends React.Component {
    constructor (props) {
        super(props);
        this.searchVOS = this.fetchVOS.bind(this);
        this.handleClick = this.handleClick.bind(this);
        this.selectVOS = this.props.onSelect;
        this.taskId = this.props.taskId;

        this.state = { list: [], emptyError: false };
    }

    fetchVOS (evt) {
        const value = evt.target.value;
        if (value.trim() === '') {
            this.setState({
                list: []
            });
            this.resetError();
            return;
        }

        ajax.getOfflineTaskByName({
            name: value,
            taskId: this.taskId
        }).then(res => {
            if (res.code === 1) {
                res.data.length === 0 && this.setState({
                    emptyError: true
                })
                // res.data.length === 0 && message.warning('没有符合条件的任务');
                this.setState({
                    list: res.data,
                    fetching: false
                });
            }
        })
    }

    handleClick (task) {
        ajax.checkIsLoop({
            taskId: this.taskId,
            dependencyTaskId: task.id
        })
            .then(res => {
                if (res.code === 1) {
                    if (res.data) message.error(`添加失败，该任务循环依赖任务${res.data.name || ''}!`)
                    else {
                        this.selectVOS(task);
                        this.$input.value = '';
                        this.setState({
                            list: []
                        });
                    }
                }
            })
    }
    resetError () {
        this.setState({
            emptyError: false
        })
    }
    render () {
        const { list, emptyError } = this.state;
        const emptyErrorStyle = {
            position: 'absolute',
            width: '100%',
            bottom: '-28px',
            left: '0px',
            color: 'red'
        }

        return <div className="m-taskselector">
            <input
                onInput={(event) => {
                    this.resetError();
                    debounceEventHander(this.searchVOS, 500, { 'maxWait': 2000 })(event);
                }}
                ref={el => this.$input = el}
                className="ant-input"
                placeholder="根据任务名称搜索"
            />
            {emptyError && <span style={emptyErrorStyle}>没有符合条件的任务</span>}
            {list.length > 0 && <ul className="tasklist">
                {list.map(o => <li className="taskitem"
                    onClick={() => { this.handleClick(o) }}
                    key={o.id}
                >
                    {o.name}
                </li>)}
            </ul>}
        </div>
    }
}
export default TaskSelector;
