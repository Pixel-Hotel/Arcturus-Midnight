package com.eu.habbo.habbohotel.rooms.utils;

import com.eu.habbo.habbohotel.users.HabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class SpezialItemMap<K, V extends HabboItem> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpezialItemMap.class);
    private final ConcurrentHashMap<K, CopyOnWriteArraySet<V>> items = new ConcurrentHashMap<>();


    public void add(K key, V item){
        if(isItemNull(item)) return;
        LOGGER.debug("Adding item instance of {} with id: {} to position: {}", item.getId(), item.getClass().getSimpleName(), key);
        items.computeIfAbsent(key, k -> new CopyOnWriteArraySet<>()).add(item);
    }

    public void remove(K key, V item){
        if(isItemNull(item)) return;
        CopyOnWriteArraySet<V> set = items.get(key);
        if(set != null) {
            if(set.remove(item)) {
                LOGGER.debug("Removed item id: {} from position: {}", item.getId(), key);
                if (set.isEmpty()) {
                    LOGGER.debug("Removed position: {} from {}", key, item.getClass().getSimpleName());
                    items.remove(key);
                }
            }
        }
    }

    public void changeKey(V item, K oldKey, K newKey){
        if(isItemNull(item)) return;
        LOGGER.debug("Changing position of item with id: {}.", item.getId());
        remove(oldKey, item);
        add(newKey, item);
    }

    public Set<V> getItemByKey(K key) {
        CopyOnWriteArraySet<V> set = items.get(key);
        return (set != null) ? set : Collections.emptySet();
    }

    public V getItemById(int id) {
        for(CopyOnWriteArraySet<V> set : items.values()) {
            for(V item : set) {
                if(item.getId() == id) {
                    return item;
                }
            }
        }
        return null;
    }


    private boolean isItemNull(V item){
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
