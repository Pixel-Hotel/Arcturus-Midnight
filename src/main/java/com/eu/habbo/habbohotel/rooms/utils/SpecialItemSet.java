package com.eu.habbo.habbohotel.rooms.utils;

import com.eu.habbo.habbohotel.users.HabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArraySet;


public class SpecialItemSet<T extends HabboItem> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpecialItemSet.class);

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
        LOGGER.debug("Add item id: {}", item.getId());
        items.add(item);
    }

    public void remove(T item){
        if(isItemNull(item)) return;
        LOGGER.debug("Removed item id: {}", item.getId());
        items.remove(item);
    }

    private boolean isItemNull(T item){
        if(item == null){
            LOGGER.warn("Item was null!");
            return true;
        }
        return false;
    }
    public void dispose(){
        items.clear();
    }

}