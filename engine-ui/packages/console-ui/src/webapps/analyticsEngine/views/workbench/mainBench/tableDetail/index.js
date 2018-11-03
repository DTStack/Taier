import React, { Component } from 'react'
import moment from 'moment'
import { Button, Tabs, Row} from 'antd';

import PaneData from './paneData';
import PaneField from './paneField';
import PaneIndex from './paneIndex';
import PanePartition from './panePartition';

const TabPane = Tabs.TabPane;


class TableDetail extends Component {

    render () {
        const tableDetail = this.props.data.tableDetail || {}
        const patitionsData = tableDetail.partitions || {}
        const indexList = tableDetail.indexes || [];
        const { onGenerateCreateSQL } = this.props;

        const previewData = tableDetail.previewData || {}

        const tabsData = [
            {
                title: '字段信息',
                key: '1',
                content: <PaneField data={{columnData:tableDetail.columns,partData:tableDetail.partitions}}/>
            },{
                title: '分区信息',
                key: '2',
                content: <PanePartition tableDateil={tableDetail}/>
            },{
                title: '数据预览',
                key: '4',
                content: <PaneData  tableDateil={tableDetail}/>
            }
        ]
        return (
            <div className="table-detail-container pane-wrapper">
                <Row className="table-detail-panel">
                    <div className="func-box">
                        <span className="title">数据库信息</span>
                        <Button className="btn" type="primary"
                            onClick={() => onGenerateCreateSQL({
                                tableId: tableDetail.id,
                                databaseId: tableDetail.databaseId,
                            })}
                        >生成建表语句</Button>
                    </div>
                    <table className="table-info" width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                        <tr>
                            <td>数据库</td>
                            <td>{tableDetail.dbName}</td>
                            <td>物理存储量</td>
                            <td>{tableDetail.tableSize}</td>
                        </tr>
                        <tr>
                            <td>创建人</td>
                            <td>{tableDetail.createUserName}</td>
                            <td>生命周期</td>
                            <td>{tableDetail.lifeDay}天</td>
                        </tr>
                        <tr>
                            <td>创建时间</td>
                            <td>{moment(tableDetail.gmtCreate).format('YYYY-MM-DD')}</td>
                            <td>是否分区</td>
                            <td>{tableDetail.hasPartition?'是':'否'}</td>
                        </tr>
                        <tr>
                            <td>表类型</td>
                            <td>{tableDetail.type===0?'内部表':'外部表'}</td>
                            <td>表结构最后变更时间</td>
                            <td>{moment(tableDetail.lastDdlTime).format('YYYY-MM-DD')}</td>
                        </tr>
                        <tr>
                            <td>描述</td>
                            <td>{tableDetail.tableDesc}</td>
                            <td>数据最后变更时间</td>
                            <td>{moment(tableDetail.lastDmlTime).format('YYYY-MM-DD')}</td>
                        </tr>
                        <tr>
                            <td>Sort Scope</td>
                            <td>{tableDetail.sortScope === 0?'LOCAL_SORT':tableDetail.sortScope === 1?'NO_SORT':tableDetail.sortScope === 2?'BATCH_SORT':tableDetail.sortScope === 3?'GLOBAL_SORT':''}</td>
                            <td>Block Size</td>
                            <td>{tableDetail.blockSize}</td>
                        </tr>
                        </tbody>
                    </table>
                </Row>
                <Row className="table-detail-panel">
                    <div className="func-box">
                        <span className="title">压缩配置</span>
                    </div>
                    <table className="table-info"  width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                            <tr>
                                <td>MAJOR_COMPACTION_SIZE</td>
                                <td>{tableDetail.compactionSize}</td>
                                <td>AUTO_LOAD_MERGE</td>
                                <td>{tableDetail.autoLoadMerge}</td>
                            </tr>
                            <tr>
                                <td>COMPACTION_LEVEL_THRESHOLD</td>
                                <td>{tableDetail.levelThreshold}</td>
                                <td>COMPACTION_PRESERVE_SEGMENTS</td>
                                <td>{tableDetail.preserveSegments}</td>
                            </tr>
                            <tr>
                                <td>ALLOWED_COMPACTION_DAYS</td>
                                <td>{tableDetail.allowCompactionDays}</td>
                            </tr>
                        </tbody>
                    </table>
                </Row>
                <Row className="tabs-row" style={{marginBottom: 40}}>
                    <div className="tabs-container">
                    <Tabs type="card"  >
                        {
                            tabsData.map(o=>(
                                <TabPane tab={o.title} key={o.key}>{o.content}</TabPane>
                            ))
                        }
                    </Tabs>
                    </div>
                </Row>
            </div>
        )
    }
}

export default TableDetail