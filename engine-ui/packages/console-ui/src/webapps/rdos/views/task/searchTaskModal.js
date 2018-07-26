import React from 'react';
import { connect } from 'react-redux';
import { Modal, Select } from 'antd';
import { debounce } from 'lodash';

import pureRender from 'utils/pureRender';

import ajax from '../../api';

import { 
    workbenchActions
} from '../../store/modules//offlineTask/offlineAction';
import { showSeach } from '../../store/modules/comm';

import { openPage } from '../../store/modules//realtimeTask/browser';
import { MENU_TYPE } from '../../comm/const';
import { inOffline, inRealtime } from '../../comm';

const Option = Select.Option;

@pureRender
class SearchTaskModal extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            keyStack: {},
            visible: false,
            data: undefined
        };
    }

    componentDidMount() {
        addEventListener('keydown', this.bindEvent, false)
        addEventListener('keyup', this.bindEvent, false)
    }

    componentWillUnmount() {
        removeEventListener('keydown', this.bindEvent, false)
        removeEventListener('keyup', this.bindEvent, false)
        this.setState({  keyStack: {} })
    }

    bindEvent = (target) => {

        const keyCode = target.keyCode
        const keyMap = this.state.keyStack

        const keyP = 80, ctrlKey = 17;

        if (keyCode === keyP || keyCode === ctrlKey) {

            const currentKeyMap = Object.assign(keyMap, {
                [keyCode]: target.type == 'keydown',
            })

            this.setState({ keyStack: currentKeyMap })

            if (target.type != 'keydown') {
                return;
            }

            if (inRealtime() || inOffline()) {

                if (currentKeyMap[ctrlKey] && currentKeyMap[keyP]) {
                    target.preventDefault();
                    this.props.showSeach(true)
                    this.onfocus()
                }
            }
        }
        return false;
    }

    close = (cb) => {
        this.props.showSeach(false)
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

        if (inOffline()) {
            ajax.searchOfflineTask({
                taskName: value,
            }).then(succCall)

        } else if (inRealtime()) {
            ajax.searchRealtimeTask({
                taskName: value,
            }).then(succCall)
        }
    }

    debounceSearch = debounce(this.search, 500, { 'maxWait': 2000 })

    onSelect = (value, option) => {
        this.close();
        const taskId = option.props.data.id
        if (inOffline()) {
            const { tabs, currentTab, openOfflineTaskTab } = this.props
            openOfflineTaskTab({
                tabs,
                currentTab,
                id: taskId,
                treeType: MENU_TYPE.TASK_DEV,
            })
        } else if(inRealtime()) {
            const { openRealtimeTaskTab } = this.props
            openRealtimeTaskTab({id: taskId })
        }
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
        const { data, visible } = this.state;
        const { visibleSearchTask } = this.props;
        const options = data && data.map(item => 
            <Option key={item.id} data={item} value={item.name}>
                {item.name}
            </Option>
        )

        return <Modal
            title="打开任务"
            visible={visibleSearchTask}
            footer=""
            onCancel={this.close}
        >
            <Select
                id="My_Search_Select"
                mode="combobox"
                showSearch
                style={{width: '100%'}}
                placeholder="按任务名称搜索"
                notFoundContent="没有发现相关任务"
                defaultActiveFirstOption={true}
                showArrow={false}
                filterOption={false}
                autoComplete="off"
                onChange={this.debounceSearch}
                onSelect={this.onSelect}
            >
                {options}
            </Select>
        </Modal>
    }
}

export default connect(state => {
    const { workbench } = state.offlineTask;
    const { tabs, currentTab } = workbench;
    return { tabs, currentTab, visibleSearchTask: state.visibleSearchTask }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        openOfflineTaskTab: actions.openTab,
        openRealtimeTaskTab: function(params) {
            dispatch(openPage(params))
        },
        showSeach: function(boolFlag) {
            dispatch(showSeach(boolFlag))
        }
    }
})(SearchTaskModal);