import React from 'react';

export default function scrollText(value) {
    const style = {
        height: '28px',
        margin: '5px 5px 5px 0px',
        width: '100%',
        textAlign: 'left',
        backgroundColor: 'transparent',
        backgroundImage: 'none',
        border: 'none',
    }
    return <input 
        style={style} 
        title={value}
        className="cell-input" 
        value={value} 
    />
}
