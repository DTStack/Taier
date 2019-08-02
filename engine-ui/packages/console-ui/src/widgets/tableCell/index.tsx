import * as React from 'react';

export default function TableCell (props: any) {
    const originStyle: any = {
        textIndent: '5px',
        backgroundColor: 'transparent',
        backgroundImage: 'none',
        width: '100%',
        border: 'none'
    }

    const { style } = props;
    let newStyle: any = { ...originStyle, ...style };

    return <textarea
        {...props}
        style={newStyle}
    />
}
