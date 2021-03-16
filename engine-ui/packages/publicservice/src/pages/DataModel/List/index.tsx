import React, { useCallback, useEffect, useMemo, useState } from 'react';
import Container from '../components/Container';
import { Input, Table, message as Message, notification, Pagination, Modal, Drawer } from 'antd'
const { Search } = Input;
import { IModelData } from '../types';
import { columnsGenerator } from './constants';
import Detail from '../Detail';
import { API } from '@/services';
import './style';

interface IPropsList {

}

interface IPagination {
  current: number;
  size: number;
  total: number;
}

interface IReqParams {
  asc: boolean;
  current: number;
  field: string;
  search: string;
  size: number;
}

const List = (props: IPropsList) => {
  const [modelList, setModelList] = useState<IModelData[]>([]);
  const [loading, setLoading] = useState<boolean>(false);
  const [pagination, setPagination] = useState<IPagination>({
    current: 1,
    size: 10,
    total: 0,
  })

  const [requestParams, setRequestParams] = useState<IReqParams>({
    asc: true,
    current: 1,
    field: "",
    search: "",
    size: 10
  })

  const [drawer, setDrawer] = useState({
    visible: false,
    modelId: -1,
  });

  /**
   * 获取数据模型列表
   * @param requestParams 
   */
  const fetchModelList = async (requestParams: IReqParams) => {
    try {
      setLoading(true);
      const { success, data, message } = await API.getModelList(requestParams);
      if(success) {
        console.log(data);
        setModelList(data.contentList);
        setPagination({
          current: data.current,
          size: data.size,
          total: data.total
        })
      } else {
        Message.error(message);
      }
    } catch (error) {
      Message.error(error.message);
    } finally {
      setLoading(false);
    }
  }

  /**
   * 发布数据模型
   */
  const releaseModel = useCallback(async (id: number) => {
    try {
      const { success, message } = await API.releaseModel({ id });
      // console.log(success, data, message);
      if(success) {
        Message.success('模型发布成功')
      } else {
        Message.error(message);
        fetchModelList(requestParams);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }, []);

  /**
   * 下线模型
   * @param id 
   */
  const unreleaseModel = useCallback(async (id: number) => {
    try {
      const { success, message } = await API.unreleaseModel({ id });
      if(success) {
        notification.success({
          message: 'aaa',
          description: '模型下线成功'
        });
        fetchModelList(requestParams);
      } else {
        Message.error(message);

      }
    } catch(error) {
      Message.error(error.message);
    } 
  }, []);

  /**
   * 删除模型
   */
  const deleteModel = useCallback(async (id: number) => {
    try {
      const { success, message } = await API.deleteModel({ id });
      if(success) {
        Message.error('模型删除成功');
      } else {
        Message.error(message);
      }
    } catch(error) {
      Message.error(error.message);
    }
  }, [])

  // 删除按钮点击事件处理，二次确认弹窗
  const handleDeleteBtnClick = (id) => {
    Modal.confirm({
      title: '确认删除吗',
      content: 'aaaaa',
      onOk() {
        deleteModel(id);
      },
      onCancel() {}
    })
  }

  /**
   * 更新模型详情ID
   * 模型名称点击回调
   * @param id 
   */
  const handleModelNameClick = (id: number) => {
    setDrawer({
      visible: true,
      modelId: id,
    })
  }

  const columns = useMemo(() => {
    return columnsGenerator({
      releaseModel,
      unreleaseModel,
      handleDeleteBtnClick,
      handleModelNameClick
    });
  }, [releaseModel, unreleaseModel, handleDeleteBtnClick, handleModelNameClick]);

  useEffect(() => {
    fetchModelList(requestParams);
  }, [requestParams]);

  return <div className="dm-list">
    <Container>
      <header className="search-area">
        <Search
          onSearch={value => setRequestParams(prev => ({ ...prev, search: value }))}
          style={{ width: 200 }}
        />
      </header>
      <div className="table-area">
        <Table
          rowKey="id"
          className="table dt-table-border"
          columns={columns as any}
          loading={loading}
          dataSource={modelList}
          pagination={false}
          scroll={{ x: 1300, y: 800 }}
        />
        <Drawer
          visible={drawer.visible}
          style={{ height: 'calc(100% - 148px)', top: 128 }}
          width={1000}
          getContainer={() => {
            return document.querySelector('.search-area')
          }}
          mask={false}
          onClose={() => {
            setDrawer({
              visible: false,
              modelId: -1
            })
          }}
        >
          <Detail modelId={drawer.modelId} />
        </Drawer>
        <Pagination
          style={{ left: 0, float: 'right' }}
          current={pagination.current}
          pageSize={pagination.size}
          total={pagination.total}
          onChange={(current, size) => {
            setRequestParams(prev => ({
              ...prev,
              current,
              size
            }))
          }}
        />
      </div>
    </Container>
  </div>
}

export default List;
