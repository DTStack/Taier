import React from 'react';
import '@/assets/iconfont/iconfont.css';

interface IconProps {
    type: string;
}

export function Icon(props: IconProps) {
    const { type, ...resetProps } = props;
    return (
        <span className={`iconfont codicon codicon-${type}`} {...resetProps} />
    )
}