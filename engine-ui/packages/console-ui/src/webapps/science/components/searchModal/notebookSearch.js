import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import SearchModal from './index';

import API from '../../api/notebook'
import * as notebookActions from '../../actions/notebookActions'

@connect(null, dispatch => {
    return {
        ...bindActionCreators(notebookActions, dispatch)
    }
})
class NotebookSearch extends React.Component {
    state = {
        key: Math.random()
    }
    close () {
        this.setState({
            key: Math.random()
        })
        this.props.onCancel();
    }
    onSelect (value) {
        this.props.openNotebook(value);
        this.close();
    }
    async onChange (value, searchCallBack) {
        searchCallBack([]);
        let res = await API.searchGlobal({
            taskName: value
        })
        if (res && res.code == 1 && res.data) {
            searchCallBack(res.data.map((item) => {
                return {
                    ...item,
                    text: item.name,
                    value: item.id
                }
            }))
        }
    }
    render () {
        const { key } = this.state;
        const { visible } = this.props;
        return (
            <SearchModal
                visible={visible}
                key={key}
                onCancel={this.close.bind(this)}
                onSelect={this.onSelect.bind(this)}
                onChange={this.onChange.bind(this)}
                title='搜索并打开 notebook'
            />
        )
    }
}
export default NotebookSearch;
