import React, { Component } from 'react';

import MyIcon from '../../../components/icon';

class Toolbar extends Component {

    render () {
        const { onCreateDB, onRefresh, onSQLQuery, onCreateTable } = this.props;
        return (
            <div className="toolbar txt-right">
                <MyIcon title="创建表" className="btn-icon" type="btn_add_table" 
                    onClick={onCreateTable}
                />
                <MyIcon title="创建数据库" className="btn-icon" type="btn_add_database" 
                    onClick={onCreateDB}
                />
                <MyIcon title="SQL查询" className="btn-icon" type="btn_sql_query" 
                    onClick={onSQLQuery}
                />
                <MyIcon title="刷新" className="btn-icon" type="btn_refresh" 
                    onClick={onRefresh}
                />
            </div>
        )
    }
}

export default Toolbar