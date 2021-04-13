import React, { useEffect, useState } from 'react';
import { API } from '@/services';
import { Form, Checkbox, Button, Select, Tooltip, Icon, message } from 'antd';
import { withRouter } from 'react-router';
import SearchInput from '@/components/SearchInput';
import { FormComponentProps } from 'antd/es/form';
import '../style.scss';

interface IProps extends FormComponentProps {
  onSearch(value: string): void; //父组件传递过来的值
}

const { Option } = Select;

function Search(props) {
  const { onSearch } = props;

  const [typeList, setTypeList] = useState([]);
  const [productList, setProductList] = useState([]);

  const getTypeList = async () => {
    let { data, success } = await API.typeList();

    if (success) {
      Array.isArray(data) &&
        data.unshift({
          dataType: '全部',
        });
      setTypeList(data || []);
    } else {
      message.error('获取类型下拉框内容失败！');
    }
  };

  const getProductList = async () => {
    let { data, success } = await API.productList();
    if (success) {
      Array.isArray(data) &&
        data.unshift({
          appName: '全部',
          appType: 'all',
        });
      setProductList(data || []);
    } else {
      message.error('获取授权产品下拉框失败！');
    }
  };

  useEffect(() => {
    getTypeList();
    getProductList();
  }, []);

  // 新增数据源
  const addList = () => {
    props.router.push('/data-source/add');
  };

  //类型多选方法
  const onMultType = (value) => {
    value.includes('全部')
      ? onSearch({ dataTypeList: null })
      : onSearch({ dataTypeList: value });
  };
  //类型多选方法
  const onMultAppType = (value) => {
    if (value.length > 0 && value.includes('all')) {
      onSearch({ appTypeList: null });
    } else {
      onSearch({ appTypeList: value });
    }
  };

  return (
    <div className="top-search">
      <Form layout="inline">
        <Form.Item>
          <SearchInput
            placeholder="数据源名称/描述"
            onSearch={(value) => onSearch({ search: value.trim() })}
            width={160}></SearchInput>
        </Form.Item>

        <Form.Item label="类型">
          <Select
            mode="multiple"
            placeholder="请选择类型"
            allowClear
            showSearch
            maxTagCount={1}
            optionFilterProp="children"
            style={{ width: 280 }}
            onChange={(value) => onMultType(value)}
            defaultValue={['全部']}>
            {typeList.length > 0 &&
              typeList.map((item) => {
                return (
                  <Option value={item.dataType} key={item.dataType}>
                    {item.dataType}
                  </Option>
                );
              })}
          </Select>
        </Form.Item>
        <Form.Item label="授权产品">
          <Select
            mode="multiple"
            style={{ width: 200 }}
            onChange={(value) => onMultAppType(value)}
            allowClear
            showSearch
            maxTagCount={1}
            optionFilterProp="children"
            defaultValue={['all']}>
            {productList.length > 0 &&
              productList.map((item) => {
                return (
                  <Option value={item.appType} key={item.appType}>
                    {item.appName}
                  </Option>
                );
              })}
          </Select>
        </Form.Item>
        <Form.Item>
          <Checkbox
            onChange={(e) =>
              onSearch({ isMeta: e.target.checked ? 1 : 0, currentPage: 1 })
            }>
            显示默认数据库
          </Checkbox>
          <Tooltip title="各模块在创建项目时的默认数据源">
            <Icon style={{ color: '#999' }} type="question-circle-o" />
          </Tooltip>
        </Form.Item>

        <Button type="primary" onClick={addList} style={{ float: 'right' }}>
          新增数据源
        </Button>
      </Form>
    </div>
  );
}
export default Form.create<IProps>({})(withRouter(Search));
