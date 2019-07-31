import * as React from 'react';

import { Button } from 'antd';
import Card from '../card';
import ErrorForm from './error';

import ErrorColumnModel from '../../../../../model/errroColumnModel';

import { resolveFormItemKey } from '../helper';

class RegisterErrorCode extends React.Component<any, any> {
    newColumn () {
        let { data = {} } = this.props;
        let { errorCodeList = [] } = data;

        this.props.updateData({
            errorCodeList: [
                ...errorCodeList,
                new ErrorColumnModel()
            ]
        });
    }
    deleteColumn (id: any) {
        let { data = {} } = this.props;
        let { errorCodeList = [] } = data;
        const targetIndex = errorCodeList.findIndex((column: any) => {
            return column.id == id;
        })
        if (targetIndex > -1) {
            errorCodeList.splice(targetIndex, 1);
        }
        this.props.updateData({
            errorCodeList
        })
    }
    updateColumnData (values: any) {
        const keyAndValue = Object.entries(values);
        let { data = {} } = this.props;
        let { errorCodeList = [] } = data;
        errorCodeList = [...errorCodeList];

        keyAndValue.forEach(([key, value]) => {
            const { id, name } = resolveFormItemKey(key);
            let targetIndex = errorCodeList.findIndex((column: any) => {
                return column.id == id;
            })
            if (targetIndex > -1) {
                errorCodeList[targetIndex] = new ErrorColumnModel({
                    ...errorCodeList[targetIndex],
                    [name]: value
                })
            }
        });
        this.props.updateData({
            errorCodeList
        });
    }
    errroRef = React.createRef()
    validate = () => {
        return new Promise((resolve: any, reject: any) => {
            this.errroRef.current.validateFieldsAndScroll({}, (err: any, values: any) => {
                if (!err) {
                    resolve(true);
                } else {
                    resolve(false);
                }
            })
        })
    }
    render () {
        const { data } = this.props;
        const { errorCodeList = [] } = data;
        return (
            <div>
                <React.Fragment>
                    <Card
                        title='错误码定义'
                        extra={(
                            <Button type='primary' onClick={this.newColumn.bind(this)}>新增参数</Button>
                        )}
                    >
                        <ErrorForm
                            ref={this.errroRef}
                            data={errorCodeList}
                            newColumn={this.newColumn.bind(this)}
                            deleteColumn={this.deleteColumn.bind(this)}
                            updateColumnData={this.updateColumnData.bind(this)}
                        />
                    </Card>
                </React.Fragment>
            </div>
        )
    }
}
export default RegisterErrorCode;
