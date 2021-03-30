import React, { useState } from 'react';
import { Input, Icon } from 'antd';

export default function SearchInput({
  placeholder = '请求输入搜索内容',
  width = 200,
  onSearch,
}) {
  const [value, setValue] = useState('');
  const IconSearch = () => (
    <span className="iconfont iconOutlinedxianxing_Search"></span>
  );
  return (
    <Input
      allowClear
      value={value}
      onChange={(e) => {
        setValue(e.target.value);
        if (!e.target.value) {
          onSearch(e.target.value);
        }
      }}
      placeholder={placeholder}
      suffix={
        <Icon
          component={IconSearch}
          onClick={() => {
            onSearch(value);
          }}
          style={{ cursor: 'pointer', color: '#999' }}
        />
      }
      style={{ width: width }}
    />
  );
}
