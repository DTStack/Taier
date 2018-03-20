import React from 'react';

export default function TableCell(props) {
    const originStyle = {
        textIndent: '5px',
        backgroundColor: 'transparent',
        backgroundImage: 'none',
        width: '100%',
        border: 'none',
    }
    const { style, value } = props
    let newStyle = Object.assign(originStyle, style)
    return <textarea 
        {...props}
        style={newStyle}
    />
}
