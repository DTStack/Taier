import React, { PropTypes, Component } from 'react';

import { 
    Icon, 
    Tooltip
} from 'antd';
import { debounceEventHander } from 'funcs';
import SearchInput from 'widgets/search-input';


const SEARCH_INPUT_CONTROL_ID = "JS_searchTable";

class Toolbar extends Component {

    state = {
        visibleSearch: false,
    }

    onSearch = (e) => {
        const { onSearch } = this.props;
        this.setState({ visibleSearch: true }, () => {
            const input = document.getElementById(SEARCH_INPUT_CONTROL_ID)
            if (input) input.focus();
            if (onSearch) onSearch(e);
        })
    }

    render () {
        const { onSearch, onCreateDB, onRefresh, onSQLQuery } = this.props;
        const { visibleSearch } = this.state;
        return (
            <div className="toolbar txt-right">
                <Tooltip title="创建数据库">
                    <Icon type="plus-circle-o" 
                        onClick={onCreateDB}
                    />
                </Tooltip>
                <Tooltip title="SQL查询">
                    <Icon type="edit" 
                        onClick={onSQLQuery}
                    />
                </Tooltip>
                <Tooltip title="刷新">
                    <Icon type="sync" 
                        onClick={onRefresh}
                    />
                </Tooltip>
                <Tooltip title="搜索表">
                    <Icon type="search" 
                        onClick={this.onSearch}
                    />
                </Tooltip>

                <SearchInput 
                    id={SEARCH_INPUT_CONTROL_ID}
                    placeholder="输入表名搜索"
                    style={{ top: '40px', left: '15px', right: '15px' }}
                    onSearch={debounceEventHander(onSearch, 500, { 'maxWait': 2000 })}
                    display={ visibleSearch ? 'block': 'none' }
                    onClose={() => { this.setState({visibleSearch: false })}}
                />
            </div>
        )
    }
}

export default Toolbar