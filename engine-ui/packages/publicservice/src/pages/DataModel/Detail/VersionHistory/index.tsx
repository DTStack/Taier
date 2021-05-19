import React, { useMemo, useState } from 'react';
import { Table, Pagination, Button, Modal } from 'antd';
import ModelBasicInfo from '@/pages/DataModel/Detail/ModelBasicInfo';
import { columnsGenerator } from './constants';
import './style';
import { IModelDetail } from '../../types';

const dataSource = [
  {
    "operateTime": "2020-08-22 19:00:00",
    "operator": "xiaoliu",
    "version": "V1.1"
  },
  {
    "operateTime": "2020-08-22 19:00:00",
    "operator": "xiaoliu",
    "version": "V1.2"
  },
  {
    "operateTime": "2020-08-22 19:00:00",
    "operator": "xiaoliu",
    "version": "V1.3"
  },
  {
    "operateTime": "2020-08-22 19:00:00",
    "operator": "xiaoliu",
    "version": "V1.4"
  },
  {
    "operateTime": "2020-08-22 19:00:00",
    "operator": "xiaoliu",
    "version": "V1.5"
  },
  {
    "operateTime": "2020-08-22 19:00:00",
    "operator": "xiaoliu",
    "version": "V1.6"
  },
];

const mockDetail: IModelDetail = {
  "step": 5,
  "id": 58,
  "modelStatus": -1,
  "modelName": "asdf",
  "modelEnName": "asdfasdf",
  "dsId": 1,
  "dsType": 1,
  "dsTypeName": "Presto",
  "dsUrl": "eyJqZGJjVXJsIjoiamRiYzpwcmVzdG86Ly8xNzIuMTYuMjMuMjM6ODA4MC9oaXZlL3RhZ19lbmdpbmUiLCJ1c2VybmFtZSI6InJvb3QifQ==",
  "dsName": "_tag_engine_tag",
  "remark": null,
  "schema": "flink110_sync2",
  "tableName": "console_account",
  "partition": false,
  "updateType": null,
  "joinList": [],
  "columns": [
    {
      "schema": "flink110_sync2",
      "tableName": "console_account",
      "columnName": "database",
      "columnType": "varchar",
      "columnComment": "",
      "dimension": true,
      "metric": false,
      "partition": false
    },
    {
      "schema": "flink110_sync2",
      "tableName": "console_account",
      "columnName": "data",
      "columnType": "varchar",
      "columnComment": "",
      "dimension": false,
      "metric": true,
      "partition": false
    },
    {
      "schema": "flink110_sync2",
      "tableName": "console_account",
      "columnName": "pt",
      "columnType": "varchar",
      "columnComment": "",
      "dimension": false,
      "metric": false,
      "partition": false
    }
  ],
  "modelPartition": {
    "datePartitionColumn": {
      "schema": "flink110_sync2",
      "tableName": "console_account",
      "columnName": "database",
      "columnType": "varchar",
      "columnComment": "",
      "dimension": true,
      "metric": false,
      "partition": false
    },
    "dateFmt": "yyyy-MM-dd HH:mm:ss",
    "timePartition": true,
    "timePartitionColumn": {
      "schema": "flink110_sync2",
      "tableName": "console_account",
      "columnName": "database",
      "columnType": "varchar",
      "columnComment": "",
      "dimension": true,
      "metric": false,
      "partition": false
    },
    "timeFmt": "HH:mm"
  },
  "creator": "admin@dtstack.com",
  "createTime": "2021-05-18 14:34:28",
}

const VersionHistory = (props: any) => {

  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const [visibleModal, setVisibleModal] = useState<{
    visible: boolean;
    detail: IModelDetail;
  }>({
    visible: true,
    detail: mockDetail
   });

  const onSelectedChange = (rowKeys: string[]) => {
    setSelectedRowKeys(rowKeys);
  }

  const isDisabledBtn = selectedRowKeys.length !== 2;


  const handleModalDetailAction = ({ type, payload }: {type: 'CLOSE' | 'OPEN', payload?: any}) => {
    switch(type) {
      case 'OPEN':
        // 1. 请求接口获取模型历史详情
        // 2. 修改状态
        console.log(type, payload);
        setVisibleModal(() => ({
          visible: true,
          detail: mockDetail,
        }))
        break;
      case 'CLOSE':
        setVisibleModal({
          visible: false,
          detail: null,
        })
        break;
    }
  }

  const columns = useMemo(() => {
    return columnsGenerator({
      handleModalDetailAction
    })
  }, []);
  
  return (
    <div className="version-history">
      <div className="version-history-content">
        <Table
          rowKey="version"
          rowSelection={{
            selectedRowKeys,
            onChange: onSelectedChange
          }}
          dataSource={dataSource}
          columns={columns as any}
          pagination={false}
          // TODO: 表格滚动高度计算
          scroll={{
            y: 300
          }}
        />
      </div>
      <div className="version-history-footer">
        <Pagination
          showSizeChanger={true}
          size="small"
          className="float-right"
          total={100}
          pageSize={10}
        />
        <Button className="ml-20" type="primary" disabled={isDisabledBtn}>版本对比</Button>
      </div>
      <Modal
        visible={visibleModal.visible}
        title="版本记录v1.1"
        onOk={() => handleModalDetailAction({ type: 'CLOSE' })}
        onCancel={() => handleModalDetailAction({ type: 'CLOSE' })}
      >
        <ModelBasicInfo
          modelDetail={visibleModal.detail}
          visibleRelationView={false}
        />
      </Modal>
    </div>
  )
}

export default VersionHistory;
