import React from 'react';
import HTable from '../HTable';
import PaneTitle from '../../components/PaneTitle';
import DataInfo from '../DataInfo';
import RelationView from '../RealationView';

import { IModelDetail } from '@/pages/DataModel/types';

interface IPropsModelBasicInfo {
  modelDetail: Partial<IModelDetail>;
  visibleRelationView?: boolean;
}

const ModelBasicInfo = (props: IPropsModelBasicInfo) => {
  const { modelDetail, visibleRelationView = true } = props;
  if (modelDetail === null) return null;
  return (
    <div className="inner-container">
      <div className="margin-bottom-20">
        <PaneTitle title="模型信息" />
        <HTable
          detail={{
            ...modelDetail,
            dsName: `${modelDetail.dsName}(${modelDetail.dsTypeName})`,
          }}
        />
      </div>
      {visibleRelationView && (
        <div className="margin-bottom-20">
          <PaneTitle title="关联视图" />
          <RelationView />
        </div>
      )}
      <div className="margin-bottom-20">
        <PaneTitle title="数据信息" />
        <DataInfo
          relationTableList={modelDetail.joinList}
          metricList={modelDetail.columns.filter((item) => item.metric)}
          dimensionList={modelDetail.columns.filter((item) => item.dimension)}
        />
      </div>
    </div>
  );
};

export default ModelBasicInfo;
