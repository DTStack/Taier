package com.dtstack.engine.common.queue;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.commons.lang3.StringUtils;



/**
 *
 * @author sishu.yss
 *
 * @param <E>
 */
public class OrderLinkedBlockingQueue<E> extends AbstractQueue<E>
        implements BlockingQueue<E>, java.io.Serializable{

    /** The capacity bound, or Integer.MAX_VALUE if none */
    private final int capacity;

    /** Current number of elements */
    private final AtomicInteger count = new AtomicInteger();

    /**
     * Head of linked list.
     * Invariant: head.item == null
     */
    transient Node<E> head;

//    /**
//     * Tail of linked list.
//     * Invariant: last.next == null
//     */
//    private transient Node<E> last;

    /** Lock held by take, poll, etc */
    private final ReentrantLock allLock = new ReentrantLock();

    /** Wait queue for waiting takes */
    private final Condition notEmpty = allLock.newCondition();

    /** Lock held by put, offer, etc */
//    private final ReentrantLock putLock = new ReentrantLock();

    /** Wait queue for waiting puts */
    private final Condition notFull = allLock.newCondition();

    /**
     * Creates a {@code LinkedBlockingQueue} with a capacity of
     * {@link Integer#MAX_VALUE}.
     */
    public OrderLinkedBlockingQueue() {
        this(Integer.MAX_VALUE);
    }

    /**
     * Creates a {@code LinkedBlockingQueue} with the given (fixed) capacity.
     *
     * @param capacity the capacity of this queue
     * @throws IllegalArgumentException if {@code capacity} is not greater
     *         than zero
     */
    public OrderLinkedBlockingQueue(int capacity) {
        if (capacity <= 0){ throw new IllegalArgumentException();}
        this.capacity = capacity;
        head = new Node<E>(null);
    }

    /**
     * Linked list node class
     */
    static class Node<E> {
        E item;

        /**
         * One of:
         * - the real successor Node
         * - this Node, meaning the successor is head.next
         * - null, meaning there is no successor (this is the last node)
         */
        Node<E> next;

        Node<E> pre;

        Node(E x) { item = x; }
    }

    /**
     * Linked list node class
     */
    public static class IndexNode<E> {
        E item;
        int index;

        public IndexNode(E item, int index) {
            this.item = item;
            this.index = index;
        }
        public E getItem() {
            return item;
        }
        public int getIndex() {
            return index;
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -4798912640993489687L;

    @Override
    public E poll() {
        final AtomicInteger count = this.count;
        if (count.get() == 0){
            return null;
        }
        E x = null;
        int c = -1;
        final ReentrantLock takeLock = this.allLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                x = dequeue();
                c = count.getAndDecrement();
                if (c > 1){
                    notEmpty.signal();
                }
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity){
            signalNotFull();
        }
        return x;
    }

    @Override
    public E peek() {
        throw new UnsupportedOperationException("unsupported peek method");
    }

    @Override
    public boolean offer(E e) {
        throw new UnsupportedOperationException("unsupported offer method");
    }

    @Override
    public void put(E e) throws InterruptedException {
        if (e == null){ throw new NullPointerException();}
        // Note: convention in all put/take/etc is to preset local var
        // holding count negative to indicate failure unless set.
        int c = -1;
        Node<E> node = new Node<E>(e);
        final ReentrantLock putLock = this.allLock;
        final AtomicInteger count = this.count;
        putLock.lockInterruptibly();
        try {
            /*
             * Note that count is used in wait guard even though it is
             * not protected by lock. This works because count can
             * only decrease at this point (all other puts are shut
             * out by lock), and we (or some other waiting put) are
             * signalled if it ever changes from capacity. Similarly
             * for all other uses of count in other wait guards.
             */
            while (count.get() == capacity) {
                notFull.await();
            }
            enqueue(node);
            c = count.getAndIncrement();
            if (c + 1 < capacity){
                notFull.signal();
            }
        } finally {
            putLock.unlock();
        }
        if (c == 0){
            signalNotEmpty();
        }

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException {
        throw new UnsupportedOperationException("unsupported offer method");
    }

    @Override
    public E take() throws InterruptedException {
        // TODO Auto-generated method stub
        E x;
        int c = -1;
        final AtomicInteger count = this.count;
        final ReentrantLock takeLock = this.allLock;
        takeLock.lockInterruptibly();
        try {
            while (count.get() == 0) {
                notEmpty.await();
            }
            x = dequeue();
            c = count.getAndDecrement();
            if (c > 1){
                notEmpty.signal();
            }
        } finally {
            takeLock.unlock();
        }
        if (c == capacity){
            signalNotFull();
        }
        return x;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        throw new UnsupportedOperationException("unsupported poll method");
    }

    @Override
    public int remainingCapacity() {
        return capacity - count.get();
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new Itr();
    }

    private class Itr implements Iterator<E> {
        /*
         * Basic weakly-consistent iterator.  At all times hold the next
         * item to hand out so that if hasNext() reports true, we will
         * still have it to return even if lost race with a take etc.
         */

        private Node<E> current;
        private Node<E> lastRet;
        private E currentElement;

        Itr() {
            allLock.lock();
            try {
                current = head.next;
                if (current != null){
                    currentElement = current.item;
                }
            } finally {
                allLock.unlock();
            }
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next live successor of p, or null if no such.
         *
         * Unlike other traversal methods, iterators need to handle both:
         * - dequeued nodes (p.next == p)
         * - (possibly multiple) interior removed nodes (p.item == null)
         */
        private Node<E> nextNode(Node<E> p) {
            for (;;) {
                Node<E> s = p.next;
                if (s == p){
                    return head.next;
                }
                if (s == null || s.item != null){
                    return s;
                }
                p = s;
            }
        }

        @Override
        public E next() {
            allLock.lock();
            try {
                if (current == null){
                    throw new NoSuchElementException();
                }
                E x = currentElement;
                lastRet = current;
                current = nextNode(current);
                currentElement = (current == null) ? null : current.item;
                return x;
            } finally {
                allLock.unlock();
            }
        }

        @Override
        public void remove() {
            if (lastRet == null){
                throw new IllegalStateException();
            }
            allLock.lock();
            try {
                Node<E> node = lastRet;
                lastRet = null;
                for (Node<E> trail = head, p = trail.next;
                     p != null;
                     trail = p, p = p.next) {
                    if (p == node) {
                        unlink(p);
                        break;
                    }
                }
            } finally {
                allLock.unlock();
            }
        }
    }

    void unlink(Node<E> p) {
        Node<E> pre = p.pre;
        Node<E> next = p.next;
        if(next !=null){
            next.pre = pre;
        }
        pre.next = next;
        p.pre = null;
        p.next = null;
        p.item = null;
        if (count.getAndDecrement() == capacity){
            notFull.signal();
        }
    }

    @Override
    public int size() {
        return count.get();
    }

    /**
     * Removes a node from head of queue.
     *
     * @return the node
     */
    private E dequeue() {
        // assert takeLock.isHeldByCurrentThread();
        // assert head.item == null;
        Node<E> h = head;
        Node<E> first = h.next;
        h.next = h; // help GC
        head = first;
        E x = first.item;
        first.item = null;
        return x;
    }

    /**
     * Links node at end of queue.
     *
     * @param node the node
     */
    private void enqueue(Node<E> node) {
        // assert putLock.isHeldByCurrentThread();
        // assert last.next == null;
        if(head.next == null){
            node.pre = head;
            head.next = node;
        }else{
            order(node,head.next);
        }
    }

    private void order(Node<E> s,Node<E> t){
        OrderObject to = (OrderObject)t.item;
        OrderObject so = (OrderObject)s.item;
        if((so.getPriority() < to.getPriority())){
            s.pre = t.pre;
            s.pre.next = s;
            t.pre = s;
            s.next = t;
        }else if(so.getPriority() >= to.getPriority()){
            if(t.next == null){
                t.next = s;
                s.pre = t;
            }else{
                order(s,t.next);
            }
        }
    }

    /**
     * Signals a waiting take. Called only from put/offer (which do not
     * otherwise ordinarily lock takeLock.)
     */
    private void signalNotEmpty() {
        final ReentrantLock takeLock = this.allLock;
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

    /**
     * Signals a waiting put. Called only from take/poll.
     */
    private void signalNotFull() {
        final ReentrantLock putLock = this.allLock;
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }

    @Override
    public boolean contains(Object o) {
        if (o == null){ return false;}
        allLock.lock();
        try {
            String oId = ((OrderObject)o).getId();
            for (Node<E> p = head.next; p != null; p = p.next){
                if (oId.equals(((OrderObject)p.item).getId())) {
                    return true;
                }
            }
            return false;
        } finally {
            allLock.unlock();
        }
    }

    public boolean remove(String sign) {
        if (StringUtils.isBlank(sign)) {return false;}
        try {
            allLock.lock();
            for (Node<E> trail = head, p = trail.next;
                 p != null;
                 trail = p, p = p.next) {
                 OrderObject oo = (OrderObject)p.item;
                 if (sign.equals(oo.getId())) {
                     unlink(p);
                     return true;
                }
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }finally {
            allLock.unlock();
        }
    }

    public IndexNode<E> getElement(String sign) {
        if (sign == null){ return null;}
        try {
            allLock.lockInterruptibly();
            int idx=0;
            for (Node<E> p = head.next; p != null; p = p.next){
                idx++;
                OrderObject oo = (OrderObject)p.item;
                if (sign.equals(oo.getId())) {
                    return new IndexNode<E>(p.item,idx);
                }
            }
            return null;
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        } finally {
            allLock.unlock();
        }
    }

    public E getIndexOrLast(int idx) {
        if (idx <= 0){ return null;}
        try {
            allLock.lockInterruptibly();
            int i = 1;
            for (Node<E> pre = head,p = pre.next;; pre=p,p = p.next,i++){
                if (i>=idx||p==null){
                    if (p != null){
                        return p.item;
                    } else {
                        return pre.item;
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        } finally {
            allLock.unlock();
        }
    }

    /**
     * 获取元素但是不移除
     * @return
     */
    public E getTop(){
        final AtomicInteger count = this.count;
        if (count.get() == 0){
            return null;
        }
        E x = null;
        final ReentrantLock takeLock = this.allLock;
        takeLock.lock();
        try {
            if (count.get() > 0) {
                Node<E> h = head;
                Node<E> first = h.next;
                x = first.item;
            }
        } finally {
            takeLock.unlock();
        }
        return x;
    }
}