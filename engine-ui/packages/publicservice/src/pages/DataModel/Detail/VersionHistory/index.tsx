import React, { useEffect, useMemo, useState } from 'react';
import { Table, Pagination, Button, Modal } from 'antd';
import { columnsGenerator, dataSource } from './constants';
import './style';
import VersionCompare from '../VersionCompare';
import VersionDetail from '../VersionDetail';
import { EnumModalActionType } from './types';
import mockResolve from '@/utils/mockResolve';

interface IModalAction<T> {
  type: EnumModalActionType;
  payload?: T;
}

interface IPropsVersionHistory {
  modelId: number;
}

interface IVersionGistoryItem {
  operateTime: string;
  operator: string;
  version: string;
}

const VersionHistory = (props: IPropsVersionHistory) => {
  const { modelId } = props;

  const [selectedRowKeys, setSelectedRowKeys] = useState<string[]>([]);
  const [visibleModalDetail, setVisibleModalDetail] = useState<{
    visible: boolean;
    modelId: number;
    version: string;
  }>({
    visible: false,
    modelId: -1,
    version: '',
  });

  const [visibleModelCompare, setVisibleModelCompare] = useState<{
    visible: boolean;
    versions?: [string, string];
  }>({
    visible: false,
    versions: ['', ''],
  });
  const [versionHistoryList, setVersionHistoryList] = useState<
    IVersionGistoryItem[]
  >([]);

  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0,
  });

  const onSelectedChange = (rowKeys: string[]) => {
    setSelectedRowKeys(rowKeys);
  };

  useEffect(() => {
    setLoading(true);
    // TODO: 获取版本历史记录列表
    mockResolve(dataSource).then((res) => {
      setVersionHistoryList(dataSource);
      setPagination({
        current: 1,
        pageSize: 10,
        total: 100,
      });
      setSelectedRowKeys([]);
      setLoading(false);
    });
  }, [modelId, pagination.current, pagination.pageSize]);

  const isDisabledBtn = selectedRowKeys.length !== 2;

  const handleModalDetailAction = ({
    type,
    payload,
  }: IModalAction<{
    modelId: number;
    version: string;
  }>) => {
    switch (type) {
      case 'OPEN':
        // 1. 请求接口获取模型历史详情
        // 2. 修改状态
        setVisibleModalDetail(() => ({
          visible: true,
          // detail: null,
          modelId: payload.modelId,
          version: payload.version,
        }));
        break;
      case 'CLOSE':
        setVisibleModalDetail({
          visible: false,
          modelId: -1,
          version: '',
        });
        break;
    }
  };

  const hadnleModalCompareAction = ({
    type,
    payload,
  }: IModalAction<[string, string]>) => {
    switch (type) {
      case 'CLOSE':
        setVisibleModelCompare({
          visible: false,
          versions: ['', ''],
        });
        break;
      case 'OPEN':
        setVisibleModelCompare({
          visible: true,
          versions: payload,
        });
        break;
    }
  };

  const handlePaginationChange = (current, pageSize) => {
    setPagination((pagination) => ({
      ...pagination,
      current,
      pageSize,
    }));
  };

  const columns = useMemo(() => {
    return columnsGenerator({
      handleModalDetailAction,
      modelId,
    });
  }, []);

  return (
    <div className="version-history">
      <div className="version-history-content">
        <Table
          rowKey="version"
          rowSelection={{
            selectedRowKeys,
            onChange: onSelectedChange,
          }}
          loading={loading}
          dataSource={versionHistoryList}
          columns={columns as any}
          pagination={false}
          // TODO: 表格滚动高度计算
          scroll={{
            y: 300,
          }}
        />
      </div>
      <div className="version-history-footer">
        <Pagination
          showSizeChanger={true}
          size="small"
          className="float-right"
          total={pagination.total}
          pageSize={pagination.pageSize}
          current={pagination.current}
          onChange={handlePaginationChange}
        />
        <Button
          className="ml-20"
          type="primary"
          disabled={isDisabledBtn}
          onClick={() =>
            hadnleModalCompareAction({
              type: EnumModalActionType.OPEN,
              payload: ['1.1', '1.2'],
            })
          }>
          版本对比
        </Button>
      </div>
      <Modal
        visible={visibleModalDetail.visible}
        title={`版本记录${visibleModalDetail.version}`}
        className="modal-version-detail"
        onCancel={() =>
          handleModalDetailAction({ type: EnumModalActionType.CLOSE })
        }
        footer={
          <Button
            type="primary"
            onClick={() =>
              handleModalDetailAction({ type: EnumModalActionType.CLOSE })
            }>
            确定
          </Button>
        }
        destroyOnClose={true}>
        <VersionDetail
          modelId={visibleModalDetail.modelId}
          version={visibleModalDetail.version}
        />
      </Modal>
      <Modal
        visible={visibleModelCompare.visible}
        className="modal-version-compare"
        title="版本对比"
        destroyOnClose={true}
        footer={
          <Button
            type="primary"
            onClick={() =>
              hadnleModalCompareAction({ type: EnumModalActionType.CLOSE })
            }>
            确定
          </Button>
        }
        onCancel={() => {
          hadnleModalCompareAction({ type: EnumModalActionType.CLOSE });
        }}>
        <VersionCompare
          modelId={modelId}
          versions={selectedRowKeys as [string, string]}
        />
      </Modal>
    </div>
  );
};

export default VersionHistory;
