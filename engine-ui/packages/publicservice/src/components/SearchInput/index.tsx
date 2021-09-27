import React, { useState } from 'react';
import { Input, Icon } from 'antd';

interface IProp {
  placeholder?: string;
  width?: number;
  onSearch: (value: string) => void;
  className?: string;
}
export default function SearchInput(props: IProp, any) {
  const {
    placeholder = '请求输入搜索内容',
    width = 200,
    onSearch,
    className,
  } = props;

  const [value, setValue] = useState('');
  const IconSearch = () => (
    <span
      className="iconfont2 iconOutlinedxianxing_Search"
      data-testid="search-icon"></span>
  );
  return (
    <Input
      className={className}
      allowClear
      value={value}
      onChange={(e) => {
        setValue(e.target.value);
        if (!e.target.value) {
          onSearch(e.target.value);
        }
      }}
      onKeyDown={(e) => {
        if (e.key === 'Enter') {
          onSearch(value);
        }
      }}
      data-testid="input"
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
