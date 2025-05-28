package com.eu.habbo.habbohotel.users.cache.actions;

import java.util.Objects;

public class ItemActionKey {

    private final int itemId;
    private final Class<? extends ItemAction> actionClass;

    public ItemActionKey(int itemId, Class<? extends ItemAction> actionClass) {
        this.itemId = itemId;
        this.actionClass = actionClass;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof ItemActionKey that)) return false;
        return this.itemId == that.itemId && this.actionClass == that.actionClass;
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, actionClass);
    }

}
