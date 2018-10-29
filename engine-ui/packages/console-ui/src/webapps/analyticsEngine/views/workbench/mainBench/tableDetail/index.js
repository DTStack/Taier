import React, { Component } from 'react'
import { Button, Tabs, Row} from 'antd';

import PaneData from './paneData';
import PaneField from './paneField';
import PaneIndex from './paneIndex';
import PanePartition from './panePartition';

const TabPane = Tabs.TabPane;


class TableDetail extends Component {

    
    render () {
        const tableDetail = this.props.data.tableDetail || 
        {
            "code": 1,
            "message": null,
            "data": {
                "column": [
                    {
                        "id": 1,
                        "tableId": 434,
                        "columnName": "id",
                        "columnType": "int",
                        "comment": "自增id",
                        "columnIndex": 0,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 2,
                        "tableId": 434,
                        "columnName": "is_deleted",
                        "columnType": "string",
                        "comment": "是否删除,Y删除，N未删除",
                        "columnIndex": 1,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 3,
                        "tableId": 434,
                        "columnName": "gmt_create",
                        "columnType": "string",
                        "comment": "创建时间",
                        "columnIndex": 2,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 4,
                        "tableId": 434,
                        "columnName": "gmt_modified",
                        "columnType": "string",
                        "comment": "修改时间",
                        "columnIndex": 3,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 5,
                        "tableId": 434,
                        "columnName": "creator",
                        "columnType": "int",
                        "comment": "创建人",
                        "columnIndex": 4,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 6,
                        "tableId": 434,
                        "columnName": "modifier",
                        "columnType": "int",
                        "comment": "修改人",
                        "columnIndex": 5,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 7,
                        "tableId": 434,
                        "columnName": "username",
                        "columnType": "string",
                        "comment": "用户名",
                        "columnIndex": 6,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 8,
                        "tableId": 434,
                        "columnName": "password",
                        "columnType": "string",
                        "comment": "用户登录密码（MD5）加密",
                        "columnIndex": 7,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 9,
                        "tableId": 434,
                        "columnName": "is_active",
                        "columnType": "string",
                        "comment": "是否已经激活,Y已激活，N未激活",
                        "columnIndex": 8,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 10,
                        "tableId": 434,
                        "columnName": "full_name",
                        "columnType": "string",
                        "comment": "姓名",
                        "columnIndex": 9,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 11,
                        "tableId": 434,
                        "columnName": "phone",
                        "columnType": "string",
                        "comment": "用户手机号",
                        "columnIndex": 10,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 12,
                        "tableId": 434,
                        "columnName": "email",
                        "columnType": "string",
                        "comment": "邮箱地址",
                        "columnIndex": 11,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 13,
                        "tableId": 434,
                        "columnName": "company",
                        "columnType": "string",
                        "comment": "用户所属公司",
                        "columnIndex": 12,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 14,
                        "tableId": 434,
                        "columnName": "own_tenant_id",
                        "columnType": "int",
                        "comment": "创建的租户组id",
                        "columnIndex": 13,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 15,
                        "tableId": 434,
                        "columnName": "last_login_date",
                        "columnType": "string",
                        "comment": "最后一次登录时间",
                        "columnIndex": 14,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 16,
                        "tableId": 434,
                        "columnName": "last_login_tenant_id",
                        "columnType": "int",
                        "comment": "最后一次登录时的租户id",
                        "columnIndex": 15,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 17,
                        "tableId": 434,
                        "columnName": "source",
                        "columnType": "int",
                        "comment": "0直接网址1官网2云日志3easydb4阿里云5已有用户在uic建6微信",
                        "columnIndex": 16,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 18,
                        "tableId": 434,
                        "columnName": "external_id",
                        "columnType": "string",
                        "comment": "外部用户id",
                        "columnIndex": 17,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 19,
                        "tableId": 434,
                        "columnName": "is_root",
                        "columnType": "string",
                        "comment": "是否是root用户,Y是，N否",
                        "columnIndex": 18,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    },
                    {
                        "id": 20,
                        "tableId": 434,
                        "columnName": "is_admin",
                        "columnType": "string",
                        "comment": "是否是管理员,Y是，N否",
                        "columnIndex": 19,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    }
                ],
                "tableModel": {
                    "increType": null,
                    "refreshRate": null,
                    "subject": null,
                    "grade": null
                },
                "partition": [
                    {
                        "id": 1,
                        "tableId": 434,
                        "columnName": "ds",
                        "columnType": "string",
                        "comment": null,
                        "columnIndex": 0,
                        "charLen": null,
                        "varcharLen": null,
                        "precision": null,
                        "scale": null
                    }
                ],
                "table": {
                    "id": 434,
                    "tableName": "uic_user",
                    "belongProjectId": 36,
                    "dataSourceId": -1,
                    "project": "dtstack_prod_test",
                    "projectAlias": "袋鼠云产品测试",
                    "chargeUser": "jiangbo@dtstack.com",
                    "gmtCreate": 1529927516000,
                    "catalogue": "",
                    "catalogueId": 57,
                    "tableDesc": "",
                    "permissionStatus": 0,
                    "lifeDay": 9999,
                    "tableSize": "0",
                    "lastDdlTime": 1539831609000,
                    "lastDmlTime": 1532577640000,
                    "gmtModified": 1539831609000,
                    "tasks": null,
                    "checkResult": "[]",
                    "listType": null,
                    "isCollect": 0,
                    "grade": null,
                    "subject": null,
                    "refreshRate": null,
                    "increType": null,
                    "isDeleted": 0,
                    "isIgnore": 0,
                    "timeTeft": null,
                    "passTime": null,
                    "partition": true
                }
            },
            "space": 148
        };
        const patitionsData = tableDetail.partitions || {}
        const indexList = tableDetail.indexes || [];

        const previewData = tableDetail.previewData || {}

        const tabsData = [
            {
                title: '字段信息',
                key: '1',
                content: <PaneField data={{columnData:tableDetail.data.column,partData:tableDetail.data.partition}}/>
            },{
                title: '分区信息',
                key: '2',
                content: <PanePartition partitions={patitionsData.data.data}/>
            },{
                title: '索引信息',
                key: '3',
                content: <PaneIndex indexData={indexList}/>
            },{
                title: '数据预览',
                key: '4',
                content: <PaneData previewList={previewData.data}/>
            }
        ]
        return (
            <div className="table-detail-container">
                <Row className="table-detail-panel">
                    <div className="func-box">
                        <span className="title">数据库信息</span>
                        <Button className="btn" type="primary">生成建表语句</Button>
                    </div>
                    <table className="table-info" width="100%" cellPadding="0" cellSpacing="0">
                        <tbody>
                        <tr>
                            <td>数据库</td>
                            <td>{tableDetail.database}</td>
                            <td>物理存储量</td>
                            <td>{tableDetail.size}</td>
                        </tr>
                        <tr>
                            <td>创建人</td>
                            <td>{tableDetail.creator}</td>
                            <td>生命周期</td>
                            <td>{tableDetail.lifecycle}</td>
                        </tr>
                        <tr>
                            <td>创建时间</td>
                            <td>{tableDetail.createTime}</td>
                            <td>是否分区</td>
                            <td>{tableDetail.splitPart}</td>
                        </tr>
                        <tr>
                            <td>表类型</td>
                            <td>{tableDetail.tableType}</td>
                            <td>表结构最后变更时间</td>
                            <td>{tableDetail.tableUpdateTime}</td>
                        </tr>
                        <tr>
                            <td>描述</td>
                            <td>{tableDetail.desc}</td>
                            <td>数据最后变更时间</td>
                            <td>{tableDetail.dataUpdateTime}</td>
                        </tr>
                        <tr>
                            <td>Sort Scope</td>
                            <td>{tableDetail.sortScope}</td>
                            <td>Block大小</td>
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
                                <td>{tableDetail.MAJOR_COMPACTION_SIZE}</td>
                                <td>AUTO_LOAD_MERGE</td>
                                <td>{tableDetail.AUTO_LOAD_MERGE}</td>
                            </tr>
                            <tr>
                                <td>COMPACTION_LEVEL_THRESHOLD</td>
                                <td>{tableDetail.COMPACTION_LEVEL_THRESHOLD}</td>
                                <td>COMPACTION_PRESERVE_SEGMENTS</td>
                                <td>{tableDetail.COMPACTION_PRESERVE_SEGMENTS}</td>
                            </tr>
                            <tr>
                                <td>ALLOWED_COMPACTION_DAYS</td>
                                <td>{tableDetail.ALLOWED_COMPACTION_DAYS}</td>
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