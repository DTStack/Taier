package com.dtstack.engine.master.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

/**
 * @author yuebai
 * @date 2020-07-13
 */
public class SortedArrayList<E> extends ArrayList<E> {
    protected final Comparator<E> comparator;

    public SortedArrayList(Comparator<E> c) {
        this.comparator = c;
    }

    public SortedArrayList() {
        this.comparator = null;
    }

    public SortedArrayList(Collection<? extends E> c) {
        this.comparator = null;
        this.addAll(c);
    }

    public Comparator getComparator() {
        return this.comparator;
    }

    public boolean add(E o) {
        int idx = 0;
        if (!this.isEmpty()) {
            idx = this.findInsertionPoint(o);
        }

        super.add(idx, o);
        return true;
    }

    public boolean addAll(Collection<? extends E> c) {
        Iterator<? extends E> i = c.iterator();
        boolean changed = false;

        while(i.hasNext()) {
            boolean ret = this.add(i.next());
            if (!changed) {
                changed = ret;
            }
        }

        return changed;
    }

    public int findInsertionPoint(E o) {
        return this.findInsertionPoint(o, 0, this.size() - 1);
    }

    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    protected int compare(E k1, E k2) {
        return this.comparator == null ? ((Comparable)k1).compareTo(k2) : this.comparator.compare(k1, k2);
    }

    protected int findInsertionPoint(E o, int low, int high) {
        while(low <= high) {
            int mid = low + high >>> 1;
            int delta = this.compare(this.get(mid), o);
            if (delta > 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }

        return low;
    }
}
