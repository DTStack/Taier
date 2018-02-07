import React from 'react';
import { connect } from 'react-redux';
import { Modal, Button, Select } from 'antd';
import { debounce } from 'lodash';

import utils from 'utils';
import pureRender from 'utils/pureRender';

import ajax from '../../api';

import { modalAction } from '../../store/modules/offlineTask/actionType';
import { 
    workbenchActions
} from '../../store/modules//offlineTask/offlineAction';
import { openPage } from '../../store/modules//realtimeTask/browser';
import { MENU_TYPE } from '../../comm/const';

const Option = Select.Option;

function inOffline() {
    return location.pathname.indexOf('offline') > -1;
}

function inRealtime() {
    return location.pathname.indexOf('realtime') > -1;
}

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

    componentWillReceiveProps(nextProps) {
        const key = nextProps.myKey
        const oldKey = this.props.myKey
        if (key !== oldKey) {
            this.setState({ visible: true }, () => {
                this.onfocus()
            })
        }
    }

    componentWillUnmount() {
        removeEventListener('keydown', this.bindEvent, false)
        removeEventListener('keyup', this.bindEvent, false)
    }

    bindEvent = (target) => {
        const pathname = location.pathname
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
                    this.setState({
                        visible: true,
                    })
                    this.onfocus()
                }
                
            }
        
        }
        return false;
    }

    close = (cb) => {
        this.setState({ visible: false, windowsKey: {}, data: undefined })
    }

    search = (value) => {
        if (!value) return;

        const pathname = location.pathname
        
        const succCall = (res) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data
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
                treeType: MENU_TYPE.TASK_DEV
            })
        } else if(inRealtime()) {
            const { openRealtimeTaskTab } = this.props
            openRealtimeTaskTab({id: taskId })
        }
    }

    onfocus = () => {
        const selector = document.getElementById('My_Search_Select')
        if (selector) {
            selector.focus()
            // fix autoComplete问题
            selector.setAttribute('autocomplete', 'off')
        }
    }

    render() {
        const { data, visible } = this.state;
        const options = data && data.map(item => 
            <Option key={item.id} data={item} value={item.name}>
                {item.name}
            </Option>
        )

        return <Modal
            title="搜索任务"
            visible={visible}
            footer=""
            onCancel={this.close}
        >
            <Select
                id="My_Search_Select"
                mode="combobox"
                showSearch
                style={{width: '100%'}}
                placeholder="输入所要搜索的任务名称"
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
    return { tabs, currentTab, myKey: state.visibleSearchTask }
}, dispatch => {
    const actions = workbenchActions(dispatch)
    return {
        openOfflineTaskTab: actions.openTab,
        openRealtimeTaskTab: function(params) {
            dispatch(openPage(params))
        }
    }
})(SearchTaskModal);