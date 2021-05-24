import React, { useEffect, useState } from 'react';
import { IModelDetail } from '@/pages/DataModel/types';
import ModelBasicInfo from '../ModelBasicInfo';
import SqlPreview from '../SqlPreview';
import PaneTitle from '@/pages/DataModel/components/PaneTitle';
import LoadingPage from '@/components/LoadingPage';
import { API } from '@/services';
import Message from '@/pages/DataModel/components/Message';
import { EnumSize } from '../types';

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

  const getVersionDetail = async (modelId, version) => {
    setLoading(true);
    try {
      const detailRes = await API.getVersionDetail({
        id: modelId,
        version,
      });
      if (!detailRes.success) return Message.error(detailRes.message);
      setModelDetail(detailRes.data);
      const sqlRes = await API.previewSql(detailRes.data);
      if (!sqlRes.success) return Message.error(sqlRes.message);
      setSql(sqlRes.message);
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getVersionDetail(modelId, version);
  }, []);

  return (
    <div className="pane-container">
      {loading ? (
        <LoadingPage style={{ height: '300px' }} />
      ) : (
        <>
          <ModelBasicInfo modelDetail={modelDetail} size={EnumSize.SMALL} />
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
