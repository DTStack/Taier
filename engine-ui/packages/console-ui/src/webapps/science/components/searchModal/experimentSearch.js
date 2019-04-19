import React from 'react';
import { bindActionCreators } from 'redux';
import { connect } from 'react-redux';

import SearchModal from './index';

import API from '../../api/experiment'
import * as experimentActions from '../../actions/experimentActions'

@connect(null, dispatch => {
    return {
        ...bindActionCreators(experimentActions, dispatch)
    }
})
class ExperimentSearch extends React.Component {
    state = {
        key: Math.random()
    }
    onSelect (value) {
        this.props.openExperiment(value);
        this.close();
    }
    async onChange (value, searchCallBack) {
        searchCallBack([]);
        let res = await API.searchGlobal({
            value
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
    close () {
        this.setState({
            key: Math.random()
        })
        this.props.onCancel();
    }
    render () {
        const { visible } = this.props;
        return (
            <SearchModal
                key={this.state.key}
                visible={visible}
                onCancel={this.close.bind(this)}
                onSelect={this.onSelect.bind(this)}
                onChange={this.onChange.bind(this)}
                title='搜索并打开实验'
            />
        )
    }
}
export default ExperimentSearch;
