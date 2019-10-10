import * as React from 'react'
import {
    Col, Progress
} from 'antd';
import {
    UPLOAD_STATUS
} from '../../store/modules/uploader';

const defaultStyle: any = {
    height: '28px',
    lineHeight: '28px',
    position: 'absolute',
    zIndex: '999',
    right: 210
}
class UploaderProgressBar extends React.Component<any, any> {
    constructor (props: any) {
        super(props)
        this.state = {};
    }
    render () {
        const { uploader } = this.props;
        let msg = ``;
        if (!uploader || uploader.status === UPLOAD_STATUS.READY) return '';
        if (uploader.status === UPLOAD_STATUS.PROGRESSING) {
            msg = `正在被导入...`;
        } else if (uploader.status === UPLOAD_STATUS.SUCCES) {
            msg = `导入成功!`;
        } else if (uploader.status === UPLOAD_STATUS.FAIL) {
            msg = `导入失败!`;
        }
        return (
            <Col style={defaultStyle}>
                <span>文件 <a style={{ maxWidth: 80, cursor: 'initial' }} className="ellipsis">{uploader.fileName}</a> { msg }</span>
                <Progress
                    style={{ width: 80 }}
                    percent={uploader.percent || 10}
                    status={uploader.status === UPLOAD_STATUS.FAIL ? 'exception' : 'active'}
                    strokeWidth={5}
                />
            </Col>
        )
    }
}

export default UploaderProgressBar
