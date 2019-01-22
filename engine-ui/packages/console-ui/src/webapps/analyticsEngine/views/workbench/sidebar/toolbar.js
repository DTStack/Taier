import React, { Component } from 'react';
import { connect } from 'react-redux';
import MyIcon from '../../../components/icon';

@connect(state => {
    return {
        licenseApps: state.licenseApps
    }
})
class Toolbar extends Component {
    fixArrayIndex = (arr) => {
        let fixArrChildrenApps = [];
        if (arr && arr.length > 1) {
            arr.map(item => {
                switch (item.name) {
                    case '数据库管理':
                        fixArrChildrenApps[0] = item;
                        break;
                    case '表管理':
                        fixArrChildrenApps[1] = item;
                        break;
                }
            })
            return fixArrChildrenApps
        } else {
            return []
        }
    }
    render () {
        const { onCreateDB, onRefresh, onSQLQuery, onCreateTable, licenseApps } = this.props;
        const fixArrChildrenApps = this.fixArrayIndex(licenseApps[2] && licenseApps[2].children);
        const isShowCreateDB = fixArrChildrenApps[0] && fixArrChildrenApps[0].is_Show;
        const isShowCreateTable = fixArrChildrenApps[1] && fixArrChildrenApps[1].is_Show;
        return (
            <div className="toolbar txt-right">
                { isShowCreateTable ? (
                    <MyIcon title="创建表" className="btn-icon" type="btn_add_table"
                        onClick={onCreateTable}
                    />
                ) : null }
                { isShowCreateDB ? (
                    <MyIcon title="创建数据库" className="btn-icon" type="btn_add_database"
                        onClick={onCreateDB}
                    />
                ) : null }
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
