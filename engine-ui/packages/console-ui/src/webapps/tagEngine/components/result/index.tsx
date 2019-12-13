import * as React from 'react';
import { Icon } from 'antd';
import classnames from 'classnames';
import './style.scss';

interface Props {
    status: 'success'|'error'|'wraning';
    title?: string;
    subTitle?: string;
    extra?: any;
    className?: string;
}
export default class Result extends React.Component<Props, {}> {
    render () {
        const { status, title, subTitle, extra, className } = this.props;
        let iconStatus = 'check-circle';
        let color = '';
        switch (status) {
            case 'success':
                iconStatus = 'check-circle';
                color = '#00A755';
                break;
            case 'error':
                iconStatus = 'close-circle';
                color = '#f5222d';
                break;
            case 'wraning':
                iconStatus = 'exclamation-circle';
                color = '#faad14';
                break;
            default:
        }
        return (
            <div className={classnames('ant-result', className)}>
                <div className="status"><Icon type={iconStatus} style={{ color }} className="result-icon"/></div>
                <div className="title">
                    {
                        title
                    }
                </div>
                <div className="subTitle">{
                    subTitle
                }
                </div>
                <div className="extra">{extra}</div>
            </div>)
    }
}
