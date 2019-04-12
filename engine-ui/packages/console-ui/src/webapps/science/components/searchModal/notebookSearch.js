import React from 'react';

import SearchModal from './index';

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
        console.log(value);
        this.close();
    }
    onChange (searchCallBack) {
        searchCallBack([{
            text: 'hh',
            value: 1
        }])
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
