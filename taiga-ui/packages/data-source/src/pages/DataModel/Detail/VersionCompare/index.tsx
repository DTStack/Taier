/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
