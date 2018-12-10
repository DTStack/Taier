import React from 'react';

import { Icon, Tabs } from 'antd';

import ColumnView from './column';
import PartitionView from './partition';

const TabPane = Tabs.TabPane;

const TAB_KEYS = {
    COLUMN: 'column',
    PARTITION: 'partition'
}

class ExtraPaneTableDetail extends React.Component {
    state = {
        activeKey: TAB_KEYS.COLUMN
    }

    render () {
        const { tableName, columns } = this.props;
        const { activeKey } = this.state;
        return (
            <div className="c-extraPane__table__detail">
                <header>
                    <span className="c-table__name">{tableName}</span>
                    <Icon
                        className="c-close__icon"
                        type="close"
                        onClick={this.props.close.bind(null, tableName)}
                    />
                </header>
                <Tabs
                    activeKey={activeKey}
                    style={{ marginTop: '5px', marginBottom: '30px' }}
                    onChange={(key) => {
                        this.setState({
                            activeKey: key
                        })
                    }}
                >
                    <TabPane
                        tab="字段"
                        key={TAB_KEYS.COLUMN}
                    >
                        <ColumnView
                            columns={columns}
                        />
                    </TabPane>
                    <TabPane
                        tab="分区"
                        key={TAB_KEYS.PARTITION}
                    >
                        <PartitionView
                            columns={columns}
                        />
                    </TabPane>
                </Tabs>
            </div>
        )
    }
}

export default ExtraPaneTableDetail;
