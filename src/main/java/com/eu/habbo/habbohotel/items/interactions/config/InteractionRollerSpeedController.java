package com.eu.habbo.habbohotel.items.interactions.config;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionRoller;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomLayout;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.Habbo;
import com.eu.habbo.habbohotel.users.HabboItem;
import com.eu.habbo.habbohotel.wired.WiredEffectType;
import com.eu.habbo.messages.ServerMessage;
import com.eu.habbo.messages.outgoing.MessageComposer;
import com.eu.habbo.messages.outgoing.rooms.items.ItemStateComposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class InteractionRollerSpeedController extends HabboItem {

    public static final Logger LOGGER = LoggerFactory.getLogger(InteractionRollerSpeedController.class);

    public InteractionRollerSpeedController(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
        setRollerSpeed(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
    }

    public InteractionRollerSpeedController(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
        setRollerSpeed(Emulator.getGameEnvironment().getRoomManager().getRoom(this.getRoomId()));
    }

    @Override
    public void serializeExtradata(ServerMessage serverMessage) {
        serverMessage.appendInt((this.isLimited() ? 256 : 0));
        serverMessage.appendString(this.getExtradata());

        super.serializeExtradata(serverMessage);
    }
    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {}
    @Override
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }
    @Override
    public boolean isWalkable() {
        return false;
    }
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
        if (room != null && (client == null || this.canToggle(client.getHabbo(), room) || (objects.length >= 2 && objects[1] instanceof WiredEffectType && objects[1] == WiredEffectType.TOGGLE_STATE))) {
            super.onClick(client, room, objects);

            if (objects != null && objects.length > 0) {
                if (objects[0] instanceof Integer) {

                    if (this.getExtradata().isEmpty())
                        this.setExtradata("0");

                    if (this.getBaseItem().getStateCount() > 0) {
                        // 12 zustände 0, 1-2 animation, 3, 4-5 animation, 6, 7-8 animation, 9, 10-11 animation
                        updateExtraData(room);


                    }
                }
            }

        }
        setRollerSpeed(room);
    }


    public boolean canToggle(Habbo habbo, Room room) {
        if (room.hasRights(habbo)) return true;

        if (!habbo.getHabboStats().isRentingSpace()) return false;

        HabboItem rentSpace = room.getHabboItem(habbo.getHabboStats().rentedItemId);

        return rentSpace != null && RoomLayout.squareInSquare(RoomLayout.getRectangle(rentSpace.getX(), rentSpace.getY(), rentSpace.getBaseItem().getWidth(), rentSpace.getBaseItem().getLength(), rentSpace.getRotation()), RoomLayout.getRectangle(this.getX(), this.getY(), this.getBaseItem().getWidth(), this.getBaseItem().getLength(), this.getRotation()));

    }

    @Override
    public void onPlace(Room room) {
        super.onPlace(room);
        setRollerSpeed(room);
    }

    private void setRollerSpeed(Room room){
        if(room == null) return;
        room.setRollerSpeed(Integer.parseInt(getExtradata()));
    }

    private void updateExtraData(Room room) {
        String oldData = this.getExtradata();
        switch (this.getExtradata()) {
            case "0", "1", "2" -> setExtradata("3");
            case "3", "4", "5" -> setExtradata("6");
            case "6", "7", "8" -> setExtradata("9");
            default -> setExtradata("0");
        }
        this.needsUpdate(true);
        room.updateItemState(this);
        String newData = this.getExtradata();
        LOGGER.debug("updateExtraData set's Extradata from {} to {}.", oldData, newData);
    }

    public MessageComposer handleAnimation(Room room) {
        String oldData = this.getExtradata();
        String newData;

        switch (oldData) {
            case "0", "2" -> newData = "1";
            case "1"      -> newData = "2";
            case "3", "5" -> newData = "4";
            case "4"      -> newData = "5";
            case "6", "8" -> newData = "7";
            case "7"      -> newData = "8";
            case "9", "11"-> newData = "10";
            case "10"     -> newData = "11";
            default -> {
                try {
                    int fallback = ((Integer.parseInt(oldData) / 3 + 1) % 4) * 3;
                    newData = String.valueOf(fallback);
                } catch (NumberFormatException e) {
                    newData = "0";
                }
            }
        }
        if(!List.of(new String[]{"0", "1", "2"}).contains(newData) )
            LOGGER.debug("handleAnimation set's Extradata from {} to {}.", oldData, newData);
        setExtradata(newData);
        return new ItemStateComposer(this);
    }
}
