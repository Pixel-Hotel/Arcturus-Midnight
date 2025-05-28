package com.eu.habbo.habbohotel.users.cache.actions;

import com.eu.habbo.habbohotel.users.HabboItem;

public interface ItemAction {

    HabboItem getItem();
    boolean redo();
    boolean undo();
}
