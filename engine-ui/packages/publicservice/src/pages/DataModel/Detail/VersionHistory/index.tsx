import React, { useEffect, useMemo, useState } from 'react';
import { Table, Pagination, Button, Modal } from 'antd';
import { columnsGenerator } from './constants';
import './style';
import VersionCompare from '../VersionCompare';
import VersionDetail from '../VersionDetail';
import { EnumModalActionType } from './types';
import { API } from '@/services';
import Message from '@/pages/DataModel/components/Message';
import { EnumModelStatus } from '../../types';

interface IModalAction<T> {
  type: EnumModalActionType;
  payload?: T;
}

interface IPropsVersionHistory {
  modelId: number;
  modelStatus: EnumModelStatus;
}

interface IVersionHistoryItem {
  operateTime: string;
  operator: string;
  version: string;
}

const VersionHistory = (props: IPropsVersionHistory) => {
  const { modelId, modelStatus } = props;

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
    IVersionHistoryItem[]
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
    setSelectedRowKeys([]);
  }, [modelId]);

  // 获取版本历史记录列表
  const getVersionHistoryList = async (params) => {
    setLoading(true);
    try {
      const { success, data, message } = await API.getVersionHistoryList(
        params
      );
      if (success) {
        setVersionHistoryList(data.data);
        setPagination({
          current: data.currentPage,
          pageSize: data.pageSize,
          total: data.totalCount,
        });
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    getVersionHistoryList({
      modelId,
      currentPage: pagination.current,
      pageSize: pagination.pageSize,
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
        setVisibleModalDetail(() => ({
          visible: true,
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

  const recoverVersion = async (modelId: number, version: string) => {
    try {
      const { success, data, message } = await API.recoverVersion({
        id: modelId,
        version,
      });
      if (!success) return Message.error(message);
      if (!data) return Message.success('恢复失败');
      Message.success('恢复成功');
    } catch (error) {
      Message.error(error.message);
    }
  };

  const handlePaginationChange = (current, pageSize) => {
    setPagination((pagination) => ({
      ...pagination,
      current,
      pageSize,
    }));
  };

  const handleModelRecover = async (modelId, version) => {
    // TODO: 判断模型是否处于发布状态
    if (modelStatus === EnumModelStatus.RELEASE) {
      return Message.error('模型已发布，请先下线模型');
    }
    // TODO: 判断模型引用关系，需后端重新定义返回格式
    const { success, data, message } = await API.isModelReferenced({
      id: modelId,
    });
    if (!success) return Message.error(message);
    // 请求，查询当前模型下游产品引用情况
    Modal.confirm({
      title: (
        <span className="cus-modal margin-left-40">
          {`确认将当前模型恢复至V${version}版本吗？`}
        </span>
      ),
      content: data ? (
        <span className="cus-modal margin-left-40">
          当前模型已被--、--引用，恢复后可能导致数据异常。
        </span>
      ) : null,
      okText: '确认',
      cancelText: '取消',
      onOk: () => {
        recoverVersion(modelId, version);
      },
      icon: (
        <span className="icon icon-warn cus-modal iconfont2 iconFilltianchong_Warning-Circle-Fill" />
      ),
    });
  };

  const columns = useMemo(() => {
    return columnsGenerator({
      handleModalDetailAction,
      handleModelRecover,
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
              payload: selectedRowKeys as [string, string],
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
          versions={visibleModelCompare.versions}
        />
      </Modal>
    </div>
  );
};

export default VersionHistory;
