package com.eu.habbo.habbohotel.items.interactions.config;


import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;
import com.eu.habbo.habbohotel.rooms.Room;

import java.sql.ResultSet;
import java.sql.SQLException;


public class InteractionInvisibleItemController extends InteractionDefault {

    public InteractionInvisibleItemController(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionInvisibleItemController(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    @Override
    public void onClick(GameClient client, Room room, Object[] objects) throws Exception {
        super.onClick(client, room, objects);
        room.setHideInvisibleItems(this.getExtradata().equals("1"));
    }


}
