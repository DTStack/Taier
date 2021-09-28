import React from 'react';
import { Modal } from 'antd';
import './style';

const DtModal = (props: any) => {
  return (
    <Modal {...props} className={'dt-modal'}>
      {props.children}
    </Modal>
  );
};

export default DtModal;
