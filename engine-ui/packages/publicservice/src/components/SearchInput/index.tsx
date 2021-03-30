import React, { useState } from 'react';
import { Input, Icon } from 'antd';

export default function SearchInput({
  placeholder = '请求输入搜索内容',
  width = 200,
  onSearch,
}) {
  const [value, setValue] = useState('');

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
          type="search"
          onClick={() => {
            onSearch(value);
          }}
          style={{ cursor: 'pointer' }}
        />
      }
      style={{ width: width }}
    />
  );
}
