import React, { useEffect, useState } from 'react';
import Container from '../components/Container';
import { Input, Table, Pagination, message as Message, message } from 'antd'
const { Search } = Input;
import { IModelData } from '../types';
import { columns } from './constants';
import { API } from '@/services';

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

  const fetchModelList = async (requestParams: IReqParams) => {
    try {
      setLoading(false);
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
    } catch (err) {
      Message.error(message);
    } finally {
      setLoading(false);
    }
  }

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
      <div className="table-area" style={{ marginTop: 12, height: 'calc(100% - 44px)' }}>
        { console.log(pagination) }
        <Table
          rowKey="id"
          className="dt-table-border"
          style={{ height: '100%' }}
          columns={columns as any}
          loading={loading}
          dataSource={modelList}
          pagination={{
            current: pagination.current,
            pageSize: pagination.size,
            total: pagination.total,
            onChange: (current, size) => {
              setRequestParams(prev => ({
                ...prev,
                current,
                size
              }))
            }
          }}
          scroll={{ x: 1300, y: 800 }}
        />
      </div>
    </Container>
  </div>
}

export default List;
