import React, { Component } from 'react';
import workbenchAction from '../../../consts/workbenchActionType';

import CreateTable from './createTable';
import DataMap from './datamap';
import TableDetail from './tableDetail';
import DatabaseDetail from './database/detail';
import SQLEditor from './sqlEditor';
import EditTable from './editTable'

class BenchContent extends Component {

    renderContent = () => {

        const props = this.props;
        const { tabData } = props;
        const { editTableInfoList={}, currentTab } = props.workbench.mainBench

        switch (tabData.actionType) {

            case workbenchAction.OPEN_SQL_QUERY: {
                return <SQLEditor data={tabData} />
            }
            case workbenchAction.CREATE_TABLE: {
                return <CreateTable data={tabData} />
            }
            case workbenchAction.OPEN_TABLE_EDITOR: {
                return <EditTable data={tabData}
                    tableDetail={editTableInfoList[`tableInfo${currentTab}`] || {}}
                    saveEditTableInfo={props.saveEditTableInfo}
                    saveTableInfo={props.saveTableInfo}
                    closeTab={()=>this.props.closeTab(tabData.id)}
                    loadCatalogue = {this.props.loadCatalogue}
                 />
            }
            case workbenchAction.OPEN_TABLE: {
                return <TableDetail 
                    data={tabData} 
                    onGenerateCreateSQL={props.onGenerateCreateSQL} 
                />
            }
            case workbenchAction.OPEN_DATABASE: {
                return <DatabaseDetail data={tabData} {...props} />
            }
            case workbenchAction.OPEN_DATA_MAP: {
                return <DataMap
                    onRemoveDataMap={props.onRemoveDataMap}
                    onGenerateCreateSQL={props.onGenerateCreateSQL}
                    data={tabData}
                />
            }
            case workbenchAction.CREATE_DATA_MAP: {
                return <DataMap
                    isCreate={true}
                    loadCatalogue={props.loadCatalogue}
                    onGetDataMap={props.onGetDataMap}
                    onGenerateCreateSQL={props.onGenerateCreateSQL}
                    data={tabData} 
                />
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