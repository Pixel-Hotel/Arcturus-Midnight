package com.eu.habbo.habbohotel.items.interactions.invisible;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionInvisibleItem extends InteractionDefault {

    public InteractionInvisibleItem(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionInvisibleItem(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

}
