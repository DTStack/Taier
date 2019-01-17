import React from 'react';

import { Button } from 'antd';
import Card from '../card';
import ErrorForm from './error';

import ErrorColumnModel from '../../../../../model/errroColumnModel';

import { resolveFormItemKey } from '../helper';

class RegisterErrorCode extends React.Component {
    newColumn () {
        let { data = {} } = this.props;
        let { errorList = [] } = data;

        this.props.updateData({
            errorList: [
                ...errorList,
                new ErrorColumnModel()
            ]
        });
    }
    deleteColumn (id) {
        let { data = {} } = this.props;
        let { errorList = [] } = data;
        const targetIndex = errorList.findIndex((column) => {
            return column.id == id;
        })
        if (targetIndex > -1) {
            errorList.splice(targetIndex, 1);
        }
        this.props.updateData({
            errorList
        })
    }
    updateColumnData (values) {
        const keyAndValue = Object.entries(values);
        let { data = {} } = this.props;
        let { errorList = [] } = data;
        errorList = [...errorList];

        keyAndValue.forEach(([key, value]) => {
            const { id, name } = resolveFormItemKey(key);
            let targetIndex = errorList.findIndex((column) => {
                return column.id == id;
            })
            if (targetIndex > -1) {
                errorList[targetIndex] = new ErrorColumnModel({
                    ...errorList[targetIndex],
                    [name]: value
                })
            }
        });
        this.props.updateData({
            errorList
        });
    }
    errroRef = React.createRef()
    validate = () => {
        return new Promise((resolve, reject) => {
            this.errroRef.current.validateFieldsAndScroll({}, (err, values) => {
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
        const { errorList = [] } = data;
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
                            data={errorList}
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
