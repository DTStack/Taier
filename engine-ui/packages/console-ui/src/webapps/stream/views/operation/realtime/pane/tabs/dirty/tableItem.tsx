import * as React from 'react';

interface TableItemProps {
    label: React.ReactNode;
}
export default function TableItem (props: React.PropsWithChildren<TableItemProps>) {
    return (
        <div className='c-dirtyView__table__item'>
            <div className='c-dirtyView__table__item__label'>{props.label}</div>
            <div className='c-dirtyView__table__item__content'>{props.children}</div>
        </div>
    )
}
