package com.eu.habbo.habbohotel.users.cache.actions;

import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoveItemAction implements ItemAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(MoveItemAction.class);

    private final Habbo habbo;
    private final HabboItem item;
    private final RoomTile oldPostion;
    private final RoomTile newPosition;
    private final double oldZ;
    private final double newZ;

    public MoveItemAction(Habbo habbo, HabboItem item, RoomTile oldPostion, RoomTile newPosition, double z) {
        this.habbo = habbo;
        this.item = item;
        this.oldPostion = oldPostion;
        this.newPosition = newPosition;
        oldZ = z;
        newZ = item.getZ();

        LOGGER.debug("User: {} moved item: {} from position: {} z: {} to position: {} z: {}",
                habbo.getHabboInfo().getUsername(), item.getRoomId(), oldPostion, oldZ, newPosition, newZ);
    }

    @Override
    public HabboItem getItem(){
        return this.item;
    }

    @Override
    public boolean redo() {
        return handle(newPosition, newZ);
    }

    @Override
    public boolean undo() {
        return handle(oldPostion, oldZ);
    }

    private boolean handle(RoomTile position, double z){
        Room room = habbo.getRoomUnit().getRoom();
        FurnitureMovementError code = FurnitureMovementError.NONE;

        if(item.getBaseItem().getType() == FurnitureType.FLOOR) {
            room.moveFurniTo(this.item, position, item.getRotation(), habbo, true, false, z, true);
        }

        else if(item.getBaseItem().getType() == FurnitureType.WALL) {
            code = room.placeWallFurniAt(item, "", habbo);
        }
        return code == FurnitureMovementError.NONE;
    }
}
