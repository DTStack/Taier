import React from 'react';
import { Divider, Icon, Modal } from 'antd';
import { EnumModelActionType } from './types';
import { EnumModelStatus } from 'pages/DataModel/types';
import classnames from 'classnames';
import { API } from '@/services';
import Message from '@/pages/DataModel/components/Message';

export const modelStatusMap = new Map([
  [EnumModelStatus.UNRELEASE, '未发布'],
  [EnumModelStatus.RELEASE, '已发布'],
  [EnumModelStatus.OFFLINE, '已下线'],
]);

const getColorByModelStatus = (status: EnumModelStatus) => {
  switch (status) {
    case EnumModelStatus.RELEASE:
      return '#12BC6A';
    case EnumModelStatus.UNRELEASE:
      return '#FF5F5C';
    case EnumModelStatus.OFFLINE:
      return '#BFBFBF';
  }
};

export const columnsGenerator = ({
  handleModelAction,
  handleDeleteBtnClick,
  handleModelNameClick,
  dataSourceFilterOptions,
  router,
}) => {
  return [
    {
      title: '模型名称',
      dataIndex: 'modelName',
      key: 'modelName',
      width: 240,
      fixed: true,
      ellipsis: true,
      render: (modelName, record) => {
        return (
          <a
            onClick={() => handleModelNameClick(record.id)}
            className="btn-link">
            {modelName}
          </a>
        );
      },
    },
    {
      title: '模型英文名',
      dataIndex: 'modelEnName',
      key: 'modelEnName',
      width: 200,
      ellipsis: true,
    },
    {
      title: '数据源',
      dataIndex: 'dataSourceType',
      width: 200,
      ellipsis: true,
      filters: dataSourceFilterOptions,
      filterIcon: (filtered) => (
        <Icon
          component={() => (
            <span
              className={classnames({
                iconfont2: true,
                iconFilltianchong_shaixuan: true,
                'icon-filter': true,
                filtered: filtered,
              })}
            />
          )}
          style={{ cursor: 'pointer', position: 'relative', width: 16 }}
        />
      ),
      filterMultiple: true,
      render: (text, record) => {
        return `${record.dsName}(${record.dsTypeName})`;
      },
    },
    {
      title: '状态',
      dataIndex: 'modelStatus',
      key: 'modelStatus',
      width: 160,
      ellipsis: true,
      filters: Array.from(modelStatusMap).map((item) => ({
        text: item[1],
        value: item[0],
      })),
      filterMultiple: true,
      filterIcon: (filtered) => (
        <Icon
          component={() => (
            <span
              className={classnames({
                iconfont2: true,
                iconFilltianchong_shaixuan: true,
                'icon-filter': true,
                filtered: filtered,
              })}
            />
          )}
          style={{
            cursor: 'pointer',
            position: 'relative',
            width: 16,
            marginLeft: 4,
          }}
        />
      ),
      render: (modelStatus) => {
        return (
          <div>
            <div
              className="dot"
              style={{ background: getColorByModelStatus(modelStatus) }}
            />
            {modelStatusMap.get(modelStatus)}
          </div>
        );
      },
    },
    {
      title: '创建人',
      dataIndex: 'creator',
      key: 'creator',
      width: 160,
      ellipsis: true,
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      width: 200,
      ellipsis: true,
    },
    {
      title: '更新时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      width: 200,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      key: 'remark',
      width: 200,
      ellipsis: true,
    },
    {
      title: '操作',
      key: 'operation',
      width: 200,
      fixed: 'right',
      render: (text, record) => {
        const isPublished = record.modelStatus === 1;
        const btnRelease = (
          <a
            className="btn-link"
            onClick={() =>
              handleModelAction({
                type: EnumModelActionType.RELEASE,
                id: record.id,
              })
            }>
            发布
          </a>
        );
        const btnUnrelease = (
          <a
            className="btn-link"
            onClick={() =>
              handleModelAction({
                type: EnumModelActionType.UNRELEASE,
                id: record.id,
              })
            }>
            下线
          </a>
        );
        const btnDelete = (isPublish: boolean) => {
          if (!isPublish) {
            return (
              <a
                className="btn-link"
                onClick={() => {
                  handleDeleteBtnClick(record.id);
                }}>
                删除
              </a>
            );
          } else {
            return <span className="btn-disabled">删除</span>;
          }
        };
        const btnEdit = (
          <a
            className="btn-link"
            onClick={async () => {
              // TODO: 根据后端接口定义修改字段
              const { success, data, message } = await API.isModelReferenced({
                id: record.id,
              });
              if (!success) return Message.error(message);
              if (data.ref) {
                const prods = data.prod || [];
                const title = `该模型已经被${prods.join(
                  '、'
                )}引用，修改后可能导致数据异常，确定编辑吗？`;
                Modal.confirm({
                  title: title,
                  onOk: () => {
                    router.push(`/data-model/edit/${record.id}`);
                  },
                  okText: '确定',
                  cancelText: '取消',
                });
              } else {
                router.push(`/data-model/edit/${record.id}`);
              }
            }}>
            编辑
          </a>
        );
        return (
          <span>
            {isPublished ? btnUnrelease : btnRelease}
            <Divider type="vertical" />
            {btnEdit}
            <Divider type="vertical" />
            {btnDelete(isPublished)}
          </span>
        );
      },
    },
  ];
};
