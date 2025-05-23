package com.eu.habbo.habbohotel.items.interactions.config;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.users.HabboItem;

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
    public boolean canWalkOn(RoomUnit roomUnit, Room room, Object[] objects) {
        return false;
    }
    @Override
    public boolean isWalkable() {
        return false;
    }
    @Override
    public void onWalk(RoomUnit roomUnit, Room room, Object[] objects) throws Exception {

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
