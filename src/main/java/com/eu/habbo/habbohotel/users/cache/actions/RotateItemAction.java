package com.eu.habbo.habbohotel.users.cache.actions;

import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;

public class RotateItemAction implements ItemAction{

    private final Habbo habbo;
    private final HabboItem item;
    private final int oldRotation;
    private final int newRotation;

    public RotateItemAction(Habbo habbo, HabboItem item, int oldRotation, int newRotation) {
        this.habbo = habbo;
        this.item = item;
        this.oldRotation = oldRotation;
        this.newRotation = newRotation;
    }

    @Override
    public HabboItem getItem() {
        return this.item;
    }

    @Override
    public boolean redo() {
        return handle(oldRotation, newRotation);
    }

    @Override
    public boolean undo() {
        return handle(newRotation, oldRotation);
    }

    private boolean handle(int fromRotation, int toRotation){
        Room room = habbo.getRoomUnit().getRoom();
        FurnitureMovementError code = FurnitureMovementError.NONE;

        if(item.getBaseItem().getType() == FurnitureType.FLOOR) {
            room.moveFurniTo(this.item, room.getLayout().getTile(item.getX(), item.getY()), item.getRotation(), habbo, true, false, item.getZ(), true);
        }

        else if(item.getBaseItem().getType() == FurnitureType.WALL) {
            code = room.placeWallFurniAt(item, "", habbo);
        }
        return code == FurnitureMovementError.NONE;
    }
}
