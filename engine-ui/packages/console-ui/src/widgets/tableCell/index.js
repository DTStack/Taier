import React from 'react';

export default function TableCell (props) {
    const originStyle = {
        textIndent: '5px',
        backgroundColor: 'transparent',
        backgroundImage: 'none',
        width: '100%',
        border: 'none'
    }

    const { style } = props;
    let newStyle = { ...originStyle, ...style };

    return <textarea
        {...props}
        style={newStyle}
    />
}
