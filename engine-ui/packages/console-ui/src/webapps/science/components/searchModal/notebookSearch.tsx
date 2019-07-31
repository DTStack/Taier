import * as React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import SearchModal from './index';

import API from '../../api/notebook'
import * as notebookActions from '../../actions/notebookActions'

@(connect(null, (dispatch: any) as any) => {
    return {
        ...bindActionCreators(notebookActions, dispatch)
    }
})
class NotebookSearch extends React.Component<any, any> {
    state: any = {
        key: Math.random()
    }
    close () {
        this.setState({
            key: Math.random()
        })
        this.props.onCancel();
    }
    onSelect(value: any) {
        this.props.openNotebook(value);
        this.close();
    }
    async onChange (value: any, searchCallBack: any) {
        searchCallBack([]);
        let res = await API.searchGlobal({
            taskName: value
        })
        if (res && res.code == 1 && res.data) {
            searchCallBack(res.data.map((item: any) => {
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
