package com.eu.habbo.habbohotel.rooms.utils;

import com.eu.habbo.habbohotel.users.HabboItem;

import java.util.concurrent.CopyOnWriteArraySet;


public class SpecialItemSet<T extends HabboItem> {
    private final CopyOnWriteArraySet<T> items = new CopyOnWriteArraySet<>();

    public CopyOnWriteArraySet<T> getItems() {
        return items;
    }

    public T get(int id){
        for(T item : items) {
            if(item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    public void add(T item){
        if(isItemNull(item)) return;
        items.add(item);
    }

    public void remove(T item){
        if(isItemNull(item)) return;
        items.remove(item);
    }

    private boolean isItemNull(T item){
        return item == null;
    }
    public void dispose(){
        items.clear();
    }

}