import React, { useEffect } from 'react';
import { Result } from 'antd';
import { API } from '@/services';

const TODO = () => {
  useEffect(() => {
    const getList = async () => {
      try {
        const { meta, data } = await API.get();
        if (meta && meta.success) {
          console.log(data);
        } else {
          console.log(meta);
        }
      } catch (ex) {
        console.log(ex);
      }
    };
    getList();
  });

  return (
    <Result
      title="TODO"
      style={{
        background: 'none',
      }}
      subTitle="TODO..."
    />
  );
};

export default TODO;
