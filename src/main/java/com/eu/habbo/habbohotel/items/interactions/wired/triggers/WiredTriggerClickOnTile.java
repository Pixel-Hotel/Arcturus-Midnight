package com.eu.habbo.habbohotel.items.interactions.wired.triggers;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionWiredTrigger;
import com.eu.habbo.habbohotel.items.interactions.wired.WiredSettings;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomUnit;
import com.eu.habbo.habbohotel.wired.WiredTriggerType;
import com.eu.habbo.messages.ServerMessage;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WiredTriggerClickOnTile extends InteractionWiredTrigger {
    protected WiredTriggerClickOnTile(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    protected WiredTriggerClickOnTile(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void loadWiredData(ResultSet set, Room room) throws SQLException {

    }

    @Override
    public void onPickUp() {

    }

    @Override
    public void serializeWiredData(ServerMessage message, Room room) {

    }

    @Override
    public String getWiredData() {
        return "";
    }

    @Override
    public boolean execute(RoomUnit roomUnit, Room room, Object[] stuff) {
        return false;
    }

    @Override
    public boolean saveData(WiredSettings settings) {
        return false;
    }

    @Override
    public WiredTriggerType getType() {
        return null;
    }
}
