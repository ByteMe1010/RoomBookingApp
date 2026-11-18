package com.codehack.nngf.Core;

public abstract class Observer<T> {
    protected NComponet parent;

    public Observer(NComponet attachedObj) {
        parent = attachedObj;
    }

    public abstract void set(T value);

    public abstract T get();
}