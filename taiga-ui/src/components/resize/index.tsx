import React from 'react'

export interface ResizeProps {
    onResize?: Function;
    children?: React.ReactNode;
}
export default class Resize extends React.Component<ResizeProps, any> {
    componentDidMount () {
        window.addEventListener('resize', this.resize, false)
    }

    componentWillUnmount () {
        window.removeEventListener('resize', this.resize, false);
    }

    resize = () => {
        const { onResize } = this.props;
        if (onResize) onResize()
    }

    render () {
        return this.props.children
    }
}
