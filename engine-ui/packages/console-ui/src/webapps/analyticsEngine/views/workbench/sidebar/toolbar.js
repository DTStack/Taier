import React, { PropTypes, Component } from 'react';

import { 
    Icon, 
    Tooltip
} from 'antd';

class Toolbar extends Component {

    render () {
        const { onCreateDB, onRefresh, onSQLQuery, onCreateTable } = this.props;
        return (
            <div className="toolbar txt-right">
                <Tooltip title="创建数据库">
                    <Icon type="plus-circle-o" 
                        onClick={onCreateDB}
                    />
                </Tooltip>
                <Tooltip title="创建表">
                    <Icon type="plus-circle-o" 
                        onClick={onCreateTable}
                    />
                </Tooltip>
                <Tooltip title="刷新">
                    <Icon type="sync" 
                        onClick={onRefresh}
                    />
                </Tooltip>
                <Tooltip title="SQL查询">
                    <Icon type="search" 
                        onClick={onSQLQuery}
                    />
                </Tooltip>
            </div>
        )
    }
}

export default Toolbar