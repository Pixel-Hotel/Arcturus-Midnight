package com.eu.habbo.habbohotel.users.cache.actions;

import com.eu.habbo.habbohotel.items.FurnitureType;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.outgoing.inventory.InventoryRefreshComposer;
import com.eu.habbo.messages.outgoing.inventory.InventoryUpdateItemComposer;

public class PickUpItemAction implements ItemAction {

    private final Habbo habbo;
    private final HabboItem item;
    private final RoomTile position;
    private final double z;
    private final int rotation;
    private final String state;
    private final String wallPosition;

    public PickUpItemAction(Habbo habbo, HabboItem item, RoomTile position, double z, int rotation, String state, String wallPosition) {
        this.habbo = habbo;
        this.item = item;
        this.position = position;
        this.z = z;
        this.rotation = rotation;
        this.state = state;
        this.wallPosition = "";
    }

    public PickUpItemAction(Habbo habbo, HabboItem item, String wallPosition) {
        this.habbo = habbo;
        this.item = item;
        this.wallPosition = wallPosition;
        this.position = null;
        this.z = 0;
        this.rotation = 0;
        this.state = item.getExtradata() == null ? "" : item.getExtradata();
    }

    @Override
    public HabboItem getItem() {
        return item;
    }

    @Override
    public boolean redo() {
        //if(item.getRoomId() > 0) return false;
        habbo.getRoomUnit().getRoom().pickUpItem(item, habbo);
        //ServerMessage composer = item.getBaseItem().getType() == FurnitureType.FLOOR ? new RemoveFloorItemComposer(item).compose() : new RemoveWallItemComposer(item).compose();
        //habbo.getClient().sendResponse(composer);
        return true;

    }

    @Override
    public boolean undo() {
        if(item.getBaseItem().getType() == FurnitureType.FLOOR) return handleFloorItem();
        else if(item.getBaseItem().getType() == FurnitureType.WALL) return handleWallItem();
        return true;
    }

    private boolean handleFloorItem(){
        Room room = habbo.getRoomUnit().getRoom();
        FurnitureMovementError code = room.placeFloorFurniAt(item, position, rotation, habbo);
        if (code != FurnitureMovementError.NONE) return false;
        item.setRoomId(room.getId());
        item.setZ(z);
        item.setExtradata(state);
        updateItem(room);
        //habbo.getClient().sendResponse(new AddFloorItemComposer(item, habbo.getHabboInfo().getUsername()));
        habbo.getClient().sendResponse(new InventoryUpdateItemComposer(item));
        habbo.getClient().sendResponse(new InventoryRefreshComposer());
        return true;
    }

    private boolean handleWallItem(){
        Room room = habbo.getRoomUnit().getRoom();
        FurnitureMovementError code = room.placeWallFurniAt(item, wallPosition, habbo);
        if (code != FurnitureMovementError.NONE) return false;
        item.setWallPosition(wallPosition);
        updateItem(room);
        return true;
    }

    private void updateItem(Room room){
        item.needsUpdate(true);
        room.updateItem(item);
    }

}
