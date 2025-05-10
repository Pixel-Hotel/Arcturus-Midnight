package com.eu.habbo.habbohotel.items.interactions.invisible;

import com.eu.habbo.habbohotel.items.Item;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionInvisibleClickItem extends InteractionInvisibleItem{
    public InteractionInvisibleClickItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionInvisibleClickItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

}
