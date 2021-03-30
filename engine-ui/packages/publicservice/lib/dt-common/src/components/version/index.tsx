
import * as React from 'react';

declare var APP: any;

const Version: React.SFC<any> = (props) => {
    const style = {
        background: 'opacity',
        bottom: 0,
        color: 'transparent',
        position: 'fixed',
        right: 0,
        ...props.style
    };
    return (
        <span style={style}>版本：{APP && APP.VERSION}</span>
    );
};
export default Version;
