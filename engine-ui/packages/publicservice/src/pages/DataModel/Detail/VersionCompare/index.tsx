import React, { useEffect, useState } from 'react';
import DiffEditor from '@/components/DiffEditor';
import LoadingPage from '@/components/LoadingPage';
import mockResolve from '@/utils/mockResolve';
import './style';

const d = {
  '1.1':
    'select * from A\nselect b from c\nselect * from A\nselect b from c\nselect * from A\nselect b from c\nselect * from A\nselect b from c\nselect * from A\nselect b from cselect b from cselect b from cselect b from cselect b from cselect b from cselect b from c',
  '1.2': 'select username form A\nselect b from c',
};

interface IPropsVersionCompare {
  modelId: number;
  versions: [string, string];
}

const VersionCompare = (props: IPropsVersionCompare) => {
  const version = ['1.1', '1.2'];
  const [loading, setLoading] = useState(true);
  const [compareContent, setCompareContent] = useState<[string, string]>([
    '',
    '',
  ]);

  useEffect(() => {
    setLoading(true);
    Promise.all(version.map((item) => mockResolve<string>(d[item])))
      .then((res) => {
        setCompareContent(res as [string, string]);
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
