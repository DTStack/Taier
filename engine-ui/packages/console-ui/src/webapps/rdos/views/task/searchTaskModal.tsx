import * as React from 'react';
import { connect } from 'react-redux';
import { Modal, Select } from 'antd';
import { debounce } from 'lodash';

import pureRender from 'utils/pureRender';
import { getContainer } from 'funcs';

import ajax from '../../api';

import {
    workbenchActions
} from '../../store/modules//offlineTask/offlineAction';
import { showSeach } from '../../store/modules/comm';

import { MENU_TYPE } from '../../comm/const';
import { inOffline } from '../../comm';

const Option = Select.Option;

@pureRender
class SearchTaskModal extends React.Component<any, any> {
    constructor (props: any) {
        super(props);
        this.state = {
            visible: false,
            data: undefined
        };
    }
    _keyStack: any;
    componentDidMount () {
        this._keyStack = {};
        addEventListener('keydown', this.bindEvent, false)
        addEventListener('keyup', this.bindEvent, false)
    }

    componentWillUnmount () {
        this._keyStack = {};
        removeEventListener('keydown', this.bindEvent, false)
        removeEventListener('keyup', this.bindEvent, false)
    }

    bindEvent = (target: any) => {
        const keyCode = target.keyCode;
        const keyMap = this._keyStack;

        const keyP = 80; const ctrlKey = 17;

        if (keyCode === keyP || keyCode === ctrlKey) {
            keyMap[keyCode] = target.type == 'keydown';

            if (target.type != 'keydown') {
                this._keyStack = {};
                return;
            }

            if (inOffline()) {
                if (keyMap[ctrlKey] && keyMap[keyP]) {
                    target.preventDefault();
                    this.props.showSeach(true)
                    this.onfocus()
                }
            }
        }
        return false;
    }

    close = (cb?: any) => {
        this.props.showSeach(false);
        this._keyStack = {};
        this.setState({ visible: false, windowsKey: {}, data: undefined })
    }

    search = (value: any) => {
        if (!value) return;

        const succCall = (res: any) => {
            if (res.code === 1) {
                this.setState({
                    data: res.data
                });
            }
        }

        if (inOffline()) {
            ajax.searchOfflineTask({
                taskName: value
            }).then(succCall)
        }
    }

    debounceSearch = debounce(this.search, 500, { 'maxWait': 2000 })

    onSelect = (value: any, option: any) => {
        this.close();
        const taskId = option.props.data.id
        const { tabs, currentTab, openOfflineTaskTab } = this.props
        if (inOffline()) {
            openOfflineTaskTab({
                tabs,
                currentTab,
                id: taskId,
                treeType: MENU_TYPE.TASK_DEV
            })
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

    render () {
        const { data } = this.state;
        const { visibleSearchTask } = this.props;
        const options = data && data.map((item: any) =>
            <Option key={item.id} {...{ data: item }} value={item.name}>
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
                    {...{ id: "My_Search_Select" }}
                    mode="combobox"
                    showSearch
                    style={{ width: '100%' }}
                    placeholder="按任务名称搜索"
                    notFoundContent="没有发现相关任务"
                    defaultActiveFirstOption={true}
                    {...{ showArrow: false} }
                    filterOption={false}
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

export default connect((state: any) => {
    const { workbench } = state.offlineTask;
    const { tabs, currentTab } = workbench;
    return {
        tabs,
        currentTab,
        visibleSearchTask: state.visibleSearchTask,
        editor: state.editor
    }
}, (dispatch: any) => {
    const actions = workbenchActions(dispatch)
    return {
        openOfflineTaskTab: actions.openTab,
        showSeach: function (boolFlag: any) {
            dispatch(showSeach(boolFlag))
        }
    }
})(SearchTaskModal);
