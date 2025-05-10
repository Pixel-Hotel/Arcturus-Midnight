package com.eu.habbo.habbohotel.items.interactions.config;

import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.items.interactions.InteractionDefault;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InteractionHanditemBlocker extends InteractionDefault {
    public InteractionHanditemBlocker(ResultSet set, Item baseItem) throws SQLException {
        super(set, baseItem);
    }

    public InteractionHanditemBlocker(int id, int userId, Item item, String extradata, int limitedStack, int limitedSells) {
        super(id, userId, item, extradata, limitedStack, limitedSells);
    }

    public boolean isDisabled(){
        return this.getExtradata().equals("1");
    }
}
