import React, { useEffect, useState } from 'react';
import DiffEditor from '@/components/DiffEditor';
import LoadingPage from '@/components/LoadingPage';
import Message from '@/pages/DataModel/components/Message';
import { API } from '@/services';
import './style';

interface IPropsVersionCompare {
  modelId: number;
  versions: [string, string];
}

const VersionCompare = (props: IPropsVersionCompare) => {
  const { modelId, versions } = props;
  const [loading, setLoading] = useState(true);
  const [compareContent, setCompareContent] = useState<[string, string]>([
    '',
    '',
  ]);

  const getSqlByVersion = (modelId, version) => {
    return API.getVersionDetail({ id: modelId, version })
      .then(({ success, data, message }) => {
        if (!success) throw new Error(message);
        return data;
      })
      .then((detail) => API.previewSql(detail))
      .then(({ success, data, message }) => {
        if (!success) throw new Error(message);
        return data;
      });
  };

  useEffect(() => {
    setLoading(true);
    if (versions.some((version) => version === '')) return setLoading(false);
    Promise.all(versions.map((version) => getSqlByVersion(modelId, version)))
      .then((res) => {
        setCompareContent(res as [string, string]);
      })
      .catch((error) => {
        Message.error(error.message);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  return (
    <div className="version-compare">
      {loading ? (
        <LoadingPage />
      ) : (
        <>
          <div className="version-title">
            <span className="version-title-prev text">版本号：v1.0</span>
            <span className="version-title-next text">版本号：v2.0</span>
          </div>
          <DiffEditor
            className="version-compare-diff"
            original={{ value: compareContent[0] }}
            modified={{ value: compareContent[1] }}
            options={{ readOnly: true }}
            sync={true}
            language="text"
          />
        </>
      )}
    </div>
  );
};

export default VersionCompare;
