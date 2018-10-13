import React, { Component } from 'react';
import workbenchAction from '../../../consts/workbenchActionType';

import CreateTable from './createTable';
import CreateDataMap from './createDataMap';
import TableDetail from './tableDetail';
import DatabaseDetail from './databaseDetail';
import SQLEditor from './sqlEditor';

class BenchContent extends Component {

    renderContent = () => {

        const props = this.props;
        const { tabData } = props;

        switch (tabData.actionType) {

            case workbenchAction.OPEN_SQL_QUERY: {
                return <SQLEditor data={tabData} />
            }
            case workbenchAction.CREATE_TABLE: {
                return <CreateTable data={tabData} />
            }
            case workbenchAction.OPEN_TABLE: {
                return <TableDetail data={tabData} />
            }
            case workbenchAction.OPEN_DATABASE: {
                return <DatabaseDetail data={tabData} />
            }
            case workbenchAction.CREATE_DATA_MAP: {
                return <CreateDataMap data={tabData} />
            }
            default: <p>
                未知类型
            </p>
        }

    }

    render () {
        return (
            <div className="m-content">
                {this.renderContent()}
            </div>
        )
    }
}

export default BenchContent