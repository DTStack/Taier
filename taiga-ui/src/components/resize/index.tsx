import React from 'react';

export interface IResizeProps {
	onResize?: () => void;
	children?: React.ReactNode;
}

export default class Resize extends React.Component<IResizeProps, void> {
	componentDidMount() {
		window.addEventListener('resize', this.resize, false);
	}

	componentWillUnmount() {
		window.removeEventListener('resize', this.resize, false);
	}

	resize = () => {
		const { onResize } = this.props;
		if (onResize) onResize();
	};

	render() {
		return this.props.children;
	}
}
