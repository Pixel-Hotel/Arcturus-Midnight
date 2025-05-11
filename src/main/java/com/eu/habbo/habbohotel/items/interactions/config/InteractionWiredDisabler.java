package com.eu.habbo.habbohotel.items.interactions.config;


import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionWiredDisabler extends InteractionDefault {

    public InteractionWiredDisabler(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionWiredDisabler(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public boolean isDisabled(){
        return this.getExtradata().equals("1");
    }

    @Override
    public void onPickUp(Room room) {
        setExtradata("0");
    }
}
