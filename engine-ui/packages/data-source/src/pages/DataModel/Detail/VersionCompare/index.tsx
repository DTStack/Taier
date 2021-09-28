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

const versionSort = (versions: [string, string]): [string, string] => {
  return parseFloat(versions[0]) < parseFloat(versions[1])
    ? (versions.reverse() as [string, string])
    : versions;
};

const detailParser = (detail) =>
  new Promise((resolve) => {
    const _detail = { ...detail };
    detail.columnList = detail.columns;
    delete _detail.columns;
    return resolve(detail);
  });

const VersionCompare = (props: IPropsVersionCompare) => {
  const { modelId } = props;
  const versions = versionSort(props.versions);
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
      .then(detailParser)
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
        const sqls = res.map((item) => item.result);
        setCompareContent(sqls as [string, string]);
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
            <span className="version-title-prev text">
              版本号：V{versions[0]}
            </span>
            <span className="version-title-next text">
              版本号：V{versions[1]}
            </span>
          </div>
          <DiffEditor
            className="version-compare-diff"
            original={{ value: compareContent[0] }}
            modified={{ value: compareContent[1] }}
            options={{ readOnly: true }}
            sync={true}
            language="sql"
          />
        </>
      )}
    </div>
  );
};

export default VersionCompare;
