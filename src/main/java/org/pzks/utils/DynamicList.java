package org.pzks.utils;

import java.util.*;

public class DynamicList implements DynamicObject, List<DynamicObject> {
    private final List<DynamicObject> dynamicElements = new ArrayList<>();

    @Override
    public int size() {
        return dynamicElements.size();
    }

    @Override
    public boolean isEmpty() {
        return dynamicElements.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return dynamicElements.contains(o);
    }

    @Override
    public Iterator<DynamicObject> iterator() {
        return dynamicElements.iterator();
    }

    @Override
    public Object[] toArray() {
        return dynamicElements.toArray(new DynamicObject[0]);
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return dynamicElements.toArray(a);
    }

    @Override
    public boolean add(DynamicObject dynamic) {
        return dynamicElements.add(dynamic);
    }

    @Override
    public boolean remove(Object o) {
        return dynamicElements.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return new HashSet<>(dynamicElements).containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends DynamicObject> c) {
        return dynamicElements.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends DynamicObject> c) {
        return dynamicElements.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return dynamicElements.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return dynamicElements.retainAll(c);
    }

    @Override
    public void clear() {
        dynamicElements.clear();
    }

    @Override
    public DynamicObject get(int index) {
        return dynamicElements.get(index);
    }

    @Override
    public DynamicObject set(int index, DynamicObject element) {
        return dynamicElements.set(index, element);
    }

    @Override
    public void add(int index, DynamicObject element) {
        dynamicElements.add(index, element);
    }

    @Override
    public DynamicObject remove(int index) {
        return dynamicElements.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return dynamicElements.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return dynamicElements.lastIndexOf(o);
    }

    @Override
    public ListIterator<DynamicObject> listIterator() {
        return dynamicElements.listIterator();
    }

    @Override
    public ListIterator<DynamicObject> listIterator(int index) {
        return dynamicElements.listIterator(index);
    }

    @Override
    public List<DynamicObject> subList(int fromIndex, int toIndex) {
        return dynamicElements.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DynamicList dynamics = (DynamicList) o;
        return Objects.equals(dynamicElements, dynamics.dynamicElements);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dynamicElements);
    }

    @Override
    public String toString() {
        return dynamicElements.toString();
    }
}
