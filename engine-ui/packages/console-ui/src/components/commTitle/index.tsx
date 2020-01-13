import * as React from 'react';
import './style.scss'
interface TitleProps {
    readonly titleName: string;
    className?: string;
}
class CommTitle extends React.Component<TitleProps, any> {
    constructor (props: any) {
        super(props);
    }
    render () {
        const { titleName, className } = this.props;
        const cls = `c-title__color ${className}`;
        return (
            <div>
                <h1 className={cls}>{titleName}</h1>
            </div>
        )
    }
}
export default CommTitle;
