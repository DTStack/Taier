import React from 'react'

const defaultStyle = {
    width: '8px',
    height: '8px',
    display: 'inline-block',
    borderRadius: '50% 50%',
    background: '#43576a',
}

export function Circle(props) {
    const style = Object.assign(defaultStyle, props.style)
    return (
        <div
          {...props}
          style={style}
        >
            {props.children || ''}
        </div>
    )
}
