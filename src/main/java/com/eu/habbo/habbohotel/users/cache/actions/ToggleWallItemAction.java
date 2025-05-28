package com.eu.habbo.habbohotel.users.cache.actions;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class ToggleWallItemAction implements ItemAction{

    private final Habbo habbo;
    private final HabboItem item;
    private final String oldState;
    private final String newState;

    public ToggleWallItemAction(Habbo habbo, HabboItem item, String oldState, String newState) {
        this.habbo = habbo;
        this.item = item;
        this.oldState = oldState;
        this.newState = newState;
    }

    @Override
    public HabboItem getItem() {
        return item;
    }

    @Override
    public boolean redo() {
        return handle(newState);
    }

    @Override
    public boolean undo() {
        return handle(oldState);
    }

    private boolean handle(String state){
        item.setExtradata(state);
        item.needsUpdate(true);
        habbo.getRoomUnit().getRoom().updateItem(item);
        Emulator.getThreading().run(item);
        return true;
    }
}
