import * as React from 'react'
import { DragSource, DropTarget } from 'react-dnd';
import './style.scss';
export interface CardProps {
    id: string;
    children: any;
    className?: string;
    moveCard: (id: string, to: number) => void;
    findCard: (id: string) => { index: number };
    connectDragSource?: any;
    connectDropTarget?: any;
    isDragging?: boolean;
}
const cardSource = {
    beginDrag (props) {
        return {
            id: props.id,
            originalIndex: props.findCard(props.id).index
        }
    },

    endDrag (props, monitor) {
        const { id: droppedId, originalIndex } = monitor.getItem()
        const didDrop = monitor.didDrop()

        if (!didDrop) {
            props.moveCard(droppedId, originalIndex)
        }
    }
}

const cardTarget = {

    hover (props, monitor) {
        const { id: draggedId } = monitor.getItem()
        const { id: overId } = props

        if (draggedId !== overId) {
            const { index: overIndex } = props.findCard(overId)
            props.moveCard(draggedId, overIndex)
        }
    },
    drop (props, monitor) {
        const { id: draggedId } = monitor.getItem()
        const { id: overId } = props;
        if (draggedId !== overId) {
            const { index: overIndex } = props.findCard(overId)
            props.moveCard(draggedId, overIndex)
        }
    }
}
@DropTarget('tag', cardTarget, (connect, monitor) => ({
    connectDropTarget: connect.dropTarget(),
    isOver: monitor.isOver()
}))
@DragSource('tag', cardSource, (connect, monitor) => ({
    connectDragSource: connect.dragSource(),
    isDragging: monitor.isDragging()
}))
class Card extends React.Component<CardProps, {}> {
    render () {
        const {
            children,
            connectDragSource,
            connectDropTarget,
            isDragging
        } = this.props;
        const opacity = isDragging ? 0 : 1
        return (<div className="card" style={{ opacity }}>
            {
                connectDragSource && connectDropTarget &&
                connectDragSource(
                    connectDropTarget(<div>
                        {
                            children
                        }
                    </div>)
                )
            }

        </div>)
    }
}
export default Card;
