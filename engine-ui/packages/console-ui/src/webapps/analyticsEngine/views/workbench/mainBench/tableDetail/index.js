import React, { Component } from 'react'
import moment from 'moment'
import API from '../../../../api'
import { Button, Tabs, Row} from 'antd';

import PaneData from './paneData';
import PaneField from './paneField';
import PaneIndex from './paneIndex';
import PanePartition from './panePartition';
import PaneBucket from './paneBucket';

const TabPane = Tabs.TabPane;


class TableDetail extends Component {
    constructor(props){
        super(props)
        this.state = {
            previewList: [],
            partitionsList: [],
        }
    }
    handleTabsChange = (e)=>{
        if(e === '4'){
            this.getData();
        }else if(e === '2'){
            this.getPartitionsData();
        }
    }
    getData = ()=>{
        this.setState({
            previewList: []
        })
        API.getPreviewData({
        tableId: this.props.data.tableDetail.id,
        databaseId: this.props.data.tableDetail.databaseId,
        }).then(res=>{
        if(res.code === 1){
            this.state.previewList = res.data;
            this.setState({
                previewList: this.state.previewList
            })
        }else{
            notification.error({
            title: '提示',
            description: res.message
            })
        }
        })
    }

    getPartitionsData = ()=>{
    API.getTablePartiton({
      tableId: this.props.data.tableDetail.id,
      pageIndex: 1,
      pageSize: 10
    }).then(res=>{
      if(res.code === 1){
        this.setState({
            partitionsList: res.data.data
        })
      }else{
        notification.error({
          title: '提示',
          description: res.message
        })
      }
    })
  }


    render () {
        const tableDetail = this.props.data.tableDetail || {}
        const patitionsData = tableDetail.partitions || {}
        const indexList = tableDetail.indexes || [];
        const { onGenerateCreateSQL } = this.props;

        const previewData = tableDetail.previewData || {}

        const tabsData = [
            {
                title: <span style={{fontSize: 12}}>字段信息</span>,
                key: '1',
                content: <PaneField data={{columnData:tableDetail.columns,partData:tableDetail.partitions}}/>
            },{
                title: <span style={{fontSize: 12}}>分区信息</span>,
                key: '2',
                content: <PanePartition dataList={this.state.partitionsList || []} tableDateil={tableDetail}/>
            },{
                title: <span style={{fontSize: 12}}>分桶信息</span>,
                key: '3',
                content: <PaneBucket data={tableDetail.bucketInfo || {}}></PaneBucket>
            },{
                title: <span style={{fontSize: 12}}>数据预览</span>,
                key: '4',
                content: <PaneData data={this.state.previewList}  tableDateil={tableDetail}/>,

            }
        ]
        return (
            <div className="table-detail-container pane-wrapper">
                <Row className="table-detail-panel">
                    <div className="func-box">
                        <span className="title" style={{fontWeight: 'bold'}}>表信息</span>
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
                            <td>{tableDetail.dbName || '-'}</td>
                            <td>物理存储量</td>
                            <td>{tableDetail.tableSize || '-'}</td>
                        </tr>
                        <tr>
                            <td>创建人</td>
                            <td>{tableDetail.createUserName || '-'}</td>
                            <td>生命周期</td>
                            <td>{tableDetail.lifeDay || '-'}天</td>
                        </tr>
                        <tr>
                            <td>创建时间</td>
                            <td>{tableDetail.gmtCreate?moment(tableDetail.gmtCreate).format('YYYY-MM-DD'):'-'}</td>
                            <td>是否分区</td>
                            <td>{tableDetail.hasPartition?'是':'否'}</td>
                        </tr>
                        <tr>
                            <td>表类型</td>
                            <td>{tableDetail.type===0?'内部表':'外部表'}</td>
                            <td>表结构最后变更时间</td>
                            <td>{tableDetail.lastDdlTime?moment(tableDetail.lastDdlTime).format('YYYY-MM-DD'):'-'}</td>
                        </tr>
                        <tr>
                            <td>描述</td>
                            <td>{tableDetail.tableDesc || '-'}</td>
                            <td>数据最后变更时间</td>
                            <td>{tableDetail.lastDmlTime?moment(tableDetail.lastDmlTime).format('YYYY-MM-DD'):'-'}</td>
                        </tr>
                        <tr>
                            <td>Sort Scope</td>
                            <td>{tableDetail.sortScope === 0?'LOCAL_SORT':tableDetail.sortScope === 1?'NO_SORT':tableDetail.sortScope === 2?'BATCH_SORT':tableDetail.sortScope === 3?'GLOBAL_SORT':''}</td>
                            <td>Block Size</td>
                            <td>{tableDetail.blockSize || '-'}</td>
                        </tr>
                        </tbody>
                    </table>
                </Row>
                <Row className="table-detail-panel">
                    <div className="func-box">
                        <span className="title" style={{fontWeight: 'bold'}}>压缩配置</span>
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
                                <td></td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </Row>
                <Row className="tabs-row" style={{marginBottom: 40}}>
                    <div className="tabs-container">
                    <Tabs type="card" onChange={this.handleTabsChange}>
                        {
                            tabsData.map(o=>(
                                <TabPane forceRender={true} tab={o.title} key={o.key}>{o.content}</TabPane>
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