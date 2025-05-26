package com.eu.habbo.habbohotel.items.interactions.config;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionRollerSpeedController extends HabboItem {
    public InteractionRollerSpeedController(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionRollerSpeedController(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }

    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }
    @Override
    public boolean isWalkable() {
        return false;
    }
    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {}
    @Override
    public void onMove(Room room, RoomTile oldLocation, RoomTile newLocation) {
        super.onMove(room, oldLocation, newLocation);

        if(room.getItemsAt(oldLocation).stream().noneMatch(item -> item.getClass().isAssignableFrom(InteractionRoller.class))) {
            for (RoomUnit unit : room.getRoomUnits()) {
                if (!oldLocation.unitIsOnFurniOnTile(unit, this.getBaseItem()))
                    continue; // If the unit was previously on the furni...
                if (newLocation.unitIsOnFurniOnTile(unit, this.getBaseItem())) continue; // but is not anymore...

                try {
                    this.onWalkOff(unit, room, new Object[]{oldLocation, newLocation}); // the unit walked off!
                } catch (Exception ignored) {

                }
            }
        }
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        setRollerDelay(room);
    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        setRollerDelay(room);
    }

    private void setRollerDelay(Room room){
        if(room == null) return;
        room.setRollerSpeed(Integer.parseInt(getExtradata()));
    }
}
