import * as React from 'react';

import { Tabs } from 'antd';

import TableItem from './tableItem';
import TableOverview from './tableOverview';
import DirtyTable from './dirtytable';
import FieldsTable from './fieldsTable';

const TabPane = Tabs.TabPane;

class DirtyView extends React.Component<any, any> {
    render () {
        const { data } = this.props;
        return <div style={{ padding: '0px 21px 20px' }}>
            <p className='c-dirtyView__p'>
                <span className='c-dirtyView__title'>脏数据表：</span>
                test_dirty
            </p>
            <p className='c-dirtyView__section__header'>
                <span className='c-dirtyView__title'>基本信息</span>
            </p>
            <div className='c-dirtyView__table'>
                <TableItem label='存储数据库'></TableItem>
                <TableItem label='数据存储天数'></TableItem>
                <TableItem label='创建者'></TableItem>
                <TableItem label='创建时间'></TableItem>
                <TableItem label='数据最后变更时间'></TableItem>
            </div>
            <div className="m-tabs c-dirtyView__tabs">
                <Tabs
                    animated={false}
                >
                    <TabPane tab="概览" key="1">
                        <TableOverview tableId={data.id} />
                    </TabPane>
                    <TabPane tab="字段信息" key="2">
                        <FieldsTable id={data.id}/>
                    </TabPane>
                    <TabPane tab="脏数据查看" key="3">
                        <DirtyTable id={data.id}/>
                    </TabPane>
                </Tabs>
            </div>
        </div>
    }
}
export default DirtyView;
