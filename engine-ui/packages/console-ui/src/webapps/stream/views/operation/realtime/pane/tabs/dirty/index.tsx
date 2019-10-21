import * as React from 'react';

import { get } from 'lodash';

import { Tabs } from 'antd';

import TableItem from './tableItem';
import TableOverview from './tableOverview';
import DirtyTable from './dirtytable';
import FieldsTable from './fieldsTable';

import Api from '../../../../../../api';
import utils from 'utils';

const TabPane = Tabs.TabPane;

export interface TableInfo {
    column: {
        id: number;
        tableId: number;
        columnName: string;
        columnType: string;
        comment: string;
        columnIndex: number;
    }[];
    table: {
        id: number;
        tableName: string;
        belongProjectId: number;
        dataSourceId: number;
        project: string;
        projectAlias: string;
        dbName: string;
        chargeUser: string;
        gmtCreate: number;
        tableDesc: string;
        lifeDay: number;
        tableSize: string;
        lastDdlTime: number;
        lastDmlTime: number;
        gmtModified: number;
    };
    partition: {
        id: number;
        tableId: number;
        columnName: string;
        columnType: string;
        comment: string;
        columnIndex: number;
    }[];
}
interface DirtyViewState {
    tableInfo: TableInfo;
}

class DirtyView extends React.Component<any, DirtyViewState> {
    state: DirtyViewState = {
        tableInfo: null
    }
    componentDidMount () {
        this.getDirtyData();
    }
    async getDirtyData () {
        const { data } = this.props;
        if (!data || !data.id) {
            return;
        }
        let res = await Api.getDitryTableInfo({
            taskId: data.id
        });
        if (res && res.code == 1) {
            this.setState({
                tableInfo: res.data
            })
        }
    }
    render () {
        const { data } = this.props;
        const { tableInfo } = this.state;
        const table = get(tableInfo, 'table', {}) as TableInfo['table'];
        return <div style={{ padding: '0px 21px 20px' }}>
            <p className='c-dirtyView__p'>
                <span className='c-dirtyView__title'>脏数据表：</span>
                {table.tableName}
            </p>
            <p className='c-dirtyView__section__header'>
                <span className='c-dirtyView__title'>基本信息</span>
            </p>
            <div className='c-dirtyView__table'>
                <TableItem label='存储数据库'>{table.dbName}</TableItem>
                <TableItem label='数据存储天数'>{table.lifeDay}</TableItem>
                <TableItem label='创建者'>{table.chargeUser}</TableItem>
                <TableItem label='创建时间'>{utils.formatDateTime(table.gmtCreate)}</TableItem>
                <TableItem label='数据最后变更时间'>{utils.formatDateTime(table.gmtModified)}</TableItem>
            </div>
            <div className="m-tabs c-dirtyView__tabs">
                <Tabs
                    animated={false}
                >
                    <TabPane tab="概览" key="1">
                        <TableOverview tableInfo={tableInfo} taskId={data.id} />
                    </TabPane>
                    <TabPane tab="字段信息" key="2">
                        <FieldsTable tableInfo={tableInfo} />
                    </TabPane>
                    <TabPane tab="脏数据查看" key="3">
                        <DirtyTable tableInfo={tableInfo} />
                    </TabPane>
                </Tabs>
            </div>
        </div>
    }
}
export default DirtyView;
