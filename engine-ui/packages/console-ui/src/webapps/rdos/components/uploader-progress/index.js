import React, { Component } from 'react'
import {
    Col, Progress, message
} from 'antd';
import { UPLOAD_STATUS, resetUploader } from '../../store/modules/uploader';

const defaultStyle = {
    height: '28px',
    lineHeight: '28px'
}

class UploaderProgressBar extends Component {
    static getDerivedStateFromProps (props, state) {
        const uploader = props.uploader;
        if (uploader && uploader.status === UPLOAD_STATUS.SUCCES) {
            message.success(`文件${uploader.fileName}上传成功!`);
            this.props.dispatch(resetUploader());
        }
    }

    render () {
        const { uploader } = this.props;
        let msg = ``;
        if (!uploader || uploader.status === UPLOAD_STATUS.READY) return '';
        if (uploader.status === UPLOAD_STATUS.PROGRESSING) {
            msg = `正在被导入...`;
        } else if (uploader.status === UPLOAD_STATUS.SUCCES) {
            msg = `导入成功!`;
        }
        return (
            <Col className="right" style={defaultStyle}>
                <span>文件 <a style={{ maxWidth: 80 }} className="ellipsis">{uploader.fileName}</a> { msg }</span>
                <Progress style={{ width: 80 }} percent={uploader.percent || 10 } strokeWidth={5} status="active" />
            </Col>
        )
    }
}

export default UploaderProgressBar
