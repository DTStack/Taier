import * as React from 'react';
import { Form, Input } from 'antd';
import { IField } from '../../types';

const { Item } = Form;
interface IProps {
  fieldsMap: IField[];
  editRecord: Object;
  setEditRecord: (editRecord: Object) => void;
}

  class FormArea extends React.Component<IProps, {}> {
    constructor(props) {
      super(props);
    }

    handleChange = (e, item) => {
      const { setEditRecord, editRecord } = this.props;
      console.log(editRecord)
      setEditRecord(Object.assign({}, editRecord, { [item.key]: e.target.value }));
    }
    render() {
      const {
        fieldsMap,
        editRecord,
      } = this.props;
      const tips = {
        lastUpdated: '日期格式YYYY-MM-DD',
        accessTime: '日期格式YYYY-MM-DD',
      }
      return (
        <>
          {
            fieldsMap.map(item => {
              return (
                <Form >
                  <Item label={<div  style={{ minWidth: '100px', textAlign: 'center', display: 'inline-block' }}>{item.value}</div>}>
                    <Input style={{ width: '300px' }} value={editRecord[item.key]} onChange={this.handleChange.bind(this, item)} placeholder={tips[item.key] ? tips[item.key] : ''} />
                  </Item>
                </Form>
              )
            })
          }
        </>
      )
    }
  }
)

export default FormArea;
