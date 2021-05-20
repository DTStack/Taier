import React, { useEffect, useState } from 'react';
import { IModelDetail } from '@/pages/DataModel/types';
import ModelBasicInfo from '../ModelBasicInfo';
import SqlPreview from '../SqlPreview';
import PaneTitle from '@/pages/DataModel/components/PaneTitle';
import mockResolve from '@/utils/mockResolve';
import LoadingPage from '@/components/LoadingPage';

const mockDetail: IModelDetail = {
  step: 5,
  id: 58,
  modelStatus: -1,
  modelName: 'asdf',
  modelEnName: 'asdfasdf',
  dsId: 1,
  dsType: 1,
  dsTypeName: 'Presto',
  dsUrl:
    'eyJqZGJjVXJsIjoiamRiYzpwcmVzdG86Ly8xNzIuMTYuMjMuMjM6ODA4MC9oaXZlL3RhZ19lbmdpbmUiLCJ1c2VybmFtZSI6InJvb3QifQ==',
  dsName: '_tag_engine_tag',
  remark: null,
  schema: 'flink110_sync2',
  tableName: 'console_account',
  updateType: null,
  joinList: [],
  columns: [
    {
      schema: 'flink110_sync2',
      tableName: 'console_account',
      columnName: 'database',
      columnType: 'varchar',
      columnComment: '',
      dimension: true,
      metric: false,
    },
    {
      schema: 'flink110_sync2',
      tableName: 'console_account',
      columnName: 'data',
      columnType: 'varchar',
      columnComment: '',
      dimension: false,
      metric: true,
    },
    {
      schema: 'flink110_sync2',
      tableName: 'console_account',
      columnName: 'pt',
      columnType: 'varchar',
      columnComment: '',
      dimension: false,
      metric: false,
    },
  ],
  modelPartition: {
    datePartitionColumn: {
      schema: 'flink110_sync2',
      tableName: 'console_account',
      columnName: 'database',
      columnType: 'varchar',
      columnComment: '',
      dimension: true,
      metric: false,
      partition: false,
    },
    dateFmt: 'yyyy-MM-dd HH:mm:ss',
    timePartition: true,
    timePartitionColumn: {
      schema: 'flink110_sync2',
      tableName: 'console_account',
      columnName: 'database',
      columnType: 'varchar',
      columnComment: '',
      dimension: true,
      metric: false,
      partition: false,
    },
    timeFmt: 'HH:mm',
  },
  creator: 'admin@dtstack.com',
  createTime: '2021-05-18 14:34:28',
};

interface IPropsVersionDetail {
  modelId: number;
  version: string;
}

const VersionDetail = (props: IPropsVersionDetail) => {
  // 获取选中版本数据模型详情
  // 更具模型详情获取sql信息
  const [modelDetail, setModelDetail] = useState<Partial<IModelDetail>>(null);
  const [sql, setSql] = useState('');
  const [loading, setLoading] = useState(false);

  const { modelId, version } = props;
  console.log(modelId, version);
  useEffect(() => {
    setLoading(true);
    mockResolve(mockDetail).then((res) => {
      setModelDetail(res);
      setLoading(false);
      // 根据detail获取sql
      setSql('select * from user');
    });
  }, []);

  return (
    <div className="pane-container">
      {loading ? (
        <LoadingPage style={{ height: '300px' }} />
      ) : (
        <>
          <ModelBasicInfo
            modelDetail={modelDetail}
            visibleRelationView={false}
          />
          <div>
            <PaneTitle title="SQL信息" />
            <SqlPreview code={sql} />
          </div>
        </>
      )}
    </div>
  );
};

export default VersionDetail;
