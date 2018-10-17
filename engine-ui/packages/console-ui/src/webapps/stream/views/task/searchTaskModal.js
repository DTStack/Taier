import React from 'react';
import { connect } from 'react-redux';
import { Modal, Select } from 'antd';
import { debounce } from 'lodash';

import pureRender from 'utils/pureRender';
import { getContainer } from 'funcs';

import ajax from '../../api';

import { showSeach } from '../../store/modules/comm';

import { openPage } from '../../store/modules//realtimeTask/browser';
import { MENU_TYPE } from '../../comm/const';
import { inRealtime } from '../../comm';

const Option = Select.Option;

@pureRender
class SearchTaskModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            visible: false,
            data: undefined
        };
    }

    componentDidMount() {
        this._keyStack = {};
        addEventListener('keydown', this.bindEvent, false)
        addEventListener('keyup', this.bindEvent, false)
    }

    componentWillUnmount() {
        this._keyStack = {};
        removeEventListener('keydown', this.bindEvent, false)
        removeEventListener('keyup', this.bindEvent, false)
    }

    bindEvent = (target) => {

        const keyCode = target.keyCode;
        const keyMap = this._keyStack;

        const keyP = 80, ctrlKey = 17;

        if (keyCode === keyP || keyCode === ctrlKey) {

            keyMap[keyCode] = target.type == 'keydown';

            if (target.type != 'keydown') {
                this._keyStack = {};
                return;
            }

            if (inRealtime()) {

                if (keyMap[ctrlKey] && keyMap[keyP]) {
                    target.preventDefault();
                    this.props.showSeach(true)
                    this.onfocus()
                }
            }
        }
        return false;
    }

    close = (cb) => {
        this.props.showSeach(false);
        this._keyStack = {};
        this.setState({ visible: false, windowsKey: {}, data: undefined })
    }

    search = (value) => {
        if (!value) return;

        const succCall = (res) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data,
                });
            }
        }

       if (inRealtime()) {
            ajax.searchRealtimeTask({
                taskName: value,
            }).then(succCall)
        }
    }

    debounceSearch = debounce(this.search, 500, { 'maxWait': 2000 })

    onSelect = (value, option) => {
        this.close();
        const taskId = option.props.data.id
        const { openRealtimeTaskTab } = this.props
        openRealtimeTaskTab({ id: taskId })

    }

    onfocus = () => {
        const selector = document.getElementById('My_Search_Select')
        if (selector) {
            selector.focus();
            // fix autoComplete问题
            selector.setAttribute('autocomplete', 'off');
        }
    }

    render() {
        const { data } = this.state;
        const { visibleSearchTask, editor } = this.props;
        const options = data && data.map(item =>
            <Option key={item.id} data={item} value={item.name}>
                {item.name}
            </Option>
        )

        return <div id="JS_search_task">
            <Modal
                title="搜索并打开任务"
                visible={visibleSearchTask}
                footer=""
                onCancel={this.close}
                getContainer={() => getContainer('JS_search_task')}
            >
                <Select
                    id="My_Search_Select"
                    mode="combobox"
                    showSearch
                    style={{ width: '100%' }}
                    placeholder="按任务名称搜索"
                    notFoundContent="没有发现相关任务"
                    defaultActiveFirstOption={true}
                    showArrow={false}
                    filterOption={false}
                    autoComplete="off"
                    onChange={this.debounceSearch}
                    onSelect={this.onSelect}
                    getPopupContainer={() => getContainer('JS_search_task')}
                >
                    {options}
                </Select>
            </Modal>
        </div>
    }
}

export default connect(state => {
    return {
        visibleSearchTask: state.visibleSearchTask,
        editor: state.editor
    }
}, dispatch => {
    return {
        openRealtimeTaskTab: function (params) {
            dispatch(openPage(params))
        },
        showSeach: function (boolFlag) {
            dispatch(showSeach(boolFlag))
        }
    }
})(SearchTaskModal);