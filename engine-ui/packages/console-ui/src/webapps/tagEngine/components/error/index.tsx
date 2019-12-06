import * as React from 'react';

interface IProps {
    text?: any;
}
const Error: React.SFC<IProps> = function (props: IProps) {
    const { text = '该模块异常。' } = props;
    return (
        <div className="error">
            <div>
                <h2 style={{ textAlign: 'center' }}>{text}</h2>
                <h4 style={{ textAlign: 'center' }}>若该问题长时间存在，请联系管理员。</h4>
            </div>
        </div>
    )
}

export default Error;
