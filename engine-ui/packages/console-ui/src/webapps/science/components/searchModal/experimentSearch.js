import React from 'react';

import SearchModal from './index';

class ExperimentSearch extends React.Component {
    state = {
        key: Math.random()
    }
    onSelect (value) {
        console.log(value);
        this.close();
    }
    onChange (searchCallBack) {
        searchCallBack([{
            text: 'hh2',
            value: 1
        }, {
            text: 'hh3',
            value: 2
        }, {
            text: 'hh4',
            value: 3
        }])
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
