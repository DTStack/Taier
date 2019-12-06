import * as React from 'react';
import Error from '../error';

interface IProps {
    children?: any;
}

interface IState {
    hasError: boolean;
}
export default class ErrorBoundary extends React.Component<IProps, IState> {
    state: IState = { hasError: false }
    // eslint-disable-next-line handle-callback-err
    static getDerivedStateFromError (error) {
        // Update state so the next render will show the fallback UI.
        return { hasError: true };
    }

    componentDidCatch (error, errorInfo) {
        // You can also log the error to an error reporting service
        this.setState({ hasError: true });
        console.log(error);
        console.log(errorInfo);
    }

    render () {
        if (this.state.hasError) {
            return <Error/>;
        }
        return this.props.children;
    }
}
