package org.pzks.parsers.systems.dataflow;

import java.util.*;

public class MemoryBank implements List<SystemOperation>, Cloneable {
    private List<SystemOperation> operations = new ArrayList<>();

    @Override
    public int size() {
        return operations.size();
    }

    @Override
    public boolean isEmpty() {
        return operations.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return operations.contains(o);
    }

    @Override
    public Iterator<SystemOperation> iterator() {
        return operations.iterator();
    }

    @Override
    public Object[] toArray() {
        return operations.toArray(new SystemOperation[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return operations.toArray(a);
    }

    @Override
    public boolean add(SystemOperation systemOperation) {
        return operations.add(systemOperation);
    }

    @Override
    public boolean remove(Object o) {
        return operations.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(operations).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends SystemOperation> c) {
        return operations.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends SystemOperation> c) {
        return operations.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return operations.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return operations.retainAll(c);
    }

    @Override
    public void clear() {
        operations.clear();
    }

    @Override
    public SystemOperation get(int index) {
        return operations.get(index);
    }

    @Override
    public SystemOperation set(int index, SystemOperation element) {
        return operations.set(index, element);
    }

    @Override
    public void add(int index, SystemOperation element) {
        operations.add(index, element);
    }

    @Override
    public SystemOperation remove(int index) {
        return operations.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return operations.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return operations.lastIndexOf(o);
    }

    @Override
    public ListIterator<SystemOperation> listIterator() {
        return operations.listIterator();
    }

    @Override
    public ListIterator<SystemOperation> listIterator(int index) {
        return operations.listIterator(index);
    }

    @Override
    public List<SystemOperation> subList(int fromIndex, int toIndex) {
        return operations.subList(fromIndex, toIndex);
    }

    @Override
    public MemoryBank clone() throws CloneNotSupportedException {
        MemoryBank clone = (MemoryBank) super.clone();
        clone.operations = new ArrayList<>(operations);
        return clone;
    }
}
