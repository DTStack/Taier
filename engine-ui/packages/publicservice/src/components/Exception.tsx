import React from 'react';
import { Link } from 'react-router-dom';
import { Result, Button } from 'antd';
import { ResultStatusType } from 'antd/lib/result';

interface IProps {
  status: ResultStatusType;
  subTitle: React.ReactNode;
}

const Exception: React.FC<IProps> = ({ status, subTitle }) => (
  <Result
    status={status}
    title={status}
    style={{
      background: 'none',
    }}
    subTitle={subTitle}
    extra={
      <Link to='/'>
        <Button type='primary'>返回首页</Button>
      </Link>
    }
  />
);

export default Exception;
